package Main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;

import org.xbill.DNS.*;

public class Socks5Proxy {

    private static final byte SOCKS_VERSION = 0x05;
    private static final byte IPv4 = 0x01;
    private static final byte DOMAIN_NAME = 0x03;

    private static final byte REQUEST_GRANTED = 0x00;
    private static final byte GENERAL_FAILURE = 0x01;
    private static final byte CONNECTION_REFUSED_BY_DESTINATION_HOST = 0x05;
    private static final byte COMMAND_NOT_SUPPORTED = 0x07;
    private static final byte ADDRESS_TYPE_NOT_SUPPORTED = 0x08;

    private static final byte[] NO_AUTHENTICATION = new byte[] {0x05, 0x00};

    private static final byte ESTABLISH_STREAM_CONNECTION = 0x01;

    private static final int DNSPORT = 53;


    private final Selector selector;
    private final InetSocketAddress localSocket;

    DatagramChannel DNSChannel;
    HashMap<Integer, SelectionKey> DNSMap = new HashMap<>();
    String DNSServer = ResolverConfig.getCurrentConfig().server(); //getting address of recursive resolver


    public Socks5Proxy(int port)throws IOException {
        selector = Selector.open();
        localSocket = new InetSocketAddress(port);
    }

    public void start() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(localSocket);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        DNSChannel = DatagramChannel.open();
        DNSChannel.configureBlocking(false);
        DNSChannel.connect(new InetSocketAddress(DNSServer, DNSPORT));
        SelectionKey DNSKey = DNSChannel.register(selector, SelectionKey.OP_READ);

        while (!Thread.interrupted()) {
            int num = selector.select();

            if (num == 0)
                continue;

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                try {

                    if (key.isValid() && key.isReadable() && key == DNSKey) {
                        resolveDNS();
                        continue;
                    }

                    if (key.isValid() && key.isAcceptable()) {
                        accept(key);
                        continue;
                    }

                    if (key.isValid() && key.isConnectable()) {
                        connect(key);
                        continue;
                    }

                    if (key.isValid() && key.isReadable()) {
                        read(key);
                    }

                    if (key.isValid() && key.isWritable()) {
                        write(key);
                    }

                } catch (IOException e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                } finally {
                    iterator.remove();
                }
            }
        }
    }

    private void accept(SelectionKey key) {
        try {
            SocketChannel clientSocketChannel = ((ServerSocketChannel) key.channel()).accept();
            clientSocketChannel.configureBlocking(false);
            System.out.println("accept connection from " + clientSocketChannel.getRemoteAddress());

            clientSocketChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void connect(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        Attachment attachment = (Attachment) key.attachment();
        try {
            socketChannel.finishConnect();
            attachment.getIn().put(createServerResponse(key, REQUEST_GRANTED)).flip();
            attachment.setOut(((Attachment) attachment.getPeer().attachment()).getIn());
            ((Attachment) attachment.getPeer().attachment()).setOut(attachment.getIn());
            attachment.getPeer().interestOps(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
        } catch (Exception e) {
            System.out.println("connection refused");
            close(key);
        }
    }

    private void read(SelectionKey key) throws IOException {

        if (key.attachment() == null) {
            key.attach(new Attachment());
        }
        Attachment attachment = (Attachment) key.attachment();
        SocketChannel socketChannel = (SocketChannel) key.channel();
        try {
            int a = 0;
            if ((a = socketChannel.read(attachment.getIn())) < 0) {
                close(key);
            } else if (a > 0){
                if (attachment.getPeer() == null)
                    authenticate(key);
                else {
                    if (!((Attachment) key.attachment()).getPeer().isValid())
                        return;
                    attachment.getPeer().interestOps(attachment.getPeer().interestOps() | SelectionKey.OP_WRITE);
                    key.interestOps(key.interestOps() ^ SelectionKey.OP_READ);
                    attachment.getIn().flip();
                }
            }
        } catch (IOException e) {
            System.out.println("connection refused");
            close(key);
        }
    }

    private void write(SelectionKey key) throws IOException {
        Attachment attachment = (Attachment) key.attachment();
        SocketChannel socketChannel = (SocketChannel) key.channel();
        try {
            int a = 0;
            if ((a = socketChannel.write(attachment.getOut())) == -1) {
                close(key);
            }
            else if (attachment.getOut().remaining() == 0) {
                attachment.getOut().clear();
                key.interestOps(SelectionKey.OP_READ);
                if (a > 0 && attachment.getPeer() != null)
                    attachment.getPeer().interestOps(attachment.getPeer().interestOps() | SelectionKey.OP_READ);
            }
        } catch (IOException e) {
            System.out.println("connection refused");
            close(key);
        }
    }

    private void close(SelectionKey key) throws IOException {
        key.cancel();
        key.channel().close();
        if (!key.isValid())
            return;
        if (((Attachment) key.attachment()).getPeer() != null) {
            SelectionKey peerKey = ((Attachment) key.attachment()).getPeer();
            ((Attachment) key.attachment()).setPeer(null);
            peerKey.cancel();
            peerKey.channel().close();
        }
    }

    private void authenticate(SelectionKey key) {
        Attachment attachment = (Attachment)key.attachment();
        byte[] in = attachment.getIn().array();
        if (!attachment.isHandshakeCommitted()) {
            if (in[0] != SOCKS_VERSION || in[1] == 0x00) { //no authentication methods supported
                attachment.getOut().put(createServerResponse(key, COMMAND_NOT_SUPPORTED)).flip();
                key.interestOps(SelectionKey.OP_WRITE);
                return;
            }
            for (int i = 0; i < in[1]; ++i) {
                if (in[i + 2] == 0) {
                    attachment.getOut().put(NO_AUTHENTICATION).flip();
                    attachment.getIn().clear();
                    key.interestOps(SelectionKey.OP_WRITE);
                    attachment.setHandshakeCommitted(true);
                    return;
                }
            }
        }

        if (in[0] != SOCKS_VERSION || in[1] != ESTABLISH_STREAM_CONNECTION || attachment.getIn().position() < 9) {
            attachment.getOut().put(createServerResponse(key, COMMAND_NOT_SUPPORTED)).flip();
            key.interestOps(SelectionKey.OP_WRITE);
        } else {
            int port = ((0xFF & in[attachment.getIn().position()-2]) << 8)+(0xFF & in[attachment.getIn().position()-1]);
            attachment.setPort(port);
            if (in[3] == IPv4) {
                byte[] address = new byte[] {in[4], in[5], in[6], in[7]};
                try {
                    InetAddress addr = InetAddress.getByAddress(address);
                    attachment.setAddress(addr);
                    createPeer(key);
                    System.out.println("redirected to " + addr.getHostName() + ":" + port);
                } catch (Exception e) {
                    key.interestOps(SelectionKey.OP_WRITE);
                    return;
                }
            } else if (in[3] == DOMAIN_NAME) {
                int nameLength = in[4];
                char[] address = new char[nameLength];
                for (int i = 0; i < nameLength; ++i)
                    address[i] = (char)in[i + 5];

                String domainName = String.valueOf(address) + ".";
                try {
                    Name name = Name.fromString(domainName);
                    Record record = Record.newRecord(name, Type.A, DClass.IN);
                    Message message = Message.newQuery(record);
                    DNSChannel.write(ByteBuffer.wrap(message.toWire()));
                    DNSMap.put(message.getHeader().getID(), key);
                } catch (IOException e) {
                    attachment.getOut().put(createServerResponse(key, GENERAL_FAILURE)).flip();
                    key.interestOps(SelectionKey.OP_WRITE);
                    return;
                }
            } else {
                key.interestOps(SelectionKey.OP_WRITE);
                return;
            }
            attachment.getIn().clear();
        }
    }

    private byte[] createServerResponse(SelectionKey key, byte status) {
        byte[] response = new byte[10];
        response[0] = SOCKS_VERSION;
        response[1] = status;
        response[3] = IPv4;
        SocketChannel socketChannel = (SocketChannel) key.channel();

        try {
            String address = ((InetSocketAddress) socketChannel.getRemoteAddress()).getAddress().getHostAddress();
            int port = ((InetSocketAddress) socketChannel.getRemoteAddress()).getPort();
            String[] strs = address.split("\\.");
            for (int i = 4; i < 8; ++i)
                response[i] = (byte)(Integer.parseInt(strs[i-4]));
            response[8] = (byte)(port >> 8); //big endian
            response[9] = (byte)(port & 0xFF);
        } catch (Exception e) {
            System.out.println("could not get socket inetAddr");
            response[1] = GENERAL_FAILURE;
        }
        return response;
    }

    private void createPeer(SelectionKey key) throws IOException {
        try {
            Attachment attachment = (Attachment) key.attachment();
            SocketChannel peer = SocketChannel.open();
            peer.configureBlocking(false);
            peer.connect(new InetSocketAddress(attachment.getAddress(), attachment.getPort()));
            SelectionKey peerKey = peer.register(key.selector(), SelectionKey.OP_CONNECT);
            if (!key.isValid())
                return;
            key.interestOps(0);
            attachment.setPeer(peerKey);
            Attachment peerAttachment = new Attachment();
            peerAttachment.setPeer(key);
            peerKey.attach(peerAttachment);
        } catch (IOException e) {
            System.out.println("could not create peer");
            close(key);
        }
    }

    private void resolveDNS() throws IOException{
        ByteBuffer buf = ByteBuffer.allocate(1024);
        if (DNSChannel.read(buf) <= 0)
            return;
        Message message = new Message(buf.array());
        Record[] records = message.getSectionArray(1);
        for (Record record : records) {
            if (record instanceof ARecord) {
                ARecord aRecord = (ARecord) record;
                int id = message.getHeader().getID();
                SelectionKey key = DNSMap.get(id);
                if (key == null)
                    continue;
                Attachment attachment = (Attachment) key.attachment();
                attachment.setAddress(aRecord.getAddress());
                System.out.println("dns resolved : " + aRecord.getAddress() + " " + attachment.getPort());
                createPeer(key);
                return;
            }
        }
    }
}

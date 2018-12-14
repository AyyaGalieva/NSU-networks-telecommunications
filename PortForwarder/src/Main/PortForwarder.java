package Main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class PortForwarder {
    private final Selector selector;
    private final InetSocketAddress localSocket;
    private final InetSocketAddress remoteSocket;

    public PortForwarder(int lport, InetAddress rhost, int rport) throws IOException{
            selector = Selector.open();
            localSocket = new InetSocketAddress(lport);
            remoteSocket = new InetSocketAddress(rhost, rport);

            if (remoteSocket.isUnresolved()) {
                System.err.println("remote address is unresolved");
                System.exit(1);
            }
    }

    public void start() throws IOException{
        System.out.println("port forwarder started");
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(localSocket);
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (!Thread.interrupted()) {
            int num = selector.select();

            if (num == 0)
                continue;

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                if (!key.isValid()) {
                    iterator.remove();
                    continue;
                }

                try {
                    if (key.isAcceptable()) {
                        accept(key);
                        continue;
                    }

                    if (key.isConnectable()) {
                        connect(key);
                        continue;
                    }

                    if (key.isReadable()) {
                        int bytesRead = read(key);
                        if (bytesRead == -1) continue;
                    }

                    if (key.isWritable())
                        write(key);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                } finally {
                    iterator.remove();
                }
            }
        }
    }

    private void accept(SelectionKey key) {
        RoutingMember client = null;
        RoutingMember remote = null;
        try {
            SocketChannel clientSocketChannel = ((ServerSocketChannel) key.channel()).accept();
            clientSocketChannel.configureBlocking(false);
            System.out.println("accept connection from " + clientSocketChannel.getRemoteAddress() + " try to connect to " + remoteSocket.getAddress() + ":" + remoteSocket.getPort());

            SocketChannel remoteSocketChannel = SocketChannel.open();
            remoteSocketChannel.configureBlocking(false);
            remoteSocketChannel.connect(remoteSocket);

            remote = new RoutingMember(remoteSocketChannel, selector);
            client = new RoutingMember(clientSocketChannel, selector);

            client.setOppositeMember(remote);
            remote.setOppositeMember(client);

            remoteSocketChannel.register(selector, SelectionKey.OP_CONNECT, remote);
            clientSocketChannel.register(selector, 0, client);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            try {
                if (client.getSocketChannel() != null)
                    client.getSocketChannel().close();
                if (remote != null && remote.getSocketChannel()!= null)
                    remote.getSocketChannel().close();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    private void connect(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        RoutingMember member = (RoutingMember) key.attachment();

        try {
            if (socketChannel.finishConnect()) {
                System.out.println("connected to " + remoteSocket.getAddress() + ":" + remoteSocket.getPort());
                member.removeOps(SelectionKey.OP_CONNECT);
                member.addOps(SelectionKey.OP_READ);
                member.getOppositeMember().addOps(SelectionKey.OP_READ);
            } else
                System.err.println("can't connect to " + remoteSocket.getAddress());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            try {
                member.getOppositeMember().getSocketChannel().close();
                member.getSocketChannel().close();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    private int read(SelectionKey key) throws IOException {
        RoutingMember member = (RoutingMember)key.attachment();
        return member.read();
    }

    private void write(SelectionKey key) throws IOException {
        RoutingMember member = (RoutingMember)key.attachment();
        member.write();
    }
}

package Main;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class RoutingMember {
    private SocketChannel socketChannel;
    private Selector selector;
    private ByteBuffer buf;
    private RoutingMember oppositeMember;
    private boolean isOutputShutdown = false;
    private boolean finishReading = false;

    private static final int BUFF_SIZE = 4096;
    private static final int EOF = -1;

    public RoutingMember(SocketChannel socketChannel, Selector selector) {
        this.socketChannel = socketChannel;
        this.selector = selector;
        this.buf = ByteBuffer.allocate(BUFF_SIZE);
    }

    public int read() throws IOException{
        int bytesRead = socketChannel.read(buf);

        if (bytesRead > 0 && oppositeMember.socketChannel.isConnected())
            oppositeMember.addOps(SelectionKey.OP_WRITE);

        if (bytesRead == EOF) {
            removeOps(SelectionKey.OP_READ);
            finishReading = true;
            if (buf.position() == 0) {
                oppositeMember.getSocketChannel().shutdownOutput();
                oppositeMember.isOutputShutdown = true;
                if (isOutputShutdown || oppositeMember.buf.position()==0) {
                    close();
                    oppositeMember.close();
                }
            }
        }

        if (!buf.hasRemaining()) {
            removeOps(SelectionKey.OP_READ);
        }
        if (bytesRead > 0)
        System.out.println("read "+bytesRead+" bytes from " + socketChannel.getRemoteAddress());
        return bytesRead;
    }

    public void write() throws IOException{
        oppositeMember.buf.flip();
        int bytesWrite = socketChannel.write(oppositeMember.buf);

        if (bytesWrite > 0) {
            oppositeMember.buf.compact();
            oppositeMember.addOps(SelectionKey.OP_READ);
        }

        if (oppositeMember.buf.position() == 0) {
            removeOps(SelectionKey.OP_WRITE);
            if (oppositeMember.finishReading) {
                socketChannel.shutdownOutput();
                isOutputShutdown = true;
                if (oppositeMember.isOutputShutdown) {
                    close();
                    oppositeMember.close();
                }
            }
        }
        if (bytesWrite > 0)
        System.out.println("sent "+bytesWrite+" bytes to "+socketChannel.getRemoteAddress());
    }

    public void addOps(int ops) {
        SelectionKey key = this.socketChannel.keyFor(selector);
        key.interestOps(key.interestOps() | ops);
    }

    public void removeOps(int ops) {
        SelectionKey key = this.socketChannel.keyFor(selector);
        key.interestOps(key.interestOps() & (~ops));
    }

    private void close() throws IOException {
        socketChannel.close();
        socketChannel.keyFor(selector).cancel();
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setOppositeMember(RoutingMember oppositeMember) {
        this.oppositeMember = oppositeMember;
    }

    public RoutingMember getOppositeMember() {
        return oppositeMember;
    }
}

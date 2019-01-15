package Main;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

public class Attachment {
    private static final int BUFF_SIZE = 8192;
    private ByteBuffer in;
    private ByteBuffer out;
    private SelectionKey peer;
    private InetAddress address;
    private int port;
    private boolean handshakeCommitted = false;

    public Attachment() {
        in = ByteBuffer.allocate(BUFF_SIZE);
        out = ByteBuffer.allocate(BUFF_SIZE);
    }

    public ByteBuffer getIn() {
        return in;
    }

    public ByteBuffer getOut() {
        return out;
    }

    public void setOut(ByteBuffer out) {
        this.out = out;
    }

    public SelectionKey getPeer() {
        return peer;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPeer(SelectionKey peer) {
        this.peer = peer;
    }

    public boolean isHandshakeCommitted() {
        return handshakeCommitted;
    }

    public void setHandshakeCommitted(boolean handshakeCommitted) {
        this.handshakeCommitted = handshakeCommitted;
    }
}

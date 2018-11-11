import java.net.InetAddress;

public class Packet {
    private Message message;
    private AddrPort destination;

    Packet(Message message, InetAddress address, int port) {
        this.message = message;
        this.destination = new AddrPort(address, port);
    }

    public Message getMessage() {
        return message;
    }

    public InetAddress getAddress() {
        return destination.getAddress();
    }

    public int getPort() {
        return destination.getPort();
    }
}

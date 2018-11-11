import java.net.InetAddress;

public class AddrPort {
    private InetAddress address;
    private  int port;

    AddrPort(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}

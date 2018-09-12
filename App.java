import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class App {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("a multicast address required");
            System.exit(1);
        }

        InetAddress address = null;
        try{
            address = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            System.err.println("Unknown address");
            System.exit(1);
        }

        byte[] data = "message".getBytes();
        int port = 5445;

        DatagramSocket sender = null;
        MulticastSocket receiver = null;
        try{
            sender = new DatagramSocket();
            receiver = new MulticastSocket(port);
            receiver.joinGroup(address);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        new Thread(new Receiver(receiver, data)).start();
        new Thread(new Sender(address, sender, port, data)).start();
    }
}

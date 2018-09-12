import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Sender implements Runnable {
    private InetAddress address;
    private DatagramSocket socket;
    private int port;
    private byte[] data;

    public Sender(InetAddress address, DatagramSocket socket, int port, byte[] data) {
        this.address = address;
        this.socket = socket;
        this.port = port;
        this.data = data;
    }

    @Override
    public void run() {
        System.out.println("Sender id running");
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, port);
        while(true) {
            try {
                socket.send(sendPacket);
                Thread.sleep(1000);
            } catch (IOException e) {
                System.out.println("Failed sending packet: " + e);
            } catch (InterruptedException e) {
            }
        }
    }
}

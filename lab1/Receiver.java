import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;

public class Receiver implements Runnable {
    private DatagramSocket socket;
    private byte[] data;
    private HashMap<String, LocalTime> copies;

    public Receiver(DatagramSocket socket, byte[] data) {
        this.socket = socket;
        this.copies = new HashMap<>();
        this.data = data;
    }

    @Override
    public void run() {
        System.out.println("Receiver is running");
        byte[] buf = new byte[data.length];
        DatagramPacket recvPacket = new DatagramPacket(buf, buf.length);

        while(true) {
            try {
                socket.receive(recvPacket);
                if (Arrays.equals(data, recvPacket.getData())) {
                    addCopy(recvPacket.getAddress().toString() + ":" + recvPacket.getPort());
                }
                copies.entrySet().removeIf(it -> {
                   if (LocalTime.now().getSecond() - it.getValue().getSecond() > 5) {
                       System.out.println(it.getKey() + " is dead");
                       return true;
                   }
                   return false;
                });
            } catch (IOException e) {
                System.out.println("Failed receiving packet: " + e);
            }
        }
    }

    private void addCopy(String str) {
        if (copies.put(str, LocalTime.now()) == null)
            System.out.println("added new copy on " + str);
    }
}

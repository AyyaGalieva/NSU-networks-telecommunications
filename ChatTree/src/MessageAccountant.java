import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.time.LocalTime;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageAccountant extends TimerTask {
    private NodeInfo node;
    private ConcurrentLinkedQueue<Packet> packets;

    public MessageAccountant(NodeInfo node) {
        this.node = node;
        packets = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void run() {
        try {
            for (Packet packet : packets) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(packet.getMessage());
                DatagramPacket datagramPacket = new DatagramPacket(baos.toByteArray(), baos.toByteArray().length, packet.getAddress(), packet.getPort());
                node.getSocket().send(datagramPacket);
            }
            checkPing();
            ping();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addMessage(Message message, InetAddress address, int port) {
        packets.add(new Packet(message, address, port));
    }

    public void removeMessage(int guid, InetAddress address, int port) {
        for (Packet packet: packets) {
            if (packet.getAddress().equals(address) && packet.getPort() == port && packet.getMessage().getGuid() == guid) {
                packets.remove(packet);
            }
        }
    }

    private void ping() throws IOException {
        Message msg = new Message("I am still alive", node.getName());
        msg.setType(MessageType.PING);
        if (node.hasParent()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(msg);
            DatagramPacket packet = new DatagramPacket(baos.toByteArray(), baos.toByteArray().length, node.getParentAddress(), node.getParentPort());
            node.getSocket().send(packet);
        }
        ConcurrentLinkedQueue<AddrPort> children = node.getChildren();
        for (AddrPort addrPort : children) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(msg);
            DatagramPacket packet = new DatagramPacket(baos.toByteArray(), baos.toByteArray().length, addrPort.getAddress(), addrPort.getPort());
            node.getSocket().send(packet);
        }
    }

    private void checkPing() {
        ConcurrentHashMap<AddrPort, LocalTime> relatives = node.getRelatives();
        for (AddrPort addrPort : node.getRelatives().keySet()) {
            if (LocalTime.now().getSecond() - relatives.get(addrPort).getSecond() > 10)
                node.forgetRelative(addrPort.getAddress(), addrPort.getPort());
        }
    }
}

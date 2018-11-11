import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Sender implements Runnable {
    private NodeInfo node;
    private DatagramSocket socket;
    private MessageAccountant messageAccountant;

    Sender(NodeInfo node, MessageAccountant messageAccountant) {
        this.node = node;
        this.messageAccountant = messageAccountant;
        socket = node.getSocket();
    }

    @Override
    public void run() {
        Message msg;
        ConcurrentLinkedQueue<AddrPort> children = node.getChildren();
        Scanner sc = new Scanner(System.in);
        try {
            if (!node.isRoot()) {
                msg = new Message("pls adopt me", node.getName());
                msg.setType(MessageType.ADOPTION_REQUEST);
                send(msg, node.getParentAddress(), node.getParentPort());
                messageAccountant.addMessage(msg, node.getParentAddress(), node.getParentPort());
            }

            while(true) {
                msg = new Message(sc.nextLine(), node.getName());
                msg.setType(MessageType.CHAT_MESSAGE);
                node.addMessage(msg, false);
                if (node.hasParent()) {
                    send(msg, node.getParentAddress(), node.getParentPort());
                    messageAccountant.addMessage(msg, node.getParentAddress(), node.getParentPort());
                }
                for (AddrPort addrPort: children) {
                    send(msg, addrPort.getAddress(), addrPort.getPort());
                    messageAccountant.addMessage(msg, addrPort.getAddress(), addrPort.getPort());
                }

            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void send(Message message, InetAddress address, int port) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(message);
        DatagramPacket packet = new DatagramPacket(baos.toByteArray(), baos.toByteArray().length, address, port);
        socket.send(packet);
    }
}

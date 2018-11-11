import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Receiver implements Runnable {
    private NodeInfo node;
    private DatagramSocket socket;
    private DatagramPacket datagramPacket;
    private MessageAccountant messageAccountant;

    Receiver(NodeInfo node, MessageAccountant messageAccountant) {
        this.node = node;
        socket = node.getSocket();
        this.messageAccountant = messageAccountant;
    }

    @Override
    public void run() {
        //System.out.println("Receiver is running");
        Message msg;
        try {
            while (true) {
                msg = receive();
                double flag = Math.random() * 100;
                if (!node.isRelative(datagramPacket.getAddress(), datagramPacket.getPort()) && msg.getType() != MessageType.ADOPTION_REQUEST &&msg.getType() != MessageType.ADOPTION_CONSENT || flag >= node.getLossPercentage() && msg.getType() == MessageType.CHAT_MESSAGE) {
                    System.out.println("message was ignored "+msg.getType());
                    continue;
                }
                switch (msg.getType()) {
                    case ADOPTION_REQUEST:
                        node.addChild(datagramPacket.getAddress(), datagramPacket.getPort());
                        answer(msg, MessageType.ADOPTION_CONSENT);
                        break;
                    case ADOPTION_CONSENT:
                        messageAccountant.removeMessage(msg.getGuid(), datagramPacket.getAddress(), datagramPacket.getPort());
                        node.setParent(true);
                        break;
                    case CHAT_MESSAGE:
                        node.addMessage(msg, true);
                        answer(msg, MessageType.CHAT_MESSAGE_ACKNOWLEDGE);
                        sendNext(msg);
                        break;
                    case CHAT_MESSAGE_ACKNOWLEDGE:
                        messageAccountant.removeMessage(msg.getGuid(), datagramPacket.getAddress(), datagramPacket.getPort());
                        break;
                    case PING:
                        node.updateRelatives(datagramPacket.getAddress(), datagramPacket.getPort());
                    default:
                        break;

                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    private Message receive() throws IOException, ClassNotFoundException {
        datagramPacket = new DatagramPacket(new byte[2048], 2048);
        socket.receive(datagramPacket);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(datagramPacket.getData()));
        return (Message) ois.readObject();
    }

    private void answer(Message message, MessageType type) throws IOException {
        MessageType oldType = message.getType();
        message.setType(type);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(message);
        DatagramPacket packet = new DatagramPacket(baos.toByteArray(), baos.toByteArray().length, datagramPacket.getAddress(), datagramPacket.getPort());
        socket.send(packet);
        message.setType(oldType);
    }

    private void sendNext(Message message) throws IOException {
        ConcurrentLinkedQueue<AddrPort> children = node.getChildren();
        if (node.hasParent() && (!node.getParentAddress().equals(datagramPacket.getAddress()) || node.getParentPort() != datagramPacket.getPort())) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(message);
            DatagramPacket packet = new DatagramPacket(baos.toByteArray(), baos.toByteArray().length, node.getParentAddress(), node.getParentPort());
            socket.send(packet);
            messageAccountant.addMessage(message, node.getParentAddress(), node.getParentPort());
        }
        for (AddrPort addrPort : children) {
            if (!addrPort.getAddress().equals(datagramPacket.getAddress()) || addrPort.getPort() != datagramPacket.getPort()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(message);
                DatagramPacket packet = new DatagramPacket(baos.toByteArray(), baos.toByteArray().length, addrPort.getAddress(), addrPort.getPort());
                socket.send(packet);
                messageAccountant.addMessage(message, addrPort.getAddress(), addrPort.getPort());
            }
        }
    }
}

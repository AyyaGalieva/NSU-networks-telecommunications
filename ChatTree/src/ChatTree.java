import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;

public class ChatTree {
    public static void main(String[] args) {
        if (args.length != 3 && args.length != 5) {
            System.out.println("usage: node_name, loss_percentage, port [, parent_address, parent_port]");
            System.exit(1);
        }

        try{
            NodeInfo node = new NodeInfo(args);
            MessageAccountant messageAccountant = new MessageAccountant(node);
            new Thread(new Receiver(node, messageAccountant)).start();
            new Thread(new Sender(node, messageAccountant)).start();
            new Timer(true).schedule(messageAccountant, 3000, 3000);
        } catch (UnknownHostException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (SocketException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}

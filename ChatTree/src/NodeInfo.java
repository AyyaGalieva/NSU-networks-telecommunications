import java.net.*;
import java.time.LocalTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NodeInfo {
    private String name;
    private int lossPercentage;
    private int port;
    private boolean root;
    private InetAddress parentAddress;
    private int parentPort;
    private boolean parent;
    private ConcurrentLinkedQueue<Message> messages;
    private ConcurrentLinkedQueue<AddrPort> children;
    private ConcurrentHashMap<AddrPort, LocalTime> relatives;
    private DatagramSocket socket;

    NodeInfo(String[] args) throws UnknownHostException, SocketException {
        name = args[0];
        lossPercentage = Integer.valueOf(args[1]);
        port = Integer.valueOf(args[2]);
        root = true;
        parent = false;
        if (5 == args.length) {
            root = false;
            parentAddress = InetAddress.getByName(args[3]);
            parentPort = Integer.valueOf(args[4]);
        }
        messages = new ConcurrentLinkedQueue<>();
        children = new ConcurrentLinkedQueue<>();
        relatives = new ConcurrentHashMap<>();

        socket = new DatagramSocket(port);
    }


    public boolean isRelative(InetAddress address, int port) {
        if (hasParent() && address.equals(parentAddress) && port == parentPort)
            return true;
        for (AddrPort addrPort: children) {
            if (addrPort.getAddress().equals(address) && addrPort.getPort() == port)
                return true;
        }
        return false;
    }

    public void addMessage(Message message, boolean doPrint) {
        if (!messages.contains(message)) {
            messages.add(message);
            if (doPrint)
                System.out.println(message.getMessage() + "\n");
        }
    }

    public void addChild(InetAddress address, int port) {
        children.add(new AddrPort(address, port));
        relatives.put(new AddrPort(address, port), LocalTime.now());
    }

    public void setParent(boolean parent) {
        this.parent = parent;
        relatives.put(new AddrPort(parentAddress, parentPort), LocalTime.now());
    }

    public void updateRelatives(InetAddress address, int port) {
        for (AddrPort addrPort : relatives.keySet()) {
            if ((addrPort.getAddress().equals(address)) && (addrPort.getPort() == port))
                relatives.replace(addrPort, LocalTime.now());
        }
    }

    public void forgetRelative(InetAddress address, int port) {
        if (address.equals(parentAddress) && port == parentPort) {
            parent = false;
            root = true;
        }
        for (AddrPort addrPort : children) {
            if (addrPort.getAddress().equals(address) && addrPort.getPort() == port) {
                children.remove(addrPort);
                break;
            }
        }
        relatives.entrySet().removeIf(it -> it.getKey().getPort() == port && it.getKey().getAddress().equals(address));
        System.out.println("relative " + address + ":" + port + " R.I.P");
    }

    public String getName() {
        return name;
    }

    public ConcurrentLinkedQueue<AddrPort> getChildren() {
        return children;
    }

    public ConcurrentHashMap<AddrPort, LocalTime> getRelatives() {
        return relatives;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public InetAddress getParentAddress() {
        return parentAddress;
    }

    public int getParentPort() {
        return parentPort;
    }

    public boolean isRoot() {
        return root;
    }

    public boolean hasParent() {
        return parent;
    }

    public int getLossPercentage() {
        return lossPercentage;
    }
}

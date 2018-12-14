package Main;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("usage: <lport>, <rhost>, <rport>");
            System.exit(1);
        }

        int lport = 0, rport = 0;
        InetAddress rhost = null;
        try {
            lport = Integer.parseInt(args[0]);
            rhost = InetAddress.getByName(args[1]);
            rport = Integer.parseInt(args[2]);

            PortForwarder portForwarder = new PortForwarder(lport, rhost, rport);
            portForwarder.start();
        } catch (NumberFormatException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }catch (UnknownHostException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}

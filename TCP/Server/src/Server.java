import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("a number of port required");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);

        ServerSocket serverSocket = null;
        int numCon = 0;

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client accepted");
                new Thread(new Connection(clientSocket)).start();
                numCon++;
            }
        }catch(IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }finally {
            try{
                serverSocket.close();
            }catch (IOException e){
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }
}



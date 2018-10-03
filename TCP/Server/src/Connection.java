import java.io.*;
import java.net.Socket;
import java.time.LocalTime;

public class Connection implements Runnable {
    private static int totalNumber;
    private int curNumber;
    private Socket clientSocket;
    private String fileName;
    private int fileLength;
    private File file;

    public Connection(Socket socket) {
        curNumber = totalNumber++;
        clientSocket = socket;
    }

    @Override
    public void run() {
        System.out.println("Client #" + curNumber + ": " + clientSocket.getInetAddress() + " connected on port " + clientSocket.getPort());
        try (ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
            fileName = (String) in.readObject();
            fileLength = in.readInt();
            file = new File("uploads\\" + fileName);

            int totalReadLength = 0;
            int partlyReadLength = 0;
            int buffSize = 1024;
            byte[] buff = new byte[buffSize];
            LocalTime timeLast = LocalTime.now();
            LocalTime startSession = LocalTime.now();

            try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
                while (totalReadLength < fileLength) {
                    int len = in.read(buff);
                    totalReadLength += len;
                    partlyReadLength += len;
                    out.write(buff, 0, len);

                    if (LocalTime.now().getSecond() > 3 + timeLast.getSecond()) {
                        System.out.println("average speed for client #" + curNumber + ": " + ((float) totalReadLength / (LocalTime.now().getSecond() - startSession.getSecond())) + " Bps");
                        System.out.println("instantaneous speed for client #" + curNumber + ": " + ((float) partlyReadLength / (LocalTime.now().getSecond() - timeLast.getSecond())) + " Bps");
                        timeLast = LocalTime.now();
                        partlyReadLength = 0;
                    }
                }
            }

            oos.writeObject("success receiving");
            oos.flush();

            clientSocket.close();

            System.out.println("connection with client #" + curNumber + " closed");
            totalNumber--;
            System.out.println("+----------------| DONE |----------------+");
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
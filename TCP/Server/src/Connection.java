package main.java.Server;

import java.io.*;
import java.net.Socket;
import java.time.LocalTime;

public class Connection implements Runnable {
    private static int totalNumber;
    private int curNumber;
    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream oos;
    private String fileName;
    private int fileLength;
    private File file;
    private BufferedOutputStream out;

    public Connection(Socket socket) {
        curNumber = totalNumber++;
        clientSocket = socket;
        try {
            in = new ObjectInputStream(clientSocket.getInputStream());
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void run() {
        System.out.println("Client #" + curNumber + ": " + clientSocket.getInetAddress() + " connected on port " + clientSocket.getPort());
        try {
            fileName = (String) in.readObject();
            fileLength = in.readInt();
            file = new File("uploads\\" + fileName);
            out = new BufferedOutputStream(new FileOutputStream(file));

            int totalReadLength = 0;
            int partlyReadLength = 0;
            int buffSize = 1024;
            byte[] buff = new byte[buffSize];
            LocalTime timeLast = LocalTime.now();
            LocalTime startSession = LocalTime.now();

            while (totalReadLength < fileLength) {
                int len = in.read(buff);
                if (len < 0) {
                    System.out.println("WTF");
                } else {
                    totalReadLength += len;
                    partlyReadLength += len;
                    out.write(buff, 0, len);
                }

                if (LocalTime.now().getSecond() > 3 + timeLast.getSecond()) {
                    System.out.println("average speed for client #" + curNumber + ": " + ((float) totalReadLength / (LocalTime.now().getSecond() - startSession.getSecond())) + " Bps");
                    System.out.println("instantaneous speed for client #" + curNumber + ": " + ((float) partlyReadLength / (LocalTime.now().getSecond() - timeLast.getSecond())) + " Bps");
                    timeLast = LocalTime.now();
                    partlyReadLength = 0;
                }
            }

            oos.writeObject("success receiving");
            oos.flush();

            out.close();
            oos.close();
            in.close();
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

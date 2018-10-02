package main.java.Client;

import javax.print.DocFlavor;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalTime;

public class Client {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("file path and server IP:port required");
            System.exit(1);
        }

        String[] ip = args[1].split(":", -1);
        InetAddress address = null;
        Socket socket = null;
        int port = Integer.parseInt(ip[1]);
        try{
            address = InetAddress.getByName(ip[0]);
            socket = new Socket(address, port);
        }catch (UnknownHostException e) {
            System.err.println("Unknown address");
            System.exit(1);
        }catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("Connection to:" + address +":" + port);
        ObjectOutputStream out;
        BufferedInputStream in;
        File file;
        ObjectInputStream ois;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            file = new File(args[0]);
            ois = new ObjectInputStream(socket.getInputStream());
            out.writeObject(file.getName());
            out.flush();
            int buffSize = 1024;
            int totalReadLength = 0;
            byte[] buff = new byte[buffSize];
            int fileLen = (int)file.length();
            out.writeInt(fileLen);
            out.flush();

            in = new BufferedInputStream(new FileInputStream(file));

            int remainLen = fileLen;
            int readBytesCount = 0;
            while(remainLen > 0) {
                if (remainLen >= buffSize) {
                    readBytesCount = in.read(buff);
                }
                else {
                    buff = new byte[remainLen];
                    readBytesCount = in.read(buff);
                }
                remainLen -= readBytesCount;
                totalReadLength += readBytesCount;

                out.write(buff);
                out.flush();
                System.out.println("sent "+ ((float)totalReadLength/fileLen)*100 + "%");
            }
            System.out.println("+----------------| DONE |----------------+");
            LocalTime waitTime = LocalTime.now();
            while(true) {
                try {
                    String answer = (String)ois.readObject();
                    if(answer == null)
                        continue;
                    System.out.println(answer);
                    break;
                }catch (ClassNotFoundException e) {}
                if (LocalTime.now().getSecond() - waitTime.getSecond() > 1) {
                    System.out.println("waiting limit has been exceeded, server is not responding");
                    System.exit(1);
                }
            }
            in.close();
            out.close();
            ois.close();
            socket.close();
        }catch (FileNotFoundException e) {
            System.err.println("no such file");
            System.exit(1);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}

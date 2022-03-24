package Client;

import java.io.*;
import java.net.*;

public class Talker {
    BufferedReader bis;
    DataOutputStream dos;

    public Talker(Socket socket, String id) throws IOException{
        bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dos = new DataOutputStream(socket.getOutputStream());
        System.out.println("Created streams from socket!");
    }

    public Talker(Socket socket) throws IOException{
        bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dos = new DataOutputStream(socket.getOutputStream());
        System.out.println("Created streams from socket!");
    }

    public String receive() throws IOException{
        String msg =  bis.readLine();
        System.out.println("RECEIVED: " + msg);
        return msg;
    }

    public void send(String msg) throws IOException{
        dos.writeUTF(msg + "\n");
        System.out.println("SENT: " + msg);
    }
}

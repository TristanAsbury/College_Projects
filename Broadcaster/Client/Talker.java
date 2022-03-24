package Client;

import java.io.*;
import java.net.*;

public class Talker {
    String id;
    BufferedReader bis;
    DataOutputStream dos;

    public Talker(Socket socket, String id) throws IOException{
        bis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        dos = new DataOutputStream(socket.getOutputStream());
        this.id = id;
        System.out.println("Created streams from socket!");
    }

    public String receive() throws IOException{
        String msg =  bis.readLine();
        System.out.println(id + " RECEIVED: " + msg);
        return msg;
    }

    public void send(String msg) throws IOException{
        dos.writeUTF(id + ": " + msg + "\n");
        System.out.println(id + " SENT: " + msg);
    }
}

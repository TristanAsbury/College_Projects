package Client;

import java.io.*;
import java.net.Socket;

public class ClientTalker {

    private DataOutputStream dos;
    private BufferedReader dis;
    private String sendID;

    public ClientTalker(Socket socket){
        sendID = "pending";
        //Get streams
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException io){
            System.out.println("Problem getting a stream from the client connection. (in talker)");
        }
    }

    public String receive(){
        String returnString = null;
        try {
            System.out.println("RECEIVED: " + returnString);
            returnString = dis.readLine();
        } catch (IOException io){
            System.out.println("Problem receiving message from client connection.");
        }
        return returnString;
    }

    public void send(String message){
        try {
            System.out.println("SENT: " + message);
            dos.writeUTF(message + "\n");
        } catch (IOException io){
            System.out.println("Problem writing message to client connection.");
        }
    }
}

package Server;

import java.io.*;
import java.net.Socket;

public class ServerTalker {

    DataOutputStream dos;
    DataInputStream dis;

    public ServerTalker(Socket socket){
        //Get streams
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException io){
            System.out.println("Problem getting a stream from the client connection. (in talker)");
        }
    }

    public String receive(){
        String returnString = null;

        try {
            returnString = dis.readUTF();
        } catch (IOException io){
            System.out.println("Problem receiving message from client connection.");
        }
        
        return returnString;
    }

    public void send(String message){
        try {
            dos.writeUTF(message + "\n");
        } catch (IOException io){
            System.out.println("Problem writing message to client connection.");
        }
    }

    
    
}

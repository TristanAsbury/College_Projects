package Client;

import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;

public class ConnectionToServer implements Runnable {
    Talker talker;
    Socket socket;
    
    public ConnectionToServer(String id){
        try {
            socket = new Socket("localhost", 1234);
        } catch (IOException io){
            System.out.println("[Connection To Server] Couldn't establish a connection to the server. Exiting");
            JOptionPane.showMessageDialog(null, "Error connecting to server. Exiting.");
            System.exit(0);
        }

        try {
            talker = new Talker(socket, id);
            send(id);   //Sends the id to the server
        } catch (IOException io){
            System.out.println("[Connection To Server] Error connecting to server!");
            System.exit(0);
        }

        new Thread(this).start();
    }

    public void run(){
        try {
            String msg = talker.receive();
            handleMessage(msg);
        } catch (IOException io){
            System.out.println("[Connection To Server] Error receiving message from server. Exiting.");
            System.exit(0);
        }
    }

    //Wrapper method
    public void send(String msg){
        try {
            talker.send(msg);
        } catch (IOException io){
            System.out.println("[Connection to Server] Problem sending message to server.");
            System.exit(0);
        }
    }

    public void handleMessage(String msg){
        System.out.println(msg);
    }
}

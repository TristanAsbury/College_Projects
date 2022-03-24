package Server;

import java.io.IOException;
import java.net.*;
import java.util.Vector;

public class Main {
    public static void main(String[] args){
        ServerSocket servSocket;
        Socket regSocket;
        Vector<ConnectionToClient> ctcs = new Vector<ConnectionToClient>();

        //This only accepts one client
        try {
            servSocket = new ServerSocket(1234);
            System.out.println("WAITING FOR CLIENT CONNECTION");
            regSocket = servSocket.accept();    //Blocks until there is a request

            System.out.println("RECEIVED CLIENT REQUEST!");
            ConnectionToClient ctc = new ConnectionToClient(regSocket); //Once we have a connection, create a ctc
            ctcs.add(ctc); //Add the ctc to the list of ctcs

        } catch (IOException io){
            System.out.println("Problem opening server socket!");
        }
    }
}s

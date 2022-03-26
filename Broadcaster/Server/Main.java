//package Server;

import java.io.IOException;
import java.net.*;
import java.util.Vector;

class Main {
    public static void main(String[] args){
        Vector<ConnectionToClient> ctcs = new Vector<ConnectionToClient>(); //this will contain all active connection to clients
        ServerSocket serverSocket = null;

        try {
            System.out.println("[Server] Starting server...");
            serverSocket = new ServerSocket(1234);
        } catch (IOException io){
            System.out.println("[Server] Error starting server...");
            System.exit(0);
        }
        
        while(true){
            try {
                System.out.println("[Server] Awaiting client connection");
                Socket tempSocket = serverSocket.accept();          //Blocks until a request from a client
                System.out.println("[Server] Successfully connected to client.");

                ConnectionToClient tempConnection = new ConnectionToClient(tempSocket, ctcs);
                ctcs.add(tempConnection);   //Adds this connection to list of client connections
            } catch (IOException io){
                System.out.println("Error connecting to that client...");
            }
        }
        
    }
}
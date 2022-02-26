import java.io.*;
import java.net.*;
import java.util.Vector;

class Main {
    public static void main(String[] args){
        boolean findingClients = true;
        Socket normalSocket;
        ServerSocket servSocket = null;
        Vector<ConnectionToClient> ctcs = new Vector<ConnectionToClient>();    //Connections to client
        
        
        try{
            System.out.println("Starting server!!");
            servSocket = new ServerSocket(1234);
        } catch (IOException io){
            System.out.println("Error starting server!");
            System.exit(0);
        }
        
        //MAIN LOOP, VERY IMPORTANT!!!!!!!!
        while(findingClients){                          //While we are looking for clients
            try {
                System.out.println("Listening for connection!");

                normalSocket = servSocket.accept();     //If a client is trying to connect, accept
                ConnectionToClient temp = new ConnectionToClient(ctcs, normalSocket);
                temp.send("Connected!");
                temp.send("weirdo");
                new Thread(temp).start();               //Starts the thread, so now it will listen for messages from the actual client
                ctcs.add(temp);                         //Add the successful connection to the vector of connections
                temp.send("HEHEHEHEE");
            } catch(IOException io){
                System.out.println("Error accepting client connection!");
            }
        }


    }
}
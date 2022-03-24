package Server;

import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

public class ConnectionToClient implements Runnable {
    Talker talker;
    Vector<ConnectionToClient> ctcs;
    String id;
    boolean receiving;

    public ConnectionToClient(Socket socket, Vector<ConnectionToClient> ctcs){
        try {
            talker = new Talker(socket);
            this.id = talker.receive(); //Receives the first message which will be the id from the client
            System.out.println("[Connection To Client] Client id is: " + id);   //Gets the id
            this.ctcs = ctcs;
        } catch (IOException io){
            System.out.println("Problem connecting to client...");
        }

        receiving = true;
        new Thread(this).start();
    }

    public void run(){
        while(receiving){
            receive();
        }
    }

    public void receive(){
        try{
            String msg = talker.receive();
            

            System.out.println("[Connection To Client] " + id + " Received: " + msg);
        } catch (IOException io){
            System.out.println("[Connection To Client] " + id + " Problem receiving message from client.");
            ctcs.remove(this);
        }
    }

    //Wrapper method
    public void send(String msg){
        try {
            talker.send(msg);
        } catch (IOException io){
            System.out.println("Problem sending message...");
            ctcs.remove(this);  //If there is a problem, remove ourself from the list of ctcs so the server doesn't send messages to us (we are forgotten :( )
        }
    }
}

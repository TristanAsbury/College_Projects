package Server;

import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

public class ConnectionToClient implements Runnable {
    Talker talker;
    Vector<ConnectionToClient> ctcs;
    String id;

    public ConnectionToClient(Socket socket, Vector<ConnectionToClient> ctcs){
        try {
            talker = new Talker(socket);
            this.id = talker.receive(); //Receives the first message which will be the id from the client
            System.out.println("[Connection To Client] Client id is: " + id);   //Gets the id
            this.ctcs = ctcs;
            new Thread(this).start();
        } catch (IOException io){
            System.out.println("Problem connecting to client...");
        }
    }

    public void run(){
        try {
            String msg = talker.receive();
            System.out.println("[Connection To Client] Received: " + msg);

            // for(ConnectionToClient ctc : ctcs){
            //     if(ctc != this){
            //         ctc.send(id + ": " + msg);
            //     }
            // }
        } catch (IOException io){
            System.out.println("[Connection To Client] Problem receiving message from client.");
            ctcs.remove(this);  //If there is a problem, remove ourself from the list of ctcs so the server doesn't send messages to us (we are forgotten :( )
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

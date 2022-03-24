package Server;

import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

public class ConnectionToClient implements Runnable {
    Talker talker;
    Vector<ConnectionToClient> ctcs;

    public ConnectionToClient(Socket socket, Vector<ConnectionToClient> ctcs){
        try {
            talker = new Talker(socket);
            this.ctcs = ctcs;
        } catch (IOException io){
            System.out.println("Problem connecting to client...");
        }
        new Thread(this).start();
    }

    public void run(){
        try {
            String msg = talker.receive();

            for(ConnectionToClient ctc : ctcs){
                if(ctc != this){
                    ctc.send(msg);
                }
            }
        } catch (IOException io){
            System.out.println("[ConnectionToClient] Problem receiving message from client.");
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

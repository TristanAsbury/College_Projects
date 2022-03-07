import java.net.Socket;
import java.util.Vector;

public class ConnectionToClient implements Runnable {
    Socket fromClient;                  //The talker that will receive a message from the specific client
    Talker talker;
    Vector<ConnectionToClient> ctcs; //Vector of ctcs so it can relay the message
    boolean keepRunning = true;

    public ConnectionToClient(Vector<ConnectionToClient> ctcs, Socket fromClient){
        this.ctcs = ctcs;
        this.fromClient = fromClient;
        talker = new Talker(fromClient);
    }

    public void run() {
        while(keepRunning){
            if(talker.id != "pending"){ //If the client is still connected
                String msg = talker.receive();
                for(ConnectionToClient ctc : ctcs){
                    if(this != ctc){
                        ctc.send(msg);
                    }
                }
            }
        }
    }

    public void send(String msg){
        talker.send(msg);
    }
}

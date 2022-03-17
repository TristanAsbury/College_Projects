package Server;
import java.io.IOException;
import java.net.Socket;

public class ConnectionToClient implements Runnable {
    Talker talker;
    
    public ConnectionToClient(Socket socket){
        try {
            talker = new Talker(socket);
        } catch (IOException io){
            System.out.println("Problem creating talker.");
        }
        new Thread(this).start();
    }

    public void run() {
        boolean keepRunning = true;

        while(keepRunning){                     //While we keep running
            try {                           
                String str = talker.receive();  //Get the message from the client
                if(str.equals("done")){         //If the message is done
                    keepRunning = false;        //Stop running
                }
            } catch (IOException io){           //If the servers connection to the client stops, then seize the server
                System.out.println("Problem receiving on server side");
                System.exit(0);
            }
        }
    }
}

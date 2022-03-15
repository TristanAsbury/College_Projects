package Server;
import java.net.Socket;

public class ConnectionToClient implements Runnable {
    ServerTalker talker;
    
    public ConnectionToClient(Socket socket){
        talker = new ServerTalker(socket);
        new Thread(this).start();
    }

    public void run(){
        boolean keepRunning = true;

        while(keepRunning){
            String str = talker.receive();
            if(str.equals("done")){
                keepRunning = false;
            }
            System.out.println("RECD: " + str);
        }
    }
}

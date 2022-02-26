import java.io.IOException;
import java.net.Socket;

class Main {
    public static void main(String[] args){
        Socket servConnection;
        Talker talker = null;

        try {
            servConnection = new Socket("localhost", 1234);
            talker = new Talker(servConnection);
            talker.send("Hello!");
        } catch (IOException io){
            System.out.println("Problem opening connection socket!");
            System.exit(0);
        }

        InputDialog myDialog = new InputDialog(talker);

        while(true){
            System.out.println(talker.receive());
        }
    }
}
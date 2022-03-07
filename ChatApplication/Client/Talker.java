import java.io.*;
import java.net.Socket;

public class Talker {
    BufferedReader inputStream;
    DataOutputStream outputStream;
    String id;

    public Talker(Socket fromClient){
        try {
            inputStream = new BufferedReader(new InputStreamReader(fromClient.getInputStream()));
            outputStream = new DataOutputStream(fromClient.getOutputStream());
        } catch (IOException io){
            System.out.println("Error setting up reader!");
        }
    }

    public String receive(){
        String returnString = null;
        try {
            returnString = inputStream.readLine();
        } catch (IOException io) {
            System.out.println("Error reading message from client connection!");
            id = "pending";
        }

        return returnString;
    }

    public void send(String output){
        try {
            outputStream.writeBytes(output + "\n");
        } catch (IOException io){
            System.out.println("Error sending message from client!");
        }
        
    }
}   

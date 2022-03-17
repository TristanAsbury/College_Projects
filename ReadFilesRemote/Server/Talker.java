package Server;

import java.io.*;
import java.net.Socket;

public class Talker {

    private DataOutputStream dos;
    private BufferedReader dis;
    private String id;

    public Talker(Socket socket) throws IOException {
        id = "PENDING...";
        //Get streams
        dos = new DataOutputStream(socket.getOutputStream());                   //Get data output stream
        dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));   //Get data input stream
    }

    public Talker(String domain, int port, String id) throws IOException{
        Socket tempSocket = new Socket(domain, port);
        this.id = id;
        dos = new DataOutputStream(tempSocket.getOutputStream());                       //Get data output stream
        dis = new BufferedReader(new InputStreamReader(tempSocket.getInputStream()));   //Get data input stream
    }

    public String receive() throws IOException {
        String returnString = dis.readLine();           //Gets the string from the input stream
        System.out.println("RECEIVED from " + id + ":" + returnString);//Print the received string to the console
        return returnString;    //Return the string to the caller
    }

    public void send(String message) throws IOException {
        dos.writeBytes(message + "\n");
        System.out.println("SENT from " + id + ": " + message);     //Print the sent message to the console
    }
}

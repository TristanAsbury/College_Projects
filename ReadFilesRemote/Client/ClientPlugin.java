package Client;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

class ClientPlugin {

    Vector<File> files;
    ClientTalker talker;

    public ClientPlugin(){
        
        Socket socket = null;
        files = new Vector<File>();
        //Connect to server
        try {
            socket = new Socket("localhost", 1234);
        } catch (IOException io){
            System.out.println("Problem connecting to server!");
        }

        //Open talker
        talker = new ClientTalker(socket);
        
        //Get root folder
        File root = new File("C:/");
        
        //Start sending file names
        sendFileNames(root);
        talker.send("done");
    }

    private void sendFileNames(File root){
        files.add(root);

        while(files.size() > 0){
            File currentFile = files.get(0);
            if(currentFile.isDirectory()){
                File[] subFiles = currentFile.listFiles();
                
                if(subFiles != null){
                    for(int i = 0; i < subFiles.length; i++){
                        files.add(subFiles[i]);
                    }
                }
                
            } else if(currentFile.isFile()){
                talker.send(currentFile.getAbsolutePath());
            }
            files.remove(0);
        }
    }
}
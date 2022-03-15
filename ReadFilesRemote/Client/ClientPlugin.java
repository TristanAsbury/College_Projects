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
        boolean connected = false;
        
        //Connect to server
        while(!connected){
            try {
                socket = new Socket("localhost", 1234);
                connected = true;
            } catch (IOException io){
                System.out.println("Problem connecting to server!");
            }
        }

        //Open talker
        talker = new ClientTalker(socket);
        
        //Get root folder CHANGE THIS IF YOU WANT A QUICK SEARCH
        File root = new File("C:/");
        
        //Start sending file names
        sendFileNames(root);
        talker.send("done");
    }

    //Simple breadth first search
    private void sendFileNames(File root){
        files.add(root);        //Add root path
        while(files.size() > 0){    //While there are still files in the vector of files
            File currentFile = files.get(0);    //Read each directory 
            if(currentFile.isDirectory()){            //If the file is a directory
                File[] subFiles = currentFile.listFiles();  //Get the subfiles
                if(subFiles != null){                 //This prevents any problems with empty folders
                    for(int i = 0; i < subFiles.length; i++){   //Add the directory
                        files.add(subFiles[i]);
                    }
                }
            } else if(currentFile.isFile()){
                talker.send(currentFile.getAbsolutePath());
            }
            files.remove(0);
        }
    }

    //Gets roots
    //Traverses directory tree
    //In visit use
    //Talker.send(f.getName())

}
package Client;
import java.io.File;
import java.io.IOException;

class ClientPlugin {
    Talker talker;
    boolean isGood;

    public ClientPlugin(){
        isGood = true;
        //Open talker
        try {
            talker = new Talker("localhost", 1234, "bob");
        } catch (IOException io){
            System.out.println("Trouble creating client talker.");
            isGood = false;
        }
        
        //Get root folder CHANGE THIS IF YOU WANT A QUICK SEARCH
        File root = new File("C:/");
        
        //Start sending file names
        sendFileNames(root);

        //Finish by sending done command
        try {
            talker.send("done");
        } catch (IOException io) {
            System.out.println("Problem sending done message.");
        }
    }

    //Simple breadth first search
    private void sendFileNames(File root){
        File[] files = root.listFiles();    //Get the files in the directory we just referenced
        if(files != null && isGood){        //If that list isn't null (happens with weird file types)
            for(File f : files){            //Go through each file
                if(f.isDirectory()){        //If that file is a directory
                    sendFileNames(f);       //Repeat
                } else if (f.isFile()){     //Else,
                    visit(f);               //Use the visit method
                }
            }
        }
    }

    private void visit(File file){
        if(isGood){         //If we're good to send
            try{            
                talker.send(file.getPath());    //Send the file name
            } catch (IOException io){
                System.out.println("Problem sending file name");
                isGood = false;
            }
        }
    }
}
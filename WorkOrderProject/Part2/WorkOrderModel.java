import javax.swing.DefaultListModel;
import java.io.*;

//This class is a "smarter" DefaultListModel that is able to load itself from a file
public class WorkOrderModel extends DefaultListModel<WorkOrder> {
    
    //Loadfrom method will automatically load WorkOrder information from the specified dataInputStream
    public void loadFrom(DataInputStream dis){
        try{
            while(dis.available() > 0){             //While there is still data in the file
                addElement(new WorkOrder(dis));     //Add a WorkOrder (constructed from a data input stream) to the instance of the WorkOrderModel
            }
            dis.close();                            //Close the stream
        } catch (IOException e){
            System.out.println("Problem reading info from file...");
        }
    }

    //Saveto method will automatically save WorkOrder information to the specified dataOutputStream
    public void saveTo(DataOutputStream dos){
        try {
            for(int i = 0; i < size(); i++){        //Go through all the WorkOrders in the instance of WorkOrderModel
                WorkOrder writeOrder = get(i);      //Create a pointer to the current index of WorkOrder
                writeOrder.writeTo(dos);            //Use the WorkOrder writeTo method to make it save itself to a dataOutputStream
            }
            dos.close();
        } catch(IOException e){
            System.out.println("Error writing to file...");
        }
    }
}

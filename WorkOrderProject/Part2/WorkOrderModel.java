import javax.swing.DefaultListModel;
import java.io.*;
public class WorkOrderModel extends DefaultListModel<WorkOrder> {
    public void loadFrom(DataInputStream dis){
        try{
            while(dis.available() > 0){
                addElement(new WorkOrder(dis));
            }
            dis.close();
        } catch (IOException e){
            System.out.println("Problem reading info from file...");
        }
    }

    public void saveTo(DataOutputStream dos){
        try {
            for(int i = 0; i < size(); i++){
                WorkOrder writeOrder = get(i);
                writeOrder.writeTo(dos);
            }
            dos.close();
        } catch(IOException e){
            System.out.println("Error writing to file...");
        }
    }
}

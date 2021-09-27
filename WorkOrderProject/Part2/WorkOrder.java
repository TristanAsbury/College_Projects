import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Date;

public class WorkOrder {
    public String department;
    public String name;
    public String description;
    
    public long initial;
    public long fulfilled;
    public float billingRate;

    public WorkOrder(String name, String department, Date initial, Date fulfilled, String description, float billingRate){
        this.name = name;
        this.department = department;
        this.initial = initial.getTime();
        this.fulfilled = fulfilled.getTime();
        this.description = description;
        this.billingRate = billingRate;
    }

    public WorkOrder(){

    }

    //Generate random stuff
    public static WorkOrder getRandom(){
        return new WorkOrder();
    }

    //Prints the objects to the console
    public void printOrder(){

    }

    //
    public void constructFrom(DataInputStream dis){

    }

    //
    public void saveTo(DataOutputStream dos){

    }

   

}

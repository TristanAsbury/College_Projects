import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;

public class WorkOrder {
    public String department;
    public String name;
    public String description;
    
    public long requested;
    public long fulfilled;
    public float billingRate;

    //Constructs workorder from input data
    public WorkOrder(String name, String department, Date requested, Date fulfilled, String description, float billingRate){
        this.name = name;
        this.department = department;
        this.requested = requested.getTime();
        this.fulfilled = fulfilled.getTime();
        this.description = description;
        this.billingRate = billingRate;
    }
    
    //Constructs workorder from a dataInputStream
    public WorkOrder(DataInputStream dis){
        try{
            this.name = dis.readUTF();
            this.department = dis.readUTF();
            this.requested = dis.readLong();
            this.fulfilled = dis.readLong();
            this.description = dis.readUTF();
            this.billingRate = dis.readFloat();
        } catch (IOException e){
            System.out.println("Cannot open from file");
            System.out.println(e.getStackTrace());
        }
    }

    //Generate and return "random" instance of WorkOrder
    public static WorkOrder getRandom(){
        String[] randNames = {"Tristan", "Bob", "George", "Larry", "Gonk", "Bonk"};
        String[] randDepts = {"SALES", "HARDWARE", "ELECTRONICS"};
        String[] randDescs = {"We need 'dis done right now.", "Orda given, do it.", "Gooday, we are requesting this orda.", "Evenin', an order has been placed.", "'Ello mate, requesting da orda."};
        WorkOrder returnOrder;
        Random rand = new Random();
        String randName = randNames[rand.nextInt(6)];
        String randDept = randDepts[rand.nextInt(3)];
        String randDesc = randDescs[rand.nextInt(5)];

        //Generating random dates
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date maxDate = new Date();
        Date beginDate = new Date();
        try{
            beginDate = sdf.parse("1/1/2000");
            maxDate = sdf.parse("1/1/2200");
        } catch(ParseException e){
            e.printStackTrace();
        }
        Date randReq = new Date(beginDate.getTime() + (long)(maxDate.getTime() * rand.nextFloat())); //Will get random date 
        Date randFul = new Date((long)(rand.nextFloat() * beginDate.getTime()) + randReq.getTime()); //Will get a date that is before the requested date

        //Generate random billing rate
        float randBR = (float)(12.8 - (7.2*rand.nextFloat()));
        returnOrder = new WorkOrder(randName, randDept, randReq, randFul, randDesc, randBR);
        return returnOrder;
    }

    //Prints the objects to the console
    public void printOrder(){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        System.out.println("Name: " + name);
        System.out.println("Department: " + department);
        System.out.println("Date Requested: " + sdf.format(requested));
        System.out.println("Date Fulfilled: " + sdf.format(fulfilled));
        System.out.println("Description: " + description);
        System.out.println("Billing Rate: " + billingRate);
    }

    public void writeTo(DataOutputStream dos){
        try{
            dos.writeUTF(name);
            dos.writeUTF(department);
            dos.writeLong(requested);
            dos.writeLong(fulfilled);
            dos.writeUTF(description);
            dos.writeFloat(billingRate);
        } catch(IOException e){
            System.out.println("Error saving to file");
        }
    }

    public String toString(){
        DateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return String.format("Name: %-15s Department: %-15s Requested: %-12s Fulfilled: %-12s Description: %-20s Billing Rate: %4.2f", name, department, simpleDateFormat.format(requested), simpleDateFormat.format(fulfilled), description, billingRate);
    }
}

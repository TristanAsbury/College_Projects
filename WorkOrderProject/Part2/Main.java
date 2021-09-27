public class Main {
    public static void main(String[] args){
        WorkOrderFrame projectFrame = new WorkOrderFrame();
        WorkOrder myWorkOrder = new WorkOrder();
        WorkOrderDialog myDialog = new WorkOrderDialog();
        WorkOrder myOrder = myDialog.getWorkOrder();
        
        if(myOrder == null){
            System.out.println("User pressed cancel.");
        }
    }
}

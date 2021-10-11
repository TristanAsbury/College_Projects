import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.AbstractTableModel;

public class WorkOrderTableModel extends AbstractTableModel implements DataManager {
    WorkOrderModel wom;

    public WorkOrderTableModel(){
        wom = new WorkOrderModel();
    }

    public int getColumnCount(){
        return 6;
    }

    public int getRowCount(){
        return wom.getSize();
    }

    public void addElement(WorkOrder order){
        wom.addElement(order);
        fireTableDataChanged();
    }

    public WorkOrder getItemAt(int index){
        return wom.get(index);
    }

    public WorkOrder getElement(int row){
        return wom.get(row);
    }

    public void removeElement(int index){
        wom.remove(index);
        fireTableDataChanged();
    }

    public void removeAllElements(){
        wom.removeAllElements();
        fireTableDataChanged();
    }

    public int getSize(){
        return wom.size();
    }

    public void saveTo(DataOutputStream dos){
        wom.saveTo(dos);
    }

    public void loadFrom(DataInputStream dis){
        wom.loadFrom(dis);
    }
    
    public Object getValueAt(int row, int col){
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        WorkOrder order = wom.getElementAt(row);
        if(col == 0){
            return order.name;
        } else if(col == 1){
            return order.department;
        } else if(col == 2){
            return sdf.format(new Date(order.requested));
        } else if(col == 3){
            if(order.fulfilled == 0){
                return "NOT COMPLETE";
            }
            return sdf.format(new Date(order.fulfilled));
        } else if(col == 4){
            return order.billingRate;
        } else if(col == 5){
            return order.description;
        } else {
            System.out.println("Error!");
            System.exit(1);
            return null;
        }
    }

    public Class<?> getColumnClass(int c){
        return String.class;
    }

    public void AddItem(WorkOrder w){
        addElement(w);
    }

    public void ReplaceItem(WorkOrder newOrder, int oldOrderIndex){
        wom.set(oldOrderIndex, newOrder);
        fireTableDataChanged();
    }
}

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

    public void removeElement(int index){
        wom.removeElementAt(index);
        fireTableDataChanged();
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

    public void AddItem(WorkOrder wo){
        wom.addElement(wo);
    }

    public void ReplaceItem(WorkOrder wo, int n){
        wom.set(n, wo);
    }
}

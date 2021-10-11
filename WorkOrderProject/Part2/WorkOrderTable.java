import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class WorkOrderTable extends JTable {
    WorkOrderTableModel wotm;
    DefaultTableColumnModel colMod;

    public WorkOrderTable(WorkOrderTableModel wotm){
        super(wotm);
        colMod = new DefaultTableColumnModel();
        createColumn(0, "Name", 40, 40);
        createColumn(1, "Department", 40, 40);
        createColumn(2, "Date Requested", 40, 40);
        createColumn(3, "Date Fulfilled", 40, 40);
        createColumn(4, "Billing Rate", 40, 40);
        createColumn(5, "Description", 40, 40);
        setColumnModel(colMod);
    }

    private void createColumn(int index, String columnName, int prefWidth, int minWidth){
        TableColumn col = new TableColumn(index);
        col.setPreferredWidth(prefWidth);
        col.setMinWidth(minWidth);
        col.setHeaderValue(columnName);
        colMod.addColumn(col);
    }

    
}

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.io.*;
import java.util.Calendar;
import java.awt.dnd.*;
import javax.swing.event.*;
import javax.swing.table.TableRowSorter;
import java.awt.datatransfer.*;


public class WorkOrderFrame extends JFrame implements ActionListener, ListSelectionListener, MouseInputListener, DropTargetListener {
    JPanel buttonPanel;
    JPanel scrollerPanel;

    JMenuBar menuBar;
    JMenu fileOptionsMenu;
    JMenu itemOptionsMenu;

    WorkOrderTableModel workOrderTableModel;
    WorkOrderTable workOrderTable;
    JScrollPane scroller;

    //These menu items are in a wider scope so we can disable them within the methods
    JMenuItem deleteMenuItem;
    JMenuItem deleteAllMenuItem;
    JMenuItem editMenuItem;
    JMenuItem completePopup;
    JMenuItem editPopup;
    JMenuItem deletePopup;

    JButton deleteButton;
    JButton exitButton;
    JButton editButton;
    JButton printButton;

    JFileChooser fileChooser;
    File chosenFile;

    JPopupMenu popupMenu;

    DataInputStream dis;
    DataOutputStream dos;
    FileInputStream fis;
    FileOutputStream fos;

    DropTarget dropTarget;

    Point mousePos;

    WorkOrderFrame(){
        initIO();
        initList();
        initButtons();
        initMenuBar();
        setUpFrame();
        setupPopupMenu();
        // for(int i = 0; i < 10; i++){
        //     workOrderTableModel.addElement(WorkOrder.getRandom());
        // }
    }

    private void setupPopupMenu(){
        popupMenu = new JPopupMenu();
        completePopup = new JMenuItem("Mark as complete");
        completePopup.addActionListener(this);
        completePopup.setToolTipText("Mark as complete");

        editPopup = new JMenuItem("Edit entry");
        editPopup.addActionListener(this);
        editPopup.setToolTipText("Edit...");

        deletePopup = new JMenuItem("Delete entry");
        deletePopup.addActionListener(this);
        deletePopup.setToolTipText("Delete");

        popupMenu.add(editPopup);
        popupMenu.add(deletePopup);
        popupMenu.add(completePopup);
    }

    private void initIO(){
        fileChooser = new JFileChooser(".");
    }
    
    private void initList(){
        scrollerPanel = new JPanel();

        workOrderTableModel = new WorkOrderTableModel();

        workOrderTable = new WorkOrderTable(workOrderTableModel);
        workOrderTableModel.addTableModelListener(workOrderTable);

        TableRowSorter<WorkOrderTableModel> sorter;
        sorter = new TableRowSorter<WorkOrderTableModel>(workOrderTableModel);
        workOrderTable.setRowSorter(sorter);

        workOrderTable.setMinimumSize(new Dimension(400, 250));
        workOrderTable.getSelectionModel().addListSelectionListener(this);

        scroller = new JScrollPane(workOrderTable);
        scrollerPanel.add(scroller);
        scroller.getViewport().setBackground(Color.getHSBColor(155, 90, 255));

        dropTarget = new DropTarget(scroller, this);

        workOrderTable.addMouseListener(this);
        add(scroller, BorderLayout.CENTER);
    }

    private void initMenuBar(){
        menuBar = new JMenuBar();
        fileOptionsMenu = new JMenu("File");
        itemOptionsMenu = new JMenu("Item");

        //Add "file" options menu items to fileOptionsMenu (we will make these anonymous because we won't have to modify them directly)
        fileOptionsMenu.add(createMenuItem("Load", "LOAD", this, KeyEvent.VK_L, KeyEvent.VK_L, "Load a file."));
        fileOptionsMenu.add(createMenuItem("Save", "SAVE", this, KeyEvent.VK_S, KeyEvent.VK_S, "Save a file."));
        fileOptionsMenu.add(createMenuItem("Save As", "SAVEAS", this, KeyEvent.VK_A, KeyEvent.VK_A, "Save as..."));
        
        //We create these as named variables since we will be modifying them
        deleteMenuItem = createMenuItem("Delete", "DELETE", this, KeyEvent.VK_D, KeyEvent.VK_D, "Delete selected item.");
        deleteMenuItem.setEnabled(false);
        deleteAllMenuItem = createMenuItem("Delete All", "DELETEALL", this, KeyEvent.VK_F, KeyEvent.VK_F, "Delete all.");
        deleteAllMenuItem.setEnabled(false);
        editMenuItem = createMenuItem("Edit Item", "EDIT", this, KeyEvent.VK_E, KeyEvent.VK_E, "Edit selected item.");
        editMenuItem.setEnabled(false);

        //Add "item" options menu items to itemOptionsMenu
        itemOptionsMenu.add(createMenuItem("New", "NEW", this, KeyEvent.VK_N, KeyEvent.VK_N, "New item."));
        itemOptionsMenu.add(deleteMenuItem);
        itemOptionsMenu.add(deleteAllMenuItem);
        itemOptionsMenu.add(editMenuItem);

        //Add JMenus to JMenuBar
        menuBar.add(fileOptionsMenu);
        menuBar.add(itemOptionsMenu);

        add(menuBar, BorderLayout.NORTH);
    }

    private void initButtons(){
        //Init Panel
        buttonPanel = new JPanel(); 

        //Init buttons
        deleteButton = createButton("Delete", "DELETE", this);
        deleteButton.setEnabled(false);
        exitButton = createButton("Exit", "EXIT", this);
        editButton = createButton("Edit", "EDIT", this);
        editButton.setEnabled(false);
        printButton = createButton("Print", "PRINT", this);
        
        //Add buttons to buttonPanel
        buttonPanel.add(createButton("Load", "LOAD", this));
        buttonPanel.add(createButton("Save", "SAVE", this));
        buttonPanel.add(createButton("Save As", "SAVEAS", this));
        buttonPanel.add(createButton("Add", "NEW", this));
        buttonPanel.add(printButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exitButton);
        
        add(buttonPanel, BorderLayout.SOUTH); //Add the panel to the frame
    }
    
    private void setUpFrame(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize((int)d.getWidth()/2, (int)d.getHeight()/2);
        setLocation((int)d.getWidth()/4, (int)d.getHeight()/4);
        setVisible(true);
        setTitle("Work Orders");
    }

    //Method to return a JMenuItem with the specified arguments passed
    private JMenuItem createMenuItem(String label, String actionCommand, ActionListener menuListener, int mnemonic, int keyCode, String toolTipText){
        JMenuItem returnedItem;
        returnedItem = new JMenuItem(label, mnemonic);
        returnedItem.setActionCommand(actionCommand);
        returnedItem.addActionListener(menuListener);
        returnedItem.setMnemonic(mnemonic);
        returnedItem.setAccelerator(KeyStroke.getKeyStroke(keyCode, KeyEvent.ALT_DOWN_MASK));
        returnedItem.setToolTipText(toolTipText);
        return returnedItem;
    }

    private JButton createButton(String label, String actionCommand, ActionListener actionListener){
        JButton returnButton;
        returnButton = new JButton(label);
        returnButton.setActionCommand(actionCommand);
        returnButton.addActionListener(actionListener);
        return returnButton;
    }

    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals("DELETE")){                  //IF THE USER IS DELETING AN ITEM
            deleteItem();
        }

        if(e.getActionCommand().equals("DELETEALL")){               //IF THE USER IS DELETING ALL THE ITEMS
            workOrderTableModel.removeAllElements();                     //Simple enough (I think??)
            deleteAllMenuItem.setEnabled(workOrderTableModel.getSize() > 0);
        }

        if(e.getActionCommand().equals("LOAD")){                    //IF THE USER IS LOADING FROM A FILE
            load();
        }

        if(e.getActionCommand().equals("NEW")){                     //IF THE USER IS ADDING A NEW STRING
            newItem();
        }
        
        if(e.getActionCommand().equals("SAVE")){                    //IF THE USER PRESSES SAVE
            save();
        }

        if(e.getActionCommand().equals("SAVEAS")){                  //IF THE USER IS SAVING A FILE AS
            saveAs();
        }

        if(e.getActionCommand().equals("EDIT")){
            editItem();
        }

        if(e.getActionCommand().equals("EXIT")){                    //IF THE USER PRESSES EXIT
            System.exit(0);
        }

        if(e.getActionCommand().equals("PRINT")){
                try {
                    boolean print = workOrderTable.print();
                    if(!print){
                        JOptionPane.showMessageDialog(null, "Unable To Print!");
                    }
                } catch (PrinterException e1) {
                    e1.printStackTrace();
                }
        }

        if(e.getSource() == completePopup){
            WorkOrder selectedWO = workOrderTableModel.getItemAt(workOrderTable.rowAtPoint(mousePos));
            if(selectedWO.fulfilled == 0){
                selectedWO.fulfilled = Calendar.getInstance().getTimeInMillis();
            } else {
                int op = JOptionPane.showConfirmDialog(this, "Would you like to mark this order as complete on today's date?");
                if(op == 0){
                    selectedWO.fulfilled = Calendar.getInstance().getTimeInMillis();
                }
            }
        }

        if(e.getSource() == deletePopup){
            workOrderTableModel.removeElement(workOrderTable.rowAtPoint(mousePos));
        }

        if(e.getSource() == editPopup){
            WorkOrder editedOrder = workOrderTableModel.getElement(workOrderTable.rowAtPoint(mousePos));
            WorkOrderDialog editDialog = new WorkOrderDialog(workOrderTableModel, editedOrder, workOrderTable.getSelectedRow());
        }
    } 

    public void valueChanged(ListSelectionEvent e){
        if(e.getSource() == workOrderTable.getSelectionModel()){
            deleteMenuItem.setEnabled(workOrderTable.getSelectedRows().length > 0);
            deleteButton.setEnabled(workOrderTable.getSelectedRows().length > 0);
            editButton.setEnabled(workOrderTable.getSelectedRows().length == 1);
            editMenuItem.setEnabled(workOrderTable.getSelectedRows().length == 1);
        }
    }

    private void saveAs(){
        int option = fileChooser.showSaveDialog(this);              //Show save dialog and get the button chosens

        if(option == JFileChooser.APPROVE_OPTION){                  //If the user pressed save
            try {
                chosenFile = fileChooser.getSelectedFile();
                fos = new FileOutputStream(chosenFile);             //Try to open up file output stream
                dos = new DataOutputStream(fos);                    //Try to open data output stream
                workOrderTableModel.saveTo(dos);
                dos.close();                                        //Close data stream
                fos.close();                                        //Close file output stream
            } catch (IOException o){
                JOptionPane.showMessageDialog(this, "Error saving file!");
            }
        }
    }

    private void load(java.util.List<File> files){
        try {
            deleteAllMenuItem.doClick();
            for(int i = 0; i < files.size(); i++){
                File curFile = files.get(i);
                fis = new FileInputStream(curFile);
                dis = new DataInputStream(fis);
                workOrderTableModel.loadFrom(dis);
                dis.close();
                fis.close();
            }
        } catch(IOException io){
            io.printStackTrace();
        }
        deleteAllMenuItem.setEnabled(workOrderTableModel.getSize() > 0);
    }

    private void load(){
        int option = fileChooser.showOpenDialog(this);              //Show the open dialog and get the option from the button pressed

        if(option == JFileChooser.APPROVE_OPTION){                  //If the user pressed load
            try {
                deleteAllMenuItem.doClick();
                chosenFile = fileChooser.getSelectedFile();
                fis = new FileInputStream(chosenFile);              //Try to open the file input stream
                dis = new DataInputStream(fis);                     //Try to open up the data stream
                workOrderTableModel.loadFrom(dis);
                dis.close();                                        //Close input stream
                fis.close();                                        //Close file input stream
            } catch(IOException o){
                chosenFile = null;
                JOptionPane.showMessageDialog(this, "Error loading file!");
            }
        }
        deleteAllMenuItem.setEnabled(workOrderTableModel.getSize() > 0);
    }

    private void newItem(){
        WorkOrderDialog addDialog = new WorkOrderDialog(workOrderTableModel);       //Create the instance of WorkOrderDialog in the add mode
        deleteAllMenuItem.setEnabled(workOrderTableModel.getSize() > 0);            //Enable the button based on list size
    }

    private void editItem(){
        WorkOrder editedOrder = workOrderTableModel.getElement(workOrderTable.getSelectedRow());
        WorkOrderDialog editDialog = new WorkOrderDialog(workOrderTableModel, editedOrder, workOrderTable.getSelectedRow());
    }

    private void deleteItem(){
        int[] rows = workOrderTable.getSelectedRows();
        for(int i = rows.length - 1; i >= 0; i --){
            workOrderTableModel.removeElement(rows[i]);
        }
        deleteAllMenuItem.setEnabled(workOrderTable.getRowCount() > 0);
    }

    //Save method
    private void save(){
        if(chosenFile != null){                                     //If there is already a chosen file
            try {
                fos = new FileOutputStream(chosenFile);             //Try to open the file ouput stream
                dos = new DataOutputStream(fos);                    //Try to open the data output stream
                workOrderTableModel.saveTo(dos);
                dos.close();
                fos.close();
            } catch (IOException o){
                JOptionPane.showMessageDialog(this, "Error saving file!");               //If there is a problem using the streams
            }
        } else {                                                    //If there is not a chosen file
            saveAs();                                               //Treat "save" as "save-as"
        }
    }

    public void mousePressed(MouseEvent e){
        showPopup(e);
    }

    public void mouseEntered(MouseEvent e){

    }

    public void mouseReleased(MouseEvent e){
        showPopup(e);
    }

    public void mouseDragged(MouseEvent e){

    }

    public void mouseClicked(MouseEvent e){
        showPopup(e);
    }

    public void mouseMoved(MouseEvent e){

    }

    public void mouseExited(MouseEvent e){

    }

    private void showPopup(MouseEvent e){
        mousePos = e.getPoint();
        if(e.isPopupTrigger()){
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
        
    }

    public void dropActionChanged(DropTargetDragEvent e){
        
    }

    public void dragExit(DropTargetEvent e){
        System.out.println("NOT HOVERING");
        workOrderTable.setBackground(Color.getHSBColor(22, 122, 122));
    }

    public void drop(DropTargetDropEvent e){
        System.out.println("DROPPED!");
        java.util.List<File> files;
        DefaultListModel<String> fileNames = new DefaultListModel<String>();
        Transferable transferableData;
        
        transferableData = e.getTransferable();

        try {
            if(transferableData.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
                e.acceptDrop(DnDConstants.ACTION_COPY);
                files = (java.util.List<File>)transferableData.getTransferData(DataFlavor.javaFileListFlavor);
                load(files);
            }
        }catch(UnsupportedFlavorException uf){
            System.out.println("Unsupported file");
        }catch(IOException io){
            System.out.println("Bad io exception.");
        }

    }
    public void dragOver(DropTargetDragEvent e){
        
    }
    public void dragEnter(DropTargetDragEvent e){
        System.out.println("HOVERING");
        workOrderTable.setBackground(Color.getHSBColor(122, 22, 22));
    }
}
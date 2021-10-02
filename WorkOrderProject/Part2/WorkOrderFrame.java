import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.io.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class WorkOrderFrame extends JFrame implements ActionListener, ListSelectionListener, DataManager {
    JPanel buttonPanel;
    JPanel scrollerPanel;

    JMenuBar menuBar;
    JMenu fileOptionsMenu;
    JMenu itemOptionsMenu;

    WorkOrderModel workOrderModel;
    JList<WorkOrder> workOrderList;
    JScrollPane scroller;

    //These menu items are in a wider scope so we can disable them within the methods
    JMenuItem deleteMenuItem;
    JMenuItem deleteAllMenuItem;
    JMenuItem editMenuItem;

    JButton deleteButton;
    JButton exitButton;
    JButton editButton;

    JFileChooser fileChooser;
    File chosenFile;

    DataInputStream dis;
    DataOutputStream dos;
    FileInputStream fis;
    FileOutputStream fos;

    WorkOrderFrame(){
        initIO();
        initList();
        initButtons();
        initMenuBar();
        setUpFrame();
        // Un-comment to add random WorkOrders to list
        // for(int i = 0; i < 10; i++){
        //     workOrderModel.addElement(WorkOrder.getRandom());
        // }
    }

    private void initIO(){
        fileChooser = new JFileChooser(".");
    }
    
    private void initList(){
        scrollerPanel = new JPanel();

        workOrderModel = new WorkOrderModel();   //Init the list model, which contains the strings
        
        workOrderList = new JList<WorkOrder>(workOrderModel);    //Init the JList which displays the list model
        workOrderList.addListSelectionListener(this);          //Add the JList to the panel

        scroller = new JScrollPane(workOrderList);             //Create scroller from the workOrderList
        scrollerPanel.add(scroller);

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
        
        //Add buttons to buttonPanel
        buttonPanel.add(createButton("Load", "LOAD", this));
        buttonPanel.add(createButton("Save", "SAVE", this));
        buttonPanel.add(createButton("Save As", "SAVEAS", this));
        buttonPanel.add(createButton("Add", "NEW", this));
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
            workOrderModel.removeAllElements();                    //Simple enough (I think??)
            deleteAllMenuItem.setEnabled(workOrderModel.size() > 0);
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
    } 

    public void valueChanged(ListSelectionEvent e){
        if(e.getSource() == workOrderList){
            deleteMenuItem.setEnabled(workOrderList.getSelectedIndices().length > 0);
            deleteButton.setEnabled(workOrderList.getSelectedIndices().length > 0);
            editButton.setEnabled(workOrderList.getSelectedIndex() >= 0);
            editMenuItem.setEnabled(workOrderList.getSelectedIndex() >= 0);
        }
    }

    private void saveAs(){
        int option = fileChooser.showSaveDialog(this);              //Show save dialog and get the button chosens

        if(option == JFileChooser.APPROVE_OPTION){                  //If the user pressed save
            try {
                chosenFile = fileChooser.getSelectedFile();
                fos = new FileOutputStream(chosenFile);             //Try to open up file output stream
                dos = new DataOutputStream(fos);                    //Try to open data output stream
                workOrderModel.saveTo(dos);
                dos.close();                                        //Close data stream
                fos.close();                                        //Close file output stream
            } catch (IOException o){
                JOptionPane.showMessageDialog(this, "Error saving file!");
            }
        }
    }

    private void load(){
        int option = fileChooser.showOpenDialog(this);              //Show the open dialog and get the option from the button pressed

        if(option == JFileChooser.APPROVE_OPTION){                  //If the user pressed load
            try {
                deleteAllMenuItem.doClick();
                chosenFile = fileChooser.getSelectedFile();
                fis = new FileInputStream(chosenFile);              //Try to open the file input stream
                dis = new DataInputStream(fis);                     //Try to open up the data stream
                workOrderModel.loadFrom(dis);
                dis.close();                                        //Close input stream
                fis.close();                                        //Close file input stream
            } catch(IOException o){
                chosenFile = null;
                JOptionPane.showMessageDialog(this, "Error loading file!");
            }
        }
        deleteAllMenuItem.setEnabled(workOrderModel.getSize() > 0);
    }

    private void newItem(){
        WorkOrderDialog addDialog = new WorkOrderDialog(this);      //Create the instance of WorkOrderDialog in the add mode
        deleteAllMenuItem.setEnabled(workOrderModel.getSize() > 0); //Enable the button based on list size
    }

    private void deleteItem(){
        int[] indices = workOrderList.getSelectedIndices();             //Get the indices that are selected
        for(int i = indices.length - 1; i >= 0; i--){                   //Loop through them backwards
            workOrderModel.remove(indices[i]);                          //Delete the items at index i
        }
        deleteAllMenuItem.setEnabled(workOrderModel.getSize() > 0);     //Enable the delete all button based on the workOrderModel contents
    }

    //Save method
    private void save(){
        if(chosenFile != null){                                     //If there is already a chosen file
            try {
                fos = new FileOutputStream(chosenFile);             //Try to open the file ouput stream
                dos = new DataOutputStream(fos);                    //Try to open the data output stream
                workOrderModel.saveTo(dos);
                dos.close();
                fos.close();
            } catch (IOException o){
                JOptionPane.showMessageDialog(this, "Error saving file!");               //If there is a problem using the streams
            }
        } else {                                                    //If there is not a chosen file
            saveAs();                                               //Treat "save" as "save-as"
        }
    }

    private void editItem(){
        WorkOrder editedOrder = workOrderModel.get(workOrderList.getSelectedIndex());   //When a user clicks edit, get the item that they have selected
        WorkOrderDialog editDialog = new WorkOrderDialog(this, editedOrder, workOrderList.getSelectedIndex());  //With this selection pass it to the constructor of the Dialog
    }

    public void AddItem(WorkOrder w){
        workOrderModel.addElement(w);
    }

    public void ReplaceItem(WorkOrder newOrder, int oldOrderIndex){
        workOrderModel.set(oldOrderIndex, newOrder);
    }
}
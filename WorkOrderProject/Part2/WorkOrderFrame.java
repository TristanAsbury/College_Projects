import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.io.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

public class WorkOrderFrame extends JFrame implements ActionListener, ListSelectionListener {
    JPanel buttonPanel;
    JPanel scrollerPanel;

    JMenuBar menuBar;
    JMenu fileOptionsMenu;
    JMenu itemOptionsMenu;

    DefaultListModel<String> stringListModel;
    JList<String> stringList;
    JScrollPane scroller;

    //These menu items are in a wider scope so we can disable them within the methods
    JMenuItem deleteMenuItem;
    JMenuItem deleteAllMenuItem;

    JButton deleteButton;
    JButton exitButton;

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
    }

    private void initIO(){
        fileChooser = new JFileChooser(".");
    }
    
    private void initList(){
        scrollerPanel = new JPanel();

        stringListModel = new DefaultListModel<String>();   //Init the list model, which contains the strings
        
        stringList = new JList<String>(stringListModel);    //Init the JList which displays the list model
        stringList.addListSelectionListener(this);          //Add the JList to the panel

        scroller = new JScrollPane(stringList);             //Create scroller from the stringList
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
        deleteMenuItem = createMenuItem("Delete", "DELETE", this, KeyEvent.VK_D, KeyEvent.VK_D, "Delete.");
        deleteMenuItem.setEnabled(false);
        deleteAllMenuItem = createMenuItem("Delete All", "DELETEALL", this, KeyEvent.VK_F, KeyEvent.VK_F, "Delete all.");
        deleteAllMenuItem.setEnabled(false);

        //Add "item" options menu items to itemOptionsMenu
        itemOptionsMenu.add(createMenuItem("New", "NEW", this, KeyEvent.VK_N, KeyEvent.VK_N, "New item."));
        itemOptionsMenu.add(deleteMenuItem);
        itemOptionsMenu.add(deleteAllMenuItem);

        //Add JMenus to JMenuBar
        menuBar.add(fileOptionsMenu);
        menuBar.add(itemOptionsMenu);

        add(menuBar, BorderLayout.NORTH);
    }

    private void initButtons(){
        buttonPanel = new JPanel(); //Init Panel

        //Init buttons
        deleteButton = createButton("Delete", "DELETE", this);
        deleteButton.setEnabled(false);
        exitButton = createButton("Exit", "EXIT", this);
        
        //Add buttons to buttonPanel
        buttonPanel.add(createButton("Load", "LOAD", this));
        buttonPanel.add(createButton("Save", "SAVE", this));
        buttonPanel.add(createButton("Save As", "SAVEAS", this));
        buttonPanel.add(createButton("Add", "NEW", this));
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
        setTitle("Part One");
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
            stringListModel.removeAllElements();                    //Simple enough (I think??)
            deleteAllMenuItem.setEnabled(stringListModel.size() > 0);
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

        if(e.getActionCommand().equals("EXIT")){                    //IF THE USER PRESSES EXIT
            System.exit(0);
        }
    } 

    public void valueChanged(ListSelectionEvent e){
        if(e.getSource() == stringList){
            deleteMenuItem.setEnabled(stringList.getSelectedIndices().length > 0);
            deleteButton.setEnabled(stringList.getSelectedIndices().length > 0);
        }
    }

    private void saveAs(){
        int option = fileChooser.showSaveDialog(this);              //Show save dialog and get the button chosens

        if(option == JFileChooser.APPROVE_OPTION){                  //If the user pressed save
            try {
                chosenFile = fileChooser.getSelectedFile();
                fos = new FileOutputStream(chosenFile);             //Try to open up file output stream
                dos = new DataOutputStream(fos);                    //Try to open data output stream
                for(int i = 0; i < stringListModel.size(); i++){ 
                        dos.writeUTF(stringListModel.get(i) + " "); //Write each string from the list into the file
                }
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
                chosenFile = fileChooser.getSelectedFile();
                fis = new FileInputStream(chosenFile);              //Try to open the file input stream
                dis = new DataInputStream(fis);                     //Try to open up the data stream
                while(dis.available() > 0){                         //If there is data left to be read
                    String tempString = dis.readUTF();              //Read the string from the file
                    stringListModel.add(stringListModel.getSize(), tempString); //Add the string that was just read
                }
                dis.close();                                        //Close input stream
                fis.close();                                        //Close file input stream
            } catch(IOException o){
                chosenFile = null;
                JOptionPane.showMessageDialog(this, "Error loading file!");
                
            }
        }
        deleteAllMenuItem.setEnabled(stringListModel.getSize() > 0);
    }

    private void newItem(){
        //Will be set to a string if the user inputs and presses okay, or null if the user presses cancel
        
        WorkOrder inputWorkOrder = null;
        // WorkOrderDialog inputDialog = new WorkOrderDialog(inputWorkOrder);
        String inputName = JOptionPane.showInputDialog(this, "Please input name:");
        
        if(inputName != null && !inputName.trim().equals("")){      //If the user actually put in a string
            stringListModel.add(stringListModel.size(), inputName); //Add the string to the list of strings
        }
        deleteAllMenuItem.setEnabled(stringListModel.getSize() > 0); //Enable the button based on list size
    }

    private void deleteItem(){
        int[] indices = stringList.getSelectedIndices();            //Get the indices that are selected
        for(int i = indices.length - 1; i >= 0; i--){               //Loop through them backwards
            stringListModel.remove(indices[i]);                     //Delete the items at i indices
        }
        deleteAllMenuItem.setEnabled(stringListModel.getSize() > 0); //Delete all menu items at selected indices
    }

    private void save(){
        if(chosenFile != null){                                     //If there is already a chosen file
            try {
                fos = new FileOutputStream(chosenFile);             //Try to open the file ouput stream
                dos = new DataOutputStream(fos);                    //Try to open the data output stream
                for(int i = 0; i < stringListModel.size(); i++){    //For each item in the list of strings
                    dos.writeUTF(stringListModel.get(i) + " ");     //Write to the file
                }
                dos.close();
                fos.close();
            } catch (IOException o){
                JOptionPane.showMessageDialog(this, "Error saving file!");               //If there is a problem using the streams
            }
        } else {                                                    //If there is not a chosen file
            saveAs();                                               //Treat "save" as "save-as"
        }
    }
}
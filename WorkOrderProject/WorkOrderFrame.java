import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.io.*;

public class WorkOrderFrame extends JFrame implements ActionListener {
    
    JPanel buttonPanel;
    JPanel listPanel;

    JButton loadButton;
    JButton saveButton;
    JButton saveAsButton;
    JButton addButton;
    JButton deleteButton;
    JButton exitButton;

    JMenuBar menuBar;
    JMenu fileOptionsMenu;
    JMenu itemOptionsMenu;

    JOptionPane addNewPane;

    DefaultListModel<String> stringListModel;
    JList stringList;

    JFileChooser fileChooser;
    File chosenFile;

    DataInputStream dis;
    DataOutputStream dos;
    FileInputStream fis;
    FileOutputStream fos;

    WorkOrderFrame(){
        addNewPane = new JOptionPane();
        initIO();
        initButtons();
        initList();
        initMenuBar();
        setUpFrame();
    }

    private void initIO(){
        fileChooser = new JFileChooser(".");
    }

    private void initButtons(){
        //Init buttons
        loadButton = createButton("Load", "LOAD", this);
        saveButton = createButton("Save", "SAVE", this);
        saveAsButton = createButton("Save As", "SAVEAS", this);
        addButton = createButton("Add", "NEW", this);
        deleteButton = createButton("Delete", "DELETE", this);
        exitButton = createButton("Exit", "EXIT", this);

        //Init Panel
        buttonPanel = new JPanel();

        //Add buttons to buttonPanel
        buttonPanel.add(loadButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(saveAsButton);
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exitButton);
        
        //Add the button panel
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void initList(){
        listPanel = new JPanel();

        //Init the list model, which contains the strings
        stringListModel = new DefaultListModel<String>();
        
        //Init the JList which displays the list model
        stringList = new JList<String>(stringListModel);
        //Add the JList to the panel
        listPanel.add(stringList);

        add(listPanel, BorderLayout.CENTER);
    }

    private void initMenuBar(){
        menuBar = new JMenuBar();
        fileOptionsMenu = new JMenu("File");
        itemOptionsMenu = new JMenu("Item");

        //Create "file" options menu items
        JMenuItem loadMenuItem = createMenuItem("Load", "LOAD", this, KeyEvent.VK_L, KeyEvent.VK_L, "Load a file.");
        JMenuItem saveMenuItem = createMenuItem("Save", "SAVE", this, KeyEvent.VK_S, KeyEvent.VK_S, "Save a file.");
        JMenuItem saveAsMenuItem = createMenuItem("Save As", "SAVEAS", this, KeyEvent.VK_A, KeyEvent.VK_A, "Save as...");

        //Add "file" options menu items to fileOptionsMenu
        fileOptionsMenu.add(loadMenuItem);
        fileOptionsMenu.add(saveMenuItem);
        fileOptionsMenu.add(saveAsMenuItem);

        //Create "item" options menu items
        JMenuItem newMenuItem = createMenuItem("New", "NEW", this, KeyEvent.VK_N, KeyEvent.VK_N, "New item.");
        JMenuItem deleteMenuItem = createMenuItem("Delete", "DELETE", this, KeyEvent.VK_D, KeyEvent.VK_D, "Delete.");
        JMenuItem deleteAllMenuItem = createMenuItem("Delete All", "DELETEALL", this, KeyEvent.VK_F, KeyEvent.VK_F, "Delete all.");

        //Add "item" options menu items to itemOptionsMenu
        itemOptionsMenu.add(newMenuItem);
        itemOptionsMenu.add(deleteMenuItem);
        itemOptionsMenu.add(deleteAllMenuItem);

        //Add JMenus to JMenuBar
        menuBar.add(fileOptionsMenu);
        menuBar.add(itemOptionsMenu);

        add(menuBar, BorderLayout.NORTH);
    }    

    private void setUpFrame(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize((int)d.getWidth()/2, (int)d.getHeight()/2);
        setVisible(true);
        setTitle("Part One");
    }

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
        //IF THE USER IS DELETING A USER
        if(e.getActionCommand().equals("DELETE")){
            //Make sure the stringList has something selected 
            if(stringList.getSelectedIndex() != -1){
                stringListModel.remove(stringList.getSelectedIndex());
            }
        }

        //IF THE USER IS DELETING ALL THE USERS
        if(e.getActionCommand().equals("DELETEALL")){
            //Simple enough (I think??)
            stringListModel.removeAllElements();
        }

        //IF THE USER IS LOADING FROM A FILE
        if(e.getActionCommand().equals("LOAD")){
            //Show the open dialog and get the option from the button pressed
            int option = fileChooser.showOpenDialog(this);
            
            //If the user pressed load
            if(option == JFileChooser.APPROVE_OPTION){
                chosenFile = fileChooser.getSelectedFile();
                try {
                    fis = new FileInputStream(chosenFile);
                    dis = new DataInputStream(fis);
                    while(dis.available() > 0){
                        String tempString = dis.readUTF();
                        stringListModel.add(0, tempString);
                    }
                    dis.close();
                    fis.close();
                } catch(IOException o){
                    System.out.println("Couldn't load file!");
                }
            }
        }

        //IF THE USER IS ADDING A NEW USER
        if(e.getActionCommand().equals("NEW")){
            //Will be set to a string if the user inputs and presses okay, or null if the user presses cancel
            String inputName = addNewPane.showInputDialog(this, "Please input name:");

            //If valid
            if(inputName != null && !inputName.trim().equals("")){
                stringListModel.add(stringListModel.size(), inputName);
            } else {
                System.out.println("Cancel");
            }
        }
        
        if(e.getActionCommand().equals("SAVE")){
            //If there is already a chosen file
            if(chosenFile != null){
                try {
                    fos = new FileOutputStream(chosenFile);
                    dos = new DataOutputStream(fos);
                    for(int i = 0; i < stringListModel.size(); i++){
                        dos.writeUTF(stringListModel.get(i) + " ");
                    }
                } catch (IOException o){
                    System.out.println("Error saving file.");
                }
            //If there is not a chosen file
            } else {
                //Get option, whether the user pressed save or cancel
                int option = fileChooser.showSaveDialog(this);
                //If user pressed save
                if(option == JFileChooser.APPROVE_OPTION){
                    //Get the file they selected
                    chosenFile = fileChooser.getSelectedFile();
                    try {
                        fos = new FileOutputStream(chosenFile);
                        dos = new DataOutputStream(fos);
                        for(int i = 0; i < stringListModel.size(); i++){
                            dos.writeUTF(stringListModel.get(i) + " ");
                        }
                    } catch (IOException o){
                        System.out.println("Couldn't save to file.");
                    }
                }
            }
        }

        //IF THE USER IS SAVING A FILE AS
        if(e.getActionCommand().equals("SAVEAS")){
            //Show save dialog and get the button chosen
            int option = fileChooser.showSaveDialog(this);

            //If the user pressed save
            if(option == JFileChooser.APPROVE_OPTION){
                chosenFile = fileChooser.getSelectedFile();
                try {
                    fos = new FileOutputStream(chosenFile);
                    dos = new DataOutputStream(fos);
                    for(int i = 0; i < stringListModel.size(); i++){
                            dos.writeUTF(stringListModel.get(i) + " ");
                    }
                    dos.close();
                    fos.close();
                } catch (IOException o){
                    System.out.println("Error Saving File");
                }
            }
        }

        if(e.getActionCommand().equals("EXIT")){
            dispose();
        }
    }  
}
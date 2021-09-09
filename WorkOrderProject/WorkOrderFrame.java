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

    private void setUpFrame(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize((int)d.getWidth()/2, (int)d.getHeight()/2);
        setVisible(true);
        setTitle("Work Orders");
    }
    
    private void initButtons(){
        //Init buttons
        loadButton = new JButton("Load");
        loadButton.setActionCommand("LOAD");
        loadButton.addActionListener(this);

        saveButton = new JButton("Save");
        saveButton.setActionCommand("SAVE");
        saveButton.addActionListener(this);

        saveAsButton = new JButton("Save As");
        saveAsButton.setActionCommand("SAVEAS");
        saveAsButton.addActionListener(this);

        addButton = new JButton("Add");
        addButton.setActionCommand("NEW");
        addButton.addActionListener(this);

        deleteButton = new JButton("Delete");
        deleteButton.setActionCommand("DELETE");
        deleteButton.addActionListener(this);

        exitButton = new JButton("Exit");
        exitButton.setActionCommand("DELETE");
        exitButton.addActionListener(this);

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
        add(buttonPanel, BorderLayout.WEST);
    }
    
    private void initList(){
        listPanel = new JPanel();

        //Init the list model, which contains the strings
        stringListModel = new DefaultListModel<String>();
        
        //Init the JList which displays the list model
        stringList = new JList<String>(stringListModel);

        //Add the JList to the panel
        listPanel.add(stringList);

        add(listPanel, BorderLayout.EAST);
    }

    private void initMenuBar(){
        menuBar = new JMenuBar();
        fileOptionsMenu = new JMenu("File");
        itemOptionsMenu = new JMenu("Item");

        //Create "file" options menu items
        JMenuItem loadMenuItem = new JMenuItem("Load");
        loadMenuItem.setActionCommand("LOAD");
        loadMenuItem.addActionListener(this);

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setActionCommand("SAVE");
        saveMenuItem.addActionListener(this);

        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        saveAsMenuItem.setActionCommand("SAVEAS");
        saveAsMenuItem.addActionListener(this);

        //Add "file" options menu items to fileOptionsMenu
        fileOptionsMenu.add(loadMenuItem);
        fileOptionsMenu.add(saveMenuItem);
        fileOptionsMenu.add(saveAsMenuItem);

        //Create "item" options menu items
        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.setActionCommand("NEW");
        newMenuItem.addActionListener(this);

        JMenuItem deleteMenuItem = new JMenuItem("Delete");
        deleteMenuItem.setActionCommand("DELETE");
        deleteMenuItem.addActionListener(this);

        JMenuItem deleteAllMenuItem = new JMenuItem("Delete All");
        deleteAllMenuItem.setActionCommand("DELETEALL");
        deleteAllMenuItem.addActionListener(this);

        //Add "item" options menu items to itemOptionsMenu
        itemOptionsMenu.add(newMenuItem);
        itemOptionsMenu.add(deleteMenuItem);
        itemOptionsMenu.add(deleteAllMenuItem);

        //Add JMenus to JMenuBar
        menuBar.add(fileOptionsMenu);
        menuBar.add(itemOptionsMenu);

        add(menuBar, BorderLayout.NORTH);
    }

    private void initIO(){
        fileChooser = new JFileChooser(".");
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

        //IF THE USER IS ADDING A NEW USER
        if(e.getActionCommand().equals("NEW")){
            String inputName = addNewPane.showInputDialog(this, "Please input name");
            while(inputName.trim().equals("")){
                inputName = addNewPane.showInputDialog(this, "Please input name");
            }
            //Add the input name to the list
            stringListModel.add(stringListModel.size(), inputName);
        }

        //IF THE USER IS LOADING FROM A FILE
        if(e.getActionCommand().equals("LOAD")){
            fileChooser.showOpenDialog(this);
            chosenFile = fileChooser.getSelectedFile();
            
            boolean fileIsGood = true;
            try {
                fis = new FileInputStream(chosenFile);
                dis = new DataInputStream(fis);
            } catch(IOException o){
                System.out.println("Couldn't open file!");
                fileIsGood = false;
            }
            if(fileIsGood){
                try {
                    int pos = 0;
                    while(dis.available() > 0){
                        String tempString = dis.readUTF();
                        stringListModel.add(0, tempString);
                    }
                } catch (IOException o){
                    System.out.println("Error reading file");
                }
            }
            try {
                dis.close();
                fis.close();
            } catch (IOException o){
                System.out.println("Couldn't close file or data stream");
            }
        }
        
        //IF THE USER IS SAVING A FILE AS
        if(e.getActionCommand().equals("SAVEAS")){
            fileChooser.showSaveDialog(this);
            chosenFile = fileChooser.getSelectedFile();

            boolean fileIsGood = true;
            try {
                fos = new FileOutputStream(chosenFile);
                dos = new DataOutputStream(fos);
            } catch (IOException o){
                System.out.println("File doesn't exist");
                fileIsGood = false;
            }
            if(fileIsGood){
                for(int i = 0; i < stringListModel.size(); i++){
                    try {
                        dos.writeUTF(stringListModel.get(i) + " ");
                    } catch (IOException o){
                        System.out.println("Couldn't insert info");
                    }
                }
            }
            try {
                dos.close();
                fos.close();
            } catch (IOException o){
                System.out.println("Couldn't close file or data stream");
            }
        }

        if(e.getActionCommand().equals("SAVE")){
            if(chosenFile != null){
                try {
                    fos = new FileOutputStream(chosenFile);
                    dos = new DataOutputStream(fos);
                    for(int i = 0; i < stringListModel.size(); i++){
                        dos.writeUTF(stringListModel.get(i) + " ");
                    }
                } catch (IOException o){
                    System.out.println("File doesn't exist");
                }
            } else {
                fileChooser.showSaveDialog(this);
                chosenFile = fileChooser.getSelectedFile();
            }
        }
    }
}

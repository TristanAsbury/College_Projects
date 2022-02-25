import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.*;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;


public class PropertiesDialog extends JDialog implements ActionListener {
    GroupLayout inputLayout;

    JPanel inputPanel;
    JPanel buttonPanel;

    JButton saveButton;

    JLabel serverNameLabel, usernameLabel, passwordLabel, intervalLabel, soundLabel;
    JTextField serverNameInput, usernameInput;
    JPasswordField passwordInput;
    JSpinner intervalInput;
    JCheckBox soundInput;

    Properties props;

    public PropertiesDialog(Properties props){ //For adding
        this.props = props;
        createGUI();
        setUp();
    }
    
    private void createGUI(){
        serverNameLabel = new JLabel("IMAP Server Name:");
        usernameLabel = new JLabel("Username:");
        passwordLabel = new JLabel("Password:");
        intervalLabel = new JLabel("Time (minutes)");
        soundLabel = new JLabel("Notification Sound:");

        serverNameInput = new JTextField("imap.gmx.com");
        usernameInput = new JTextField("tasbury07@gmx.com");
        passwordInput = new JPasswordField("tasbury07");
        SpinnerModel intervalModel = new SpinnerNumberModel(1, 1, 60, 1);
        intervalInput = new JSpinner(intervalModel);
        
        soundInput = new JCheckBox();

        
        saveButton = new JButton("Submit And Continue");
        saveButton.addActionListener(this);
        
        inputPanel = new JPanel();
        buttonPanel = new JPanel();
        
        buttonPanel.add(saveButton);

        inputLayout = new GroupLayout(inputPanel);
        inputPanel.setLayout(inputLayout);
        inputLayout.setAutoCreateGaps(true);
        inputLayout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup hGroup = inputLayout.createSequentialGroup();
        hGroup.addGroup(inputLayout.createParallelGroup().
                    addComponent(serverNameLabel)
                    .addComponent(usernameLabel)
                    .addComponent(passwordLabel)
                    .addComponent(intervalLabel)
                    .addComponent(soundLabel));
        hGroup.addGroup(inputLayout.createParallelGroup().
                    addComponent(serverNameInput)
                    .addComponent(usernameInput)
                    .addComponent(passwordInput)
                    .addComponent(intervalInput)
                    .addComponent(soundInput));
        inputLayout.setHorizontalGroup(hGroup);
        GroupLayout.SequentialGroup vGroup = inputLayout.createSequentialGroup();
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(serverNameLabel)
                    .addComponent(serverNameInput));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(usernameLabel)
                    .addComponent(usernameInput));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(passwordLabel)
                    .addComponent(passwordInput));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(intervalLabel)
                    .addComponent(intervalInput));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(soundLabel)
                    .addComponent(soundInput));
        inputLayout.setVerticalGroup(vGroup);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public void actionPerformed(ActionEvent e){
        if(e.getSource().equals(saveButton)){
            //When the user presses save
            //if there exists a properties file, then edit the properties, else, create the properties file, and then edit them
            props.put("host", serverNameInput.getText());
            props.put("username", usernameInput.getText());
            props.put("password", passwordInput.getText());
            props.put("protocolProvider", "imap");
            props.put("interval", intervalInput.getValue().toString());
            props.put("notisound", String.valueOf(soundInput.isSelected()));
            try {
                props.store(new DataOutputStream(new FileOutputStream("props.properties")), null);
            } catch(IOException io){
                System.out.println("Error saving props file!");
            }
            
            dispose();
        }
    }

    private void setUp(){
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize((int)d.getWidth()/5, (int)d.getHeight()/5);
        setLocation((int)d.getWidth()/5, (int)d.getHeight()/5);
        setTitle("Properties");
        setVisible(true);
    }
}

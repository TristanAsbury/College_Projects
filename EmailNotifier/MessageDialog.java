import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class MessageDialog extends JDialog implements ActionListener {
    GroupLayout inputLayout;

    JPanel messagePanel;
    JPanel buttonPanel;

    JButton okayButton;
    boolean messageSeen;

    Folder mailFolder;
    int newMessageCount;
    JLabel mainLabel;

    public MessageDialog(Boolean messageSeen, Folder mailFolder, int newMessageCount){ //For adding
        this.messageSeen = messageSeen;
        this.mailFolder = mailFolder;
        this.newMessageCount = newMessageCount;

        createGUI();
        setUp();
    }
    
    private void createGUI(){
        try {
            mainLabel = new JLabel("NEW MESSAGES: " + newMessageCount + "\n"
            + "MOST RECENT MESSAGE: " + mailFolder.getMessage(mailFolder.getMessageCount()).getSubject());
        } catch (MessagingException me){
            System.out.println("Problem reading mail!");
        }

        okayButton = new JButton("Okay!");
        okayButton.addActionListener(this);
        
        messagePanel = new JPanel();
        buttonPanel = new JPanel();

        messagePanel.add(mainLabel);
        buttonPanel.add(okayButton);

        add(messagePanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public void actionPerformed(ActionEvent e){
        if(e.getSource().equals(okayButton)){
            messageSeen = true; //Send back that we have seen the message
            dispose();
        }
    }

    private void setUp(){
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize((int)d.getWidth()/5, (int)d.getHeight()/5);
        setLocation((int)d.getWidth()/5, (int)d.getHeight()/5);
        setTitle("Properties");
        setVisible(true);
    }
}

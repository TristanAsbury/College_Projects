import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;


public class MessageDialog extends JDialog implements ActionListener {
    GroupLayout inputLayout;

    JPanel messagePanel;
    JPanel buttonPanel;

    JButton okayButton;

    Folder mailFolder;
    JLabel newMessagesLabel;
    JLabel fromLabel;
    JLabel subjectLabel;
    JLabel messageLabel;

    public MessageDialog(Folder mailFolder){ //For adding
        this.mailFolder = mailFolder;
        createGUI();
        setUp();
    }
    
    private void createGUI(){
        try {
            Message recentMessage = mailFolder.getMessage(mailFolder.getMessageCount());
            newMessagesLabel = new JLabel("NEW MESSAGES: " + mailFolder.getNewMessageCount());
            fromLabel = new JLabel("From: " + recentMessage.getFrom()[0].toString());
            subjectLabel = new JLabel("Subject: " + recentMessage.getSubject());
            messageLabel = new JLabel("Message: " + recentMessage.getContent().toString());

            messagePanel = new JPanel();
            buttonPanel = new JPanel();

            inputLayout = new GroupLayout(messagePanel);
            messagePanel.setLayout(inputLayout);
            inputLayout.setAutoCreateGaps(true);
            inputLayout.setAutoCreateContainerGaps(true);

            GroupLayout.SequentialGroup hGroup = inputLayout.createSequentialGroup();
            hGroup.addGroup(inputLayout.createParallelGroup().
                        addComponent(newMessagesLabel)
                        .addComponent(fromLabel)
                        .addComponent(subjectLabel)
                        .addComponent(messageLabel));
            inputLayout.setHorizontalGroup(hGroup);
            GroupLayout.SequentialGroup vGroup = inputLayout.createSequentialGroup();
            vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                        addComponent(newMessagesLabel));
            vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                        addComponent(fromLabel));
            vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                        addComponent(subjectLabel));
            vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                        addComponent(messageLabel));
            inputLayout.setVerticalGroup(vGroup);

            okayButton = new JButton("Okay!");
            okayButton.addActionListener(this);
            
            
            buttonPanel.add(okayButton);
            add(messagePanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);

        } catch (MessagingException me){
            System.out.println("Problem reading mail!");
        } catch (IOException io){
            System.out.println("IO Boi");
        }

        

    }
    
    public void actionPerformed(ActionEvent e){
        if(e.getSource().equals(okayButton)){
            try {
                mailFolder.close(false); //Close the folder to show that we have read all new messages
                mailFolder.open(Folder.READ_WRITE); //Open the folder again to reset
            } catch (MessagingException me){
                System.out.println("Problem closing folder!");
            }
            dispose();      //Close the window
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

//package Client;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame implements ActionListener, DocumentListener {
    ConnectionToServer cts;

    JPanel inputPanel;
    JLabel usernameLabel;
    JTextField inputField;
    JButton sendButton;

    JPanel outputPanel;
    JLabel messageLabel;

    JPanel exitPanel;
    JButton exitButton;

    public GUI(String id){
        messageLabel = new JLabel("");
        usernameLabel = new JLabel("Send as " + id + ":");

        cts = new ConnectionToServer(id, messageLabel);   //Create connection to the server.
        inputPanel = new JPanel();      //Create input panel
        outputPanel = new JPanel();     //Create ouput panel (the label of the message)            
        exitPanel = new JPanel();       //Create the exit panel (for the exit button)

        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);

        inputField = new JTextField(20);
        inputField.getDocument().addDocumentListener(this);

        sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        sendButton.addActionListener(this);

        inputPanel.add(usernameLabel);
        inputPanel.add(inputField);
        inputPanel.add(sendButton);
        outputPanel.add(messageLabel);

        exitPanel.add(exitButton, BorderLayout.SOUTH);

        add(inputPanel, BorderLayout.NORTH);
        add(outputPanel, BorderLayout.CENTER);
        add(exitPanel, BorderLayout.SOUTH);

        setupFrame();
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == sendButton){    //If the send button was pushed
            cts.send(inputField.getText()); //Get text, and if it's not empty, send it
        }

        if(e.getSource() == exitButton){    //If the exit button was pressed
            System.exit(0);                 //Exit the program
        }
    }

    public void insertUpdate(DocumentEvent e){ 
        if(e.getDocument() == inputField.getDocument()){
            sendButton.setEnabled(!inputField.getText().trim().equals("")); //If there is text, set the button to true, otherwise, its false
        }
    }

    public void removeUpdate(DocumentEvent e){ 
        if(e.getDocument() == inputField.getDocument()){
            sendButton.setEnabled(!inputField.getText().trim().equals("")); //If there is text, set the button to true, otherwise, its false
        }
    }

    public void changedUpdate(DocumentEvent e){ }

    private void setupFrame(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize((int)d.getWidth()/4, (int)d.getHeight()/4);
        setLocation((int)d.getWidth()/4, (int)d.getHeight()/4);
        setTitle("Living Things");
        setVisible(true);
    }
}

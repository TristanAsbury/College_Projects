package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame implements ActionListener {
    ConnectionToServer cts;

    JTextField inputField;
    JButton sendButton;
    JPanel inputPanel;
    JLabel message;


    public GUI(String id){
        cts = new ConnectionToServer(id);   //Create connection to the server.
        inputPanel = new JPanel();

        inputField = new JTextField(20);
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        message = new JLabel("");

        inputPanel.add(inputField);
        inputPanel.add(sendButton);
        inputPanel.add(message);

        add(inputPanel);
        setupFrame();
    }

    private void setupFrame(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize((int)d.getWidth()/2, (int)d.getHeight()/2);
        setLocation((int)d.getWidth()/4, (int)d.getHeight()/4);
        setTitle("Living Things");
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e){
        //If the send button was pushed
        if(e.getSource() == sendButton){
            //Get text, and if it's not empty, send it
            if(inputField.getText().trim() != ""){
                cts.send(inputField.getText());
            }
        }
    }
}

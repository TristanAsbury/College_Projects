import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class InputDialog extends JDialog implements ActionListener {
    GroupLayout inputLayout;

    JPanel inputPanel;
    JScrollPane messagesPane;

    JList textList;

    JTextField messageInput;
    JButton sendButton;

    Talker talker;

    public InputDialog(Talker talker){ //For adding
        this.talker = talker;
        createGUI();
        setUp();
    }
    
    private void createGUI(){
        messagesPane = new JScrollPane(textList);

        messageInput = new JTextField(20);
        sendButton = new JButton("Send Message");
        sendButton.addActionListener(this);
        
        inputPanel = new JPanel();
        
        inputPanel.add(messageInput);
        inputPanel.add(sendButton);
        

        add(inputPanel, BorderLayout.CENTER);
        add(messagesPane, BorderLayout.SOUTH);
    }
    
    public void actionPerformed(ActionEvent e){
        if(e.getSource().equals(sendButton)){   //If the user presses send
            talker.send(messageInput.getText());
        }
    }

    private void setUp(){
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize((int)d.getWidth()/4, (int)d.getHeight()/4);
        setLocation((int)d.getWidth()/4, (int)d.getHeight()/4);
        setTitle("Properties");
        setVisible(true);
    }
}

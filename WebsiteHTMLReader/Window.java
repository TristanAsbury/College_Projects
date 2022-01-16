import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.awt.Toolkit;
import java.awt.Dimension;

public class Window extends JFrame implements ActionListener {
    
    JButton goButton;
    JTextField urlField;
    JPanel inputPanel;
    JScrollPane outputPanel;
    BufferedReader webReader;
    URL url;
    DefaultListModel<String> htmlTextContainer;
    JList<String> htmlBox;
    
    public Window(){
        goButton = new JButton("Go");
        goButton.addActionListener(this);

        htmlTextContainer = new DefaultListModel<String>();
        htmlBox = new JList<String>(htmlTextContainer);

        urlField = new JTextField(20);
        inputPanel = new JPanel();
        inputPanel.add(goButton);
        inputPanel.add(urlField);
        add(inputPanel, BorderLayout.NORTH);

        outputPanel = new JScrollPane(htmlBox);
        add(outputPanel, BorderLayout.CENTER);
        setupWindow();
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == goButton){
            htmlTextContainer.removeAllElements();  //Clears JList
            String currentHTMLLine = "";
            try {
                url = new URL(urlField.getText());
                webReader = new BufferedReader(new InputStreamReader(url.openStream()));

                while(currentHTMLLine != null){
                    currentHTMLLine = webReader.readLine();
                    htmlTextContainer.addElement(currentHTMLLine);
                }
                
            } catch (MalformedURLException mue){
                JOptionPane.showMessageDialog(null, "Malformed URL! Perhaps adding an http prefix will fix it.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ioe){
                System.out.println("Problem with io");
            }
            urlField.setText("");               //Clear url box
        }
    }

    private void setupWindow(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize((int)d.getWidth()/2, (int)d.getHeight()/2);
        setLocation((int)d.getWidth()/4, (int)d.getHeight()/4);
        setTitle("Living Things");
        setVisible(true);
    }
}
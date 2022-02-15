import javax.swing.*;
import java.awt.event.*;
import java.awt.Toolkit;
import java.net.MalformedURLException;
import java.net.URL;
import java.awt.Dimension;
import java.awt.BorderLayout;

public class Window extends JFrame implements ActionListener {
    Scraper lilScraper;
    JScrollPane output;
    JButton goButton;
    JTextField urlField;
    JPanel inputPanel;
    JScrollPane outputPanel;
    URL url;
    DefaultListModel<String> htmlTextContainer;
    JList<String> htmlBox;
    String currentHTMLLine;
    

    public Window(){
        // Input UI
        goButton = new JButton("Go");
        goButton.addActionListener(this);
        urlField = new JTextField(20);
        inputPanel = new JPanel();
        inputPanel.add(goButton);
        inputPanel.add(urlField);
        add(inputPanel, BorderLayout.NORTH);
        htmlTextContainer = new DefaultListModel<String>();
        htmlBox = new JList<String>(htmlTextContainer);
        outputPanel = new JScrollPane(htmlBox);
        add(outputPanel, BorderLayout.CENTER);
        setupWindow();
    }

    private void setupWindow(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize((int)d.getWidth()/2, (int)d.getHeight()/2);
        setLocation((int)d.getWidth()/4, (int)d.getHeight()/4);
        setTitle("HTML Fetcher");
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == goButton){
            try{
                lilScraper = new Scraper(new URL(urlField.getText()));
                for(int i = 0; i < lilScraper.sites.size(); i++){
                    htmlTextContainer.addElement(lilScraper.sites.get(i).url.toString() + " DIST: " + lilScraper.sites.get(i).distance + " LINKS: " + lilScraper.sites.get(i).numLinks);
                }
            } catch(MalformedURLException mue){
                System.out.println("Invalid URL!");
            }
        }
    }
}

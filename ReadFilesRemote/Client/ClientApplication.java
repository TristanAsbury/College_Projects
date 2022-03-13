package Client;
import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.GridLayout;

public class ClientApplication extends JFrame {

    public ClientApplication() {
        //Creat layout
        setLayout(new GridLayout(100, 100));

        //Get dimensions for custom screen size
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();

        //Add the squares
        for(int i = 0; i < 10000; i++){
            LittlePanel tempPanel = new LittlePanel();
            int width = (int)d.getWidth()/3;
            tempPanel.setSize(width/100, width/100);
            add(tempPanel);
        }
        setupFrame();
    }

    private void setupFrame(){
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize((int)d.getWidth()/3, (int)d.getWidth()/3);
        setResizable(false);
        setLocation((int)d.getWidth()/4, (int)d.getHeight()/4);
        setTitle("Fun Thing!");
        setVisible(true);
    }


}

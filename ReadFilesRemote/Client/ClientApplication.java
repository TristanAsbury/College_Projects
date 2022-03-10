package Client;
import javax.swing.JFrame;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.GridLayout;

public class ClientApplication extends JFrame {

    public ClientApplication() {
        setLayout(new GridLayout(100, 100));
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();

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
        setLocation((int)d.getWidth()/4, (int)d.getHeight()/4);
        setTitle("Fun Thing!");
        setVisible(true);
    }


}

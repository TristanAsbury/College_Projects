package Client;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

public class LittlePanel extends JPanel implements MouseInputListener {

    static Random r = new Random();

    public LittlePanel(){
        addMouseListener(this);
    }

    public void mouseClicked(MouseEvent e) { }

    public void mousePressed(MouseEvent e) { }

    public void mouseReleased(MouseEvent e) { }

    public void mouseEntered(MouseEvent e) {
        setBackground(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
    }

    public void mouseExited(MouseEvent e) { }

    public void mouseDragged(MouseEvent e) { }

    public void mouseMoved(MouseEvent e) { }
    
}

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

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        setBackground(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
    }

    @Override
    public void mouseExited(MouseEvent e) { 
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
    
}

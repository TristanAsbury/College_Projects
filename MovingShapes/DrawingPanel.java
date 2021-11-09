import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Vector;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class DrawingPanel extends JPanel implements MouseInputListener {
    Vector<LivingThing> livingThings;

    public DrawingPanel(Vector<LivingThing> livingThings){
        this.livingThings = livingThings;
    }

    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2D;
        super.paintComponent(g);
        g2D = (Graphics2D)g;
        for(int i = 0; i < livingThings.size(); i++){
            LivingThing lt = livingThings.get(i);
            lt.draw(g2D);
        }
    }
    
    public void mouseEntered(MouseEvent e){ }
    public void mouseReleased(MouseEvent e){
        
    }

    public void mouseDragged(MouseEvent e){
        for(int i = 0; i < livingThings.size(); i++){
            livingThings.elementAt(i).destination = e.getPoint();
        }
    }
    public void mouseClicked(MouseEvent e){
        
    }
    public void mouseMoved(MouseEvent e){
        
    }
    public void mouseExited(MouseEvent e){
        
    }
    public void mousePressed(MouseEvent e){
        
    }
}

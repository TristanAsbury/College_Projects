import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class DrawingPanel extends JPanel implements MouseInputListener {
    Vector<LivingThing> livingThings;
    boolean tracing;

    public DrawingPanel(Vector<LivingThing> livingThings){
        this.livingThings = livingThings;
        addMouseListener(this);
        addMouseMotionListener(this);
        tracing = false;
    }
    
    public void enableTracing(boolean tracing){
        this.tracing = tracing;
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2D;
        if(!tracing){
            super.paintComponent(g);
        }
        g2D = (Graphics2D)g;
        for(int i = 0; i < livingThings.size(); i++){
            LivingThing lt = livingThings.get(i);
            lt.draw(g2D);
        }
    }
    
    public void mouseEntered(MouseEvent e){ }
    public void mouseReleased(MouseEvent e){ }

    public void mouseDragged(MouseEvent e){
        for(int i = 0; i < livingThings.size(); i++){
            livingThings.elementAt(i).chaseDone = false;
            livingThings.elementAt(i).destination = e.getPoint();
        }
    }

    public void mouseClicked(MouseEvent e){
        for(int i = 0; i < livingThings.size(); i++){
            livingThings.elementAt(i).chaseDone = false;
            livingThings.elementAt(i).destination = e.getPoint();
        }
    }

    public void mouseMoved(MouseEvent e){ }

    public void mouseExited(MouseEvent e){ }

    public void mousePressed(MouseEvent e){ }
}

import javax.swing.JPanel;
import java.util.Vector;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class DrawingPanel extends JPanel {
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
}

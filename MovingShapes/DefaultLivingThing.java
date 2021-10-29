import java.awt.Toolkit;
import java.util.Random;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Vector;
public class DefaultLivingThing extends LivingThing {

    public DefaultLivingThing(JPanel panel){
        lp = panel;
    }

    public void draw(Graphics2D g){
        g.drawPolygon(xCoord, yCoord, 4);
        g.setColor(color);
    }

    public static LivingThing getRandom(JPanel lp){
        Random r = new Random();
        DefaultLivingThing livingThing = new DefaultLivingThing(lp);
        livingThing.xSpeed = r.nextInt(20) - 10;
        livingThing.ySpeed = r.nextInt(20) - 10;
        livingThing.xAcc = r.nextFloat() * -2 + 1;
        livingThing.yAcc = 0;
        System.out.println("W: " + lp.getWidth() + " H: " + lp.getHeight());
        livingThing.xPos = r.nextInt(lp.getWidth());
        livingThing.yPos = r.nextInt(lp.getHeight());
        livingThing.angle = r.nextFloat() * 360;
        livingThing.angularAcceleration = r.nextFloat() / 2; 
        livingThing.angularSpeed = r.nextFloat();
        livingThing.lifeTime = (int)(1000 + r.nextFloat() * 3000);
        livingThing.radius = r.nextInt(20)+10;
        livingThing.color = Color.getHSBColor(r.nextInt(255), r.nextInt(255), r.nextInt(255));
        return livingThing;
    }
}

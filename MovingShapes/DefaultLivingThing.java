import java.awt.Toolkit;
import java.util.Random;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.Color;
import java.awt.Graphics2D;
public class DefaultLivingThing extends LivingThing {

    public DefaultLivingThing(JPanel panel){
        lp = panel;    
    }

    public void draw(Graphics2D g){

        g.fillRect((int)xPos, (int)yPos, radius, radius);
        g.setColor(color);
    }

    public static LivingThing getRandom(JPanel lp){
        Random r = new Random();
        DefaultLivingThing livingThing = new DefaultLivingThing(lp);
        livingThing.xSpeed = r.nextInt(4) - 2;
        livingThing.ySpeed = r.nextInt(4) - 2;
        livingThing.xAcc = r.nextFloat() * -2 + 1;
        livingThing.yAcc = r.nextFloat() * -2 + 1;
        System.out.println("W: " + lp.getWidth() + " H: " + lp.getHeight());
        livingThing.xPos = r.nextInt(lp.getWidth());
        livingThing.yPos = r.nextInt(lp.getHeight());
        livingThing.angle = r.nextFloat() * 360;
        livingThing.angularAcceleration = r.nextFloat(); 
        livingThing.angularSpeed = r.nextFloat() * 2;
        livingThing.lifeTime = (int)(50 + r.nextFloat() * 30);
        livingThing.radius = r.nextInt(20)+10;
        livingThing.color = Color.getHSBColor(r.nextInt(255), r.nextInt(255), r.nextInt(255));
        return livingThing;
    }
}

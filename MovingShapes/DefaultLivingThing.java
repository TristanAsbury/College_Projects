import java.util.Random;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics2D;

public class DefaultLivingThing extends LivingThing {

    public DefaultLivingThing(JPanel panel){
        lp = panel;
    }

    public void draw(Graphics2D g){
        //If in last 30% of life
        if(lifeTime <= 0.3*maxLifeTime && perfectOpacity - (255/lifeTime) >= 0){
            perfectOpacity -= 255 / lifeTime;
            opacity = (int)perfectOpacity;
        }
        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
        g.setColor(color);
        g.fillPolygon(xCoord, yCoord, numOfPoints*2);
    }

    public static LivingThing getRandom(JPanel lp, boolean grav, int lifeTime){
        Random r = new Random();
        DefaultLivingThing livingThing = new DefaultLivingThing(lp);
        livingThing.mode = 1;
        livingThing.outerRadius = r.nextInt(40) + 20;
        livingThing.innerRadius = (int)(livingThing.outerRadius * 0.75);
        livingThing.numOfPoints = r.nextInt(4) + 10;
        livingThing.xCoord = new int[livingThing.numOfPoints * 2];
        livingThing.yCoord = new int[livingThing.numOfPoints * 2];
        livingThing.conEn = 0.45 + (r.nextFloat()/2);
        livingThing.xSpeed = r.nextInt(20) - 10;
        livingThing.ySpeed = r.nextInt(20) - 10;
        livingThing.xAcc = 0;
        livingThing.yAcc = 0;
        livingThing.xPos = r.nextInt(lp.getWidth());
        livingThing.yPos = r.nextInt(lp.getHeight());
        livingThing.angle = r.nextFloat() * 360;
        livingThing.angularAcceleration = r.nextFloat() / 2; 
        livingThing.angularSpeed = r.nextFloat();
        livingThing.maxLifeTime = (int)(500 + r.nextFloat() * 700);
        livingThing.lifeTime = livingThing.maxLifeTime;
        livingThing.color = new Color(r.nextInt(128) + 128, r.nextInt(128) + 128, r.nextInt(128) + 128);
        return livingThing;
    }
}

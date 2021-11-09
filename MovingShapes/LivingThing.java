import java.util.Random;
import java.util.Vector;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Point;

public abstract class LivingThing {
    double conEn;
    double xPos, yPos;
    double xAcc, yAcc;
    double xSpeed, ySpeed;
    int xCoord[], yCoord[];
    double angle;
    double angularSpeed;
    double angularAcceleration;
    double timeScalar;
    double lifeTime;
    double maxLifeTime;
    double alpha;
    int numOfPoints;
    int outerRadius, innerRadius;
    int opacity;
    double perfectOpacity;
    boolean gEnabled;
    boolean chaseEnabled;
    boolean idleEnabled;
    boolean chaseDone;

    Point destination;

    Color color;

    Vector<MortalityListener> mortListeners;
    Vector<LivingThing> things;
    JPanel lp;

    public LivingThing(){
        alpha = 0;
        opacity = 255;
        perfectOpacity = 255;
        xPos = yPos = 0;
        timeScalar = 50;
        mortListeners = new Vector<MortalityListener>();
    }

    public void setDest(Point mousePoint){
        destination = mousePoint;
    }

    public void setGravity(boolean gEnabled){
        this.gEnabled = gEnabled;
        Random r = new Random();
        yAcc = gEnabled ? 1 : 0;
        if(gEnabled){
            yAcc = 1;
        } else {
            yAcc = 0;
            if(ySpeed <= 0.1){
                ySpeed = 1 + r.nextFloat() * 3;
            }
        }
    }

    public void update(){
        updateVitality();
        double deltaScaledMillis = 1 * (timeScalar/100);
        updateLinearVelocity(deltaScaledMillis);
        reflect();
        updateCurrentPosition(deltaScaledMillis);
        updateAngularSpeed(deltaScaledMillis);
        updateAngle(deltaScaledMillis);
        updateOrientation(deltaScaledMillis);
    }

    private void updateVitality(){
        lifeTime--;
        if(lifeTime <= 0){
            MortalityEvent mortEv = new MortalityEvent(this, true);
            for(int i = 0; i < mortListeners.size(); i++){
                mortListeners.get(i).onLifeEvent(mortEv);
            }
            System.out.println("Died");
        }
    }

    public void addMortalityListener(MortalityListener ml){
        mortListeners.add(ml);
    }

    private void updateLinearVelocity(double deltaScaledMillis){
        if(chaseEnabled && destination != null){
            if(!chaseDone){
                if(((yPos <= destination.getY() - outerRadius) || (yPos >= destination.getY() + outerRadius)) && ((xPos <= destination.getX() - outerRadius) || (xPos >= destination.getX() + outerRadius))){
                    chaseDone = true;
                } else {
                    xSpeed = (destination.getX() - xPos) * 0.1;
                    ySpeed = (destination.getY() - yPos) * 0.1;
                }
            }
        } else if(idleEnabled){
            xSpeed = 0;
            ySpeed = 0;
        } else {
            xSpeed = xSpeed + xAcc * deltaScaledMillis; 
            ySpeed = ySpeed + yAcc * deltaScaledMillis;
        }   
    }

    private void updateCurrentPosition(double deltaScaledMillis){
            xPos = xPos + xSpeed * deltaScaledMillis;
            yPos = yPos + ySpeed * deltaScaledMillis; 
    }

    private void updateAngularSpeed(double deltaScaledMillis){
        angularSpeed = angularSpeed + angularAcceleration * deltaScaledMillis;
    }

    private void updateAngle(double deltaScaledMillis){
        alpha = angle + angularSpeed * deltaScaledMillis;
    }

    private void updateOrientation(double deltaScaledMillis){
        double deltaAlpha = Math.PI/numOfPoints;
        for(int i = 0; i < numOfPoints * 2; i++){
            if(i%2 == 0){
                xCoord[i] = (int)(xPos + innerRadius * Math.cos(alpha));
                yCoord[i] = (int)(yPos + innerRadius * Math.sin(alpha));
            } else {
                xCoord[i] = (int)(xPos + outerRadius * Math.cos(alpha));
                yCoord[i] = (int)(yPos + outerRadius * Math.sin(alpha));
            }
            alpha += deltaAlpha;
        }
    }
    
    private void reflect(){
        if(xPos <= outerRadius || xPos >= lp.getSize().getWidth()-outerRadius){
            if(xPos >= (lp.getWidth() - outerRadius)){
                xPos = lp.getWidth() - outerRadius;
            }
            if(xPos <= outerRadius){
                xPos = outerRadius;
            }
            angularSpeed = 0;
            angle = 0;
            xSpeed = -xSpeed;
        }

        if(yPos <= outerRadius || yPos >= lp.getSize().getHeight()-outerRadius){
            if(yPos >= (lp.getHeight() - outerRadius)){
                yPos = lp.getHeight() - outerRadius;
            }
            if(yPos <= outerRadius){
                yPos = outerRadius;
            }
            if(gEnabled){
                ySpeed = (int)(-ySpeed * conEn);
            } else {
                ySpeed = -ySpeed;
            }
        }
    }
    public abstract void draw(Graphics2D g);

    public static LivingThing getDefaultLivingThing(JPanel owner){
        DefaultLivingThing dlt = new DefaultLivingThing(owner);
        return dlt;
    }
}

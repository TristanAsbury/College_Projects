import java.util.Vector;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

public abstract class LivingThing {
    float xPos, yPos;
    float xSpeed, ySpeed;
    float angle;
    float angularSpeed;
    float xAcc, yAcc;
    float angularAcceleration;
    float timeScalar;
    int lifeTime;
    int radius;
    int xCoord[];
    int yCoord[];
    float alpha;
    
    Color color;

    Vector<MortalityListener> mortListeners;
    Vector<LivingThing> things;
    JPanel lp;

    public LivingThing(){
        alpha = angle;
        xPos = yPos = 0;
        xCoord = new int[4];
        yCoord = new int[4];
        timeScalar = 50;
        this.things = things;
        mortListeners = new Vector<MortalityListener>();
    }

    public void setGravity(boolean gEnabled){
        yAcc = gEnabled ? 2 : 0;
    }

    public void update(){
        updateVitality();
        float deltaScaledMillis = 1 * (timeScalar/100);
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
        }
    }

    public void addMortalityListener(MortalityListener ml){
        mortListeners.add(ml);
    }

    private void updateLinearVelocity(float deltaScaledMillis){
        xSpeed = xSpeed + xAcc * deltaScaledMillis; 
        ySpeed = ySpeed + yAcc * deltaScaledMillis;
    }

    private void updateCurrentPosition(float deltaScaledMillis){
        xPos = xPos + xSpeed * deltaScaledMillis;
        yPos = yPos + ySpeed * deltaScaledMillis;
    }

    private void updateAngularSpeed(float deltaScaledMillis){
        angularSpeed = angularSpeed + angularAcceleration * deltaScaledMillis;
    }

    private void updateAngle(float deltaScaledMillis){
        alpha = angle + angularSpeed * deltaScaledMillis;
    }

    private void updateOrientation(float deltaScaledMillis){
        for(int i = 0; i < 4; i++){
            xCoord[i] = (int)(xPos + radius * Math.cos(alpha));
            yCoord[i] = (int)(yPos + radius * Math.sin(alpha));
            alpha += Math.PI / 2.0;
        }
    }
    
    private void reflect(){
        if(xPos <= radius || xPos >= lp.getSize().getWidth()-radius){
            if(xPos >= (lp.getWidth() - radius)){
                xPos = lp.getWidth() - radius;
            }
            if(xPos <= radius){
                xPos = radius;
            }
            angularSpeed = 0;
            angle = 0;
            xSpeed = -xSpeed;
        }

        if(yPos <= radius || yPos >= lp.getSize().getHeight()-radius){
            if(yPos >= (lp.getHeight() - radius)){
                yPos = lp.getHeight() - radius;
            }
            if(yPos <= radius){
                yPos = radius;
            }
            angularSpeed = 0;
            angle = 0;
            ySpeed = -ySpeed;
        }
    }
    public abstract void draw(Graphics2D g);

    public static LivingThing getDefaultLivingThing(JPanel owner){
        DefaultLivingThing dlt = new DefaultLivingThing(owner);
        return dlt;
    }
}

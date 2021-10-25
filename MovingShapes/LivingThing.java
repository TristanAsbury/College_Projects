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
    Color color;

    Vector<MortalityListener> mortListeners;
    JPanel lp;

    public LivingThing(){
        xPos = yPos = 0;
        timeScalar = 50;
        mortListeners = new Vector<MortalityListener>();
    }
    public void update(){
        updateVitality();
        float deltaScaledMillis = 1 * (timeScalar/100);
        updateLinearVelocity(deltaScaledMillis);
        updateCurrentPosition(deltaScaledMillis);
        updateAngularSpeed(deltaScaledMillis);
        updateAngle(deltaScaledMillis);
        reflect();
    }
    private void updateVitality(){
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
        angle = angle + angularSpeed * deltaScaledMillis;
    }
    
    private void reflect(){
        if(xPos <= 0 || xPos >= lp.getSize().getWidth()){
            xSpeed = -xSpeed;
        }

        if(yPos <= 0 || yPos >= lp.getSize().getHeight()){
            ySpeed = -ySpeed;
        }
    }
    public abstract void draw(Graphics2D g);

    public static LivingThing getDefaultLivingThing(JPanel owner){
        DefaultLivingThing dlt = new DefaultLivingThing(owner);
        return dlt;
    }
}

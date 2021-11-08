import java.util.Random;
import java.util.Vector;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

public abstract class LivingThing {
    double conEn;
    float xPos, yPos;
    float xAcc, yAcc;
    float xSpeed, ySpeed;
    int xCoord[], yCoord[];
    float angle;
    float angularSpeed;
    float angularAcceleration;
    float timeScalar;
    double lifeTime;
    double maxLifeTime;
    float alpha;
    int numOfPoints;
    int outerRadius, innerRadius;
    int opacity;
    double perfectOpacity;
    boolean gEnabled;
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
            System.out.println("Died");
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

import java.awt.Toolkit;
import java.awt.Dimension;
import javax.swing.JFrame;
import java.util.Vector;
import javax.swing.Timer;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.JSlider;
import java.awt.BorderLayout;
import javax.swing.JPanel;

public class LivingFrame extends JFrame implements ActionListener, ChangeListener{
    DrawingPanel lp;
    Vector<LivingThing> livingThings;
    Timer updateTimer;
    JSlider speedSlider;
    JPanel optionsPanel;

    public LivingFrame(){
        setupFrame();
        setupPanel();
        setupUI();
        updateTimer = new Timer(10, this);
        updateTimer.setActionCommand("UPDATE");
        updateTimer.start();
    }

    private void setupUI(){
        optionsPanel = new JPanel();
        speedSlider = new JSlider(1, 100);
        speedSlider.addChangeListener(this);
        optionsPanel.add(speedSlider);
        add(optionsPanel, BorderLayout.SOUTH);
    }

    public void setupPanel(){
        livingThings = new Vector<LivingThing>();
        lp = new DrawingPanel(livingThings);
        add(lp, BorderLayout.CENTER);
        for(int i = 0; i < 20; i++){
            livingThings.add(DefaultLivingThing.getRandom(lp));
        }
        lp.setBackground(Color.BLACK);
    }

    private void setupFrame(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize((int)d.getWidth()/2, (int)d.getHeight()/2);
        setLocation((int)d.getWidth()/4, (int)d.getHeight()/4);
        setTitle("Living Things");
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e){
        if(e.getActionCommand().equals("UPDATE")){
            for(LivingThing lt : livingThings){
                lt.update();
                lp.repaint();
            }
        }
    }

    public void stateChanged(ChangeEvent c){
        if(c.getSource() == speedSlider){
            for(LivingThing lt : livingThings){
                lt.timeScalar = speedSlider.getValue();
            }
        }
    }
}

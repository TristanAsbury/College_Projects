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
import javax.swing.*;

public class LivingFrame extends JFrame implements ActionListener, ChangeListener, MortalityListener {
    DrawingPanel lp;
    Vector<LivingThing> livingThings;
    Timer updateTimer;
    JSlider speedSlider;
    JPanel optionsPanel;
    JButton addButton;
    JCheckBox gravityButton;
    boolean gravity;

    public LivingFrame(){
        setupPanel();
        setupUI();
        updateTimer = new Timer(10, this);
        updateTimer.setActionCommand("UPDATE");
        updateTimer.start();
        setupFrame();
        lp.repaint();
    }

    private void setupUI(){
        optionsPanel = new JPanel();
        speedSlider = new JSlider(1, 100);
        addButton = new JButton("Add");
        addButton.addActionListener(this);
        gravityButton = new JCheckBox("Gravity");
        gravityButton.addActionListener(this);
        speedSlider.addChangeListener(this);
        optionsPanel.add(speedSlider);
        optionsPanel.add(addButton);
        optionsPanel.add(gravityButton);
        add(optionsPanel, BorderLayout.SOUTH);
    }

    public void setupPanel(){
        livingThings = new Vector<LivingThing>();
        lp = new DrawingPanel(livingThings);
        add(lp, BorderLayout.CENTER);
        lp.setBackground(Color.WHITE);
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
            for(int i = 0; i < livingThings.size(); i++){
                LivingThing lt = livingThings.elementAt(i);
                lt.update();
                lp.repaint();
            }
        }

        if(e.getSource() == addButton){
            LivingThing lt = DefaultLivingThing.getRandom(lp);
            lt.addMortalityListener(this);
            livingThings.addElement(lt);
        }

        if(e.getSource() == gravityButton){
            for(int i = 0; i < livingThings.size(); i++){
                livingThings.elementAt(i).setGravity(gravityButton.isSelected());
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

    public void onLifeEvent(MortalityEvent e){
        for(int i = 0; i < livingThings.size(); i++){
            if(livingThings.elementAt(i)==e.getSource()){
                livingThings.removeElementAt(i);
            }
        }
    }
}

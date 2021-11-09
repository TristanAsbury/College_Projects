import java.awt.Toolkit;
import java.awt.Dimension;
import javax.swing.JFrame;
import java.util.Vector;
import javax.swing.Timer;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.*;
import java.awt.Color;
import java.awt.event.*;
import javax.swing.JSlider;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.*;

public class LivingFrame extends JFrame implements ActionListener, ChangeListener, MortalityListener {
    DrawingPanel lp;
    JPanel optionsPanel;

    Vector<LivingThing> livingThings;
    Timer updateTimer;

    JLabel speedLabel, amountLabel, lifeTimeLabel, gravityAccLabel, consEnergyLabel, animationIntervalLabel, modeHeader;
    JSlider speedSlider, addAmountSlider, lifeTimeSlider, gravityAccSlider, consEnergySlider, animationIntervalSlider;
    JButton clearAllButton, addSingularButton, addAmountButton;
    JRadioButton idleMode, chaseMode, gravityMode, randomMode;
    ButtonGroup modeGroup;

    JCheckBox tracers;

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
        modeGroup = new ButtonGroup();
        optionsPanel = new JPanel();
        speedSlider = new JSlider(1, 100);
        addAmountSlider = new JSlider(1, 50);
        lifeTimeSlider = new JSlider(300, 500);
        gravityAccSlider = new JSlider(1, 5);
        consEnergySlider = new JSlider(0, 1);
        animationIntervalSlider = new JSlider(1, 1000);

        modeHeader = new JLabel("Modes:");
        speedLabel = new JLabel("Speed");
        amountLabel = new JLabel("Add Amount");
        lifeTimeLabel = new JLabel("Lifetime");
        gravityAccLabel = new JLabel("Gravity Acceleration");
        consEnergyLabel = new JLabel("Energy Cons.");
        animationIntervalLabel = new JLabel("Animation Int.");

        addSingularButton = new JButton("Add Singular Star");
        addSingularButton.addActionListener(this);
        
        addAmountButton = new JButton("Add set amount");
        addAmountButton.addActionListener(this);

        clearAllButton = new JButton("Clear all");
        clearAllButton.addActionListener(this);

        gravityMode = new JRadioButton("Gravity");
        gravityMode.addActionListener(this);
        idleMode = new JRadioButton("Idle");
        idleMode.addActionListener(this);
        chaseMode = new JRadioButton("Chase");
        chaseMode.addActionListener(this);
        randomMode = new JRadioButton("Random");
        randomMode.addActionListener(this);

        modeGroup.add(gravityMode);
        modeGroup.add(idleMode);
        modeGroup.add(randomMode);
        modeGroup.add(chaseMode);

        tracers = new JCheckBox("Tracers");
        tracers.addActionListener(this);

        speedSlider.addChangeListener(this);

        GroupLayout inputLayout = new GroupLayout(optionsPanel);
        optionsPanel.setLayout(inputLayout);
        inputLayout.setAutoCreateGaps(true);
        inputLayout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup hGroup = inputLayout.createSequentialGroup();
        hGroup.addGroup(inputLayout.createParallelGroup().
                    addComponent(addSingularButton)
                    .addComponent(speedLabel)
                    .addComponent(amountLabel)
                    .addComponent(lifeTimeLabel)
                    .addComponent(gravityAccLabel)
                    .addComponent(consEnergyLabel)
                    .addComponent(animationIntervalLabel)
                    .addComponent(modeHeader)
                    .addComponent(idleMode)
                    .addComponent(chaseMode)
                    .addComponent(gravityMode)
                    .addComponent(randomMode)
                    .addComponent(tracers));
        hGroup.addGroup(inputLayout.createParallelGroup().
                    addComponent(addAmountButton)
                    .addComponent(speedSlider)
                    .addComponent(addAmountSlider)
                    .addComponent(lifeTimeSlider)
                    .addComponent(gravityAccSlider)
                    .addComponent(consEnergySlider)
                    .addComponent(animationIntervalSlider));
        hGroup.addGroup(inputLayout.createParallelGroup().
                    addComponent(clearAllButton));
        inputLayout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = inputLayout.createSequentialGroup();
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(addSingularButton)
                    .addComponent(addAmountButton)
                    .addComponent(clearAllButton));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(speedLabel)
                    .addComponent(speedSlider));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(amountLabel)
                    .addComponent(addAmountSlider));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(lifeTimeLabel)
                    .addComponent(lifeTimeSlider));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(gravityAccLabel)
                    .addComponent(gravityAccSlider));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(consEnergyLabel)
                    .addComponent(consEnergySlider));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(animationIntervalLabel)
                    .addComponent(animationIntervalSlider));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(modeHeader));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(idleMode));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(chaseMode));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(gravityMode));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(randomMode));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(tracers));
        inputLayout.setVerticalGroup(vGroup);

        add(optionsPanel, BorderLayout.WEST);
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

        if(e.getSource() == addSingularButton){
            LivingThing lt = DefaultLivingThing.getRandom(lp, gravityMode.isSelected(), lifeTimeSlider.getValue());
            lt.addMortalityListener(this);
            livingThings.addElement(lt);
        }

        if(e.getSource() == addAmountButton){
            for(int i = 0; i < addAmountSlider.getValue(); i++){
                LivingThing lt = DefaultLivingThing.getRandom(lp, gravityMode.isSelected(), lifeTimeSlider.getValue());
                lt.addMortalityListener(this);
                livingThings.addElement(lt);
            }
        }

        if(e.getSource() == clearAllButton){
            for(int i = livingThings.size() - 1; i >= 0; i--){
                livingThings.remove(i);
            }
            lp.repaint();
        }

        if(e.getSource() == gravityMode){
            for(int i = 0; i < livingThings.size(); i++){
                LivingThing thing = livingThings.elementAt(i);
                thing.chaseEnabled = false;
                thing.chaseEnabled = false;
                thing.setGravity(true);
            }
        }

        if(e.getSource() == chaseMode){
            for(int i = 0; i < livingThings.size(); i++){
                livingThings.elementAt(i).setGravity(false);
                livingThings.elementAt(i).chaseEnabled = true;
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

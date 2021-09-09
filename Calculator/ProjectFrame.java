import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

public class ProjectFrame extends JFrame implements ActionListener, DocumentListener {
    JPanel inputPanel; //TOP
    JPanel optionPanel; //CENTER
    JPanel outputPanel; //SOUTH

    ButtonGroup calcOptionGroup;
    
    //Input Panel Area
    JTextField inputArea;
    JButton clearButton;
    //Radio Buttons
    JRadioButton sqrtRadio, sinRadio, cosRadio, lnRadio;
    //Check Box
    JCheckBox inverseCheckBox;
    //Ouput
    JTextField outputText;
    
    ProjectFrame(){
        //Set Up Panels
        inputPanel = new JPanel();
        optionPanel = new JPanel();
        outputPanel = new JPanel();
        calcOptionGroup = new ButtonGroup();

        //Set Up Radio Buttons and Check Box
        inverseCheckBox = new JCheckBox("Inverse");
        inverseCheckBox.addActionListener(this);
        inverseCheckBox.setActionCommand("SELECTION");
        
        sqrtRadio = new JRadioButton("Square Root");
        sqrtRadio.addActionListener(this);
        sqrtRadio.setActionCommand("SELECTION");
        //This will set the sqrt option to the default selected option
        sqrtRadio.setSelected(true);

        sinRadio = new JRadioButton("Sin");
        sinRadio.addActionListener(this);
        sinRadio.setActionCommand("SELECTION");

        cosRadio = new JRadioButton("Cos");
        cosRadio.addActionListener(this);
        cosRadio.setActionCommand("SELECTION");

        lnRadio = new JRadioButton("Ln");
        lnRadio.addActionListener(this);
        lnRadio.setActionCommand("SELECTION");
        //Add radio buttons to RadioPanel and ButtonGroup
        optionPanel.add(inverseCheckBox);
        optionPanel.add(sqrtRadio);
        optionPanel.add(sinRadio);
        optionPanel.add(cosRadio);
        optionPanel.add(lnRadio);
        //Button Group
        calcOptionGroup.add(sqrtRadio);
        calcOptionGroup.add(sinRadio);
        calcOptionGroup.add(cosRadio);
        calcOptionGroup.add(lnRadio);
        
        //Create Text Area and Clear Button
        inputArea = new JTextField(6);
        inputArea.getDocument().addDocumentListener(this);
        clearButton = new JButton("Clear Input");
        clearButton.setActionCommand("CLEAR");
        clearButton.addActionListener(this);
        
        //Add them to input panel
        inputPanel.add(inputArea);
        inputPanel.add(clearButton);

        //Ouput
        outputText = new JTextField(16);
        outputText.setEditable(false);

        //Output Panel
        outputPanel.add(outputText);
        //Add panels
        add(inputPanel, BorderLayout.NORTH);
        add(optionPanel, BorderLayout.CENTER);
        add(outputPanel, BorderLayout.SOUTH);
        calcOptionGroup = new ButtonGroup();
        
        setupFrame();
    }

    public void updateOutput(){
        double enteredNum = 0;
        boolean calcGood = true;
        boolean inverse = inverseCheckBox.isSelected();
        if(!inputArea.getText().trim().equals("")){
            try {
                enteredNum = Double.valueOf(inputArea.getText());
            } catch(NumberFormatException e) {
                outputText.setText("Error.");
                calcGood = false;
            }
            //Go onto calculing
            if(calcGood){   
                if(sqrtRadio.isSelected()){
                    outputText.setText(sqrtCalc(enteredNum, inverse));
                } else if(sinRadio.isSelected()){
                    outputText.setText(sinCalc(enteredNum, inverse));
                } else if(cosRadio.isSelected()){
                    outputText.setText(cosCalc(enteredNum, inverse));
                } else if(lnRadio.isSelected()){
                    outputText.setText(lnCalc(enteredNum, inverse));
                }
            }
        } else {
            outputText.setText("");
        }
    }

    public void setupFrame(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize((int)(d.getWidth()/3),(int)d.getHeight()/3);
        setVisible(true); 
        setTitle("Calculator Project");
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == clearButton){
            inputArea.setText("");
            outputText.setText("");
        } else if(e.getActionCommand().equals("SELECTION")){
            updateOutput();
        }
    }

    private String lnCalc(double x, boolean inv){
        //LOG: Domain = (0,inf)
        //LOG INV: Domain = (-inf, inf)
        if(inv){
            return Double.toString(Math.exp(x));
        }
        if(!(x <= 0)){
            return Double.toString(Math.log(x));
        }
        return "Error.";
    }

    private String sqrtCalc(double x, boolean inv){
        //SQRT: Domain = (0, inf)
        //SQRT INV: (-inf, inf)
        if(inv){
            return Double.toString(Math.pow(x, 2));
        }
        if(!(x<0)){
            return Double.toString(Math.sqrt(x));
        }
        return "Error.";
    }

    private String sinCalc(double x, boolean inv){
        //SIN: Domain = (-inf, inf)
        //SIN INV: Domain = [-1, 1]
        if(inv){
            if(x <= 1 && x >= -1){
                return Double.toString(Math.asin(x));
            }
            return "Error.";
        }
        return Double.toString(Math.sin(x));
    }

    private String cosCalc(double x, boolean inv){
        //COS: Domain = (-inf, inf)
        //COS INV: Domain = [-1, 1]
        if(inv){
            if(x <= 1 && x>= -1){
                return Double.toString(Math.acos(x));
            }
            return "Error.";
        }
        return Double.toString(Math.cos(x));
    }

    public void insertUpdate(DocumentEvent e) {
        if(e.getDocument().equals(inputArea.getDocument())){
            updateOutput();
        }
    }
    public void removeUpdate(DocumentEvent e) {
        if(e.getDocument().equals(inputArea.getDocument())){
            updateOutput();
        }
    }   
    public void changedUpdate(DocumentEvent e) {
        if(e.getDocument().equals(inputArea.getDocument())){
            updateOutput();
        }
    }
}

import java.awt.Dialog;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class WorkOrderDialog extends JDialog implements ActionListener, DocumentListener {
    GroupLayout inputLayout;

    JPanel inputPanel;
    JPanel buttonPanel;
    JPanel errorPanel;

    JButton submitButton;
    JButton cancelButton;

    JLabel nameInputLabel, nameInputError;
    JLabel departmentInputLabel, departmentInputError;
    JLabel dateInitLabel, dateInitError;
    JLabel dateFulLabel, dateFulError;
    JLabel descriptionInputLabel, descriptionInputError;
    JLabel billingRateInputLabel, billingRateError;

    JTextField nameInput;
    JTextField departmentInput;
    JTextField dateInitInput;
    JTextField dateFulInput;
    JTextField descriptionInput;
    JTextField billingRateInput;

    WorkOrder returnOrder;

    public WorkOrderDialog(){
        errorPanel = new JPanel();

        nameInputLabel = new JLabel("Name:");
        departmentInputLabel = new JLabel("Department:");
        dateInitLabel = new JLabel("Date requested:");
        dateFulLabel = new JLabel("Date fulfilled");
        descriptionInputLabel = new JLabel("Description:");
        billingRateInputLabel = new JLabel("Billing rate:");
        
        nameInputError = makeLabel("Invalid name.", Color.RED, false);
        departmentInputError = makeLabel("Invalid department.", Color.RED, false);
        dateInitError = makeLabel("Invalid request date.", Color.RED, false);
        dateFulError = makeLabel("Invalid fulfillment date.", Color.RED, false);
        descriptionInputError = makeLabel("Invalid description.", Color.RED, false);
        billingRateError = makeLabel("Invalid billing rate.", Color.RED, false);
        

        nameInput = new JTextField(15);
        nameInput.setInputVerifier(new OrderVerifier(0, nameInputError));

        departmentInput = new JTextField(15);
        departmentInput.setInputVerifier(new OrderVerifier(1, departmentInputError));

        dateInitInput = new JTextField(15);
        dateInitInput.getDocument().addDocumentListener(this);
        dateInitInput.setInputVerifier(new OrderVerifier(4, dateInitError));
        
        dateFulInput = new JTextField(15);
        dateFulInput.getDocument().addDocumentListener(this);
        dateFulInput.setInputVerifier(new OrderVerifier(5, dateFulError));

        descriptionInput = new JTextField(15);
        descriptionInput.setInputVerifier(new OrderVerifier(2, descriptionInputError));
        descriptionInput.getDocument().addDocumentListener(this);

        billingRateInput = new JTextField(15);
        billingRateInput.setInputVerifier(new OrderVerifier(3, billingRateError));
        billingRateInput.getDocument().addDocumentListener(this);
        
        
        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.setVerifyInputWhenFocusTarget(false);

        inputPanel = new JPanel();
        buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        

        inputLayout = new GroupLayout(inputPanel);

        inputPanel.setLayout(inputLayout);
        inputLayout.setAutoCreateGaps(true);
        inputLayout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup hGroup = inputLayout.createSequentialGroup();
        hGroup.addGroup(inputLayout.createParallelGroup().
                    addComponent(nameInputLabel)
                    .addComponent(departmentInputLabel)
                    .addComponent(dateInitLabel)
                    .addComponent(dateFulLabel)
                    .addComponent(descriptionInputLabel)
                    .addComponent(billingRateInputLabel));
        hGroup.addGroup(inputLayout.createParallelGroup().
                    addComponent(nameInput)
                    .addComponent(departmentInput)
                    .addComponent(dateInitInput)
                    .addComponent(dateFulInput)
                    .addComponent(descriptionInput)
                    .addComponent(billingRateInput));
        hGroup.addGroup(inputLayout.createParallelGroup()
                    .addComponent(nameInputError)
                    .addComponent(departmentInputError)
                    .addComponent(dateInitError)
                    .addComponent(dateFulError)
                    .addComponent(descriptionInputError)
                    .addComponent(billingRateError));
        inputLayout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = inputLayout.createSequentialGroup();
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(nameInputLabel)
                    .addComponent(nameInput)
                    .addComponent(nameInputError));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(departmentInputLabel)
                    .addComponent(departmentInput)
                    .addComponent(departmentInputError));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(dateInitLabel)
                    .addComponent(dateInitInput)
                    .addComponent(dateInitError));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(dateFulLabel)
                    .addComponent(dateFulInput)
                    .addComponent(dateFulError));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(descriptionInputLabel)
                    .addComponent(descriptionInput)
                    .addComponent(descriptionInputError));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(billingRateInputLabel)
                    .addComponent(billingRateInput)
                    .addComponent(billingRateError));
        inputLayout.setVerticalGroup(vGroup);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        setUp();
    }

    private JLabel makeLabel(String text, Color color, boolean visible){
        JLabel rLabel;
        rLabel = new JLabel(text);
        rLabel.setForeground(color);
        rLabel.setVisible(visible);
        return rLabel;
    }
    
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == cancelButton){
            returnOrder = null;
            dispose();
        } else if(e.getSource() == submitButton){
            boolean isGood = validateInput();
            if(isGood){
                dispose();
            }
        }
    }

    public void insertUpdate(DocumentEvent d){
        
    }

    public void removeUpdate(DocumentEvent d){
        
    }

    public void changedUpdate(DocumentEvent d){

    }

    public WorkOrder getWorkOrder(){
        return returnOrder;
    }

    private boolean validateInput(){
        boolean isGood = true;
        //Check all fields
        if(nameInput.getText().trim().equals("")){
            nameInputError.setVisible(true);
            isGood = false;
        }

        if(departmentInput.getText().trim().equals("")){
            nameInputError.setVisible(true);
            isGood = false;
        }

        return isGood;
    }

    private void setUp(){
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize((int)d.getWidth()/4, (int)d.getHeight()/4);
        setLocation((int)d.getWidth()/4, (int)d.getHeight()/4);
        setTitle("Part One");
        setVisible(true);
    }
    
}

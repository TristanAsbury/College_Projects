import java.awt.Dialog;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.Date;

public class WorkOrderDialog extends JDialog implements ActionListener {
    GroupLayout inputLayout;

    JPanel inputPanel;
    JPanel buttonPanel;

    JButton submitButton;
    JButton cancelButton;

    JLabel nameInputLabel, nameInputError;
    JLabel departmentInputLabel;
    JLabel dateReqLabel, dateReqError;
    JLabel dateFulLabel, dateFulError;
    JLabel descriptionInputLabel, descriptionInputError;
    JLabel billingRateInputLabel, billingRateError;

    JTextField nameInput;
    JComboBox<String> departmentInput;
    JTextField dateReqInput;
    JTextField dateFulInput;
    JTextField descriptionInput;
    JTextField billingRateInput;

    WorkOrder editedOrder;
    int oldIndex;
    DataManager dataManager;

    boolean isEditing;

    public WorkOrderDialog(DataManager dataManager){ //For adding
        this.dataManager = dataManager;
        isEditing = false;
        createGUI();
        setUp();
    }

    public WorkOrderDialog(DataManager dataManager, WorkOrder editedOrder, int oldIndex){ //For editing
        this.oldIndex = oldIndex;
        this.dataManager = dataManager;
        this.editedOrder = editedOrder;
        isEditing = true;
        createGUI();
        nameInput.setText(editedOrder.name);
        departmentInput.setSelectedItem(editedOrder.department);
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        dateReqInput.setText(df.format(editedOrder.requested));
        dateFulInput.setText(df.format(editedOrder.fulfilled));
        descriptionInput.setText(editedOrder.description);
        billingRateInput.setText(Float.toString(editedOrder.billingRate));
        setUp();
    }
    
    private void createGUI(){
        nameInputLabel = new JLabel("Name:");
        departmentInputLabel = new JLabel("Department:");
        dateReqLabel = new JLabel("Date requested:");
        dateFulLabel = new JLabel("Date fulfilled");
        descriptionInputLabel = new JLabel("Description:");
        billingRateInputLabel = new JLabel("Billing rate:");
        
        nameInputError = makeLabel("Invalid name.", Color.RED, false);
        dateReqError = makeLabel("Invalid request date.", Color.RED, false);
        dateFulError = makeLabel("Invalid fulfillment date.", Color.RED, false);
        descriptionInputError = makeLabel("Invalid description.", Color.RED, false);
        billingRateError = makeLabel("Invalid billing rate.", Color.RED, false);

        nameInput = new JTextField(15);
        nameInput.setInputVerifier(new OrderVerifier(0, nameInputError));
        
        String[] depts = {"SALES", "HARDWARE", "ELECTRONICS"};
        departmentInput = new JComboBox<String>(depts);

        dateReqInput = new JTextField(15);
        dateReqInput.setInputVerifier(new OrderVerifier(4, dateReqError));
        
        dateFulInput = new JTextField(15);
        dateFulInput.setInputVerifier(new OrderVerifier(5, dateFulError));

        descriptionInput = new JTextField(15);
        descriptionInput.setInputVerifier(new OrderVerifier(2, descriptionInputError));

        billingRateInput = new JTextField(15);
        billingRateInput.setInputVerifier(new OrderVerifier(3, billingRateError));
        
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
                    .addComponent(dateReqLabel)
                    .addComponent(dateFulLabel)
                    .addComponent(descriptionInputLabel)
                    .addComponent(billingRateInputLabel));
        hGroup.addGroup(inputLayout.createParallelGroup().
                    addComponent(nameInput)
                    .addComponent(departmentInput)
                    .addComponent(dateReqInput)
                    .addComponent(dateFulInput)
                    .addComponent(descriptionInput)
                    .addComponent(billingRateInput));
        hGroup.addGroup(inputLayout.createParallelGroup()
                    .addComponent(nameInputError)
                    .addComponent(dateReqError)
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
                    .addComponent(departmentInput));
        vGroup.addGroup(inputLayout.createParallelGroup(Alignment.BASELINE).
                    addComponent(dateReqLabel)
                    .addComponent(dateReqInput)
                    .addComponent(dateReqError));
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
            dispose();
        } else if(e.getSource() == submitButton){
            boolean isGood = validateInput();
            if(isGood){
                if(isEditing){ //if they are editing a work order
                    WorkOrder returnOrder = makeReturnOrder();
                    dataManager.ReplaceItem(returnOrder, oldIndex);
                } else {        //if they are adding a work order
                    WorkOrder returnOrder = makeReturnOrder();
                    dataManager.AddItem(returnOrder);
                }
                dispose();
            }
        }
    }

    private boolean validateInput(){
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        boolean isGood = true;

        //Check all fields
        //Check name
        if(nameInput.getText().trim().equals("")){  
            isGood = false;
            nameInputError.setVisible(true);
            nameInput.requestFocus();
        }
        
        //Check dates
        try{                                        
            long dateReq = df.parse(dateReqInput.getText()).getTime();
            long dateFul = df.parse(dateFulInput.getText()).getTime();
            if(dateFul < dateReq){
                isGood = false;
                dateFulError.setVisible(true);
                dateReqError.setVisible(true);
                dateReqInput.requestFocus();
            }
        } catch(ParseException e){
            isGood = false;
            dateFulError.setVisible(true);
            dateReqError.setVisible(true);
            dateReqInput.requestFocus();
        }

        //Check billing rate
        try {   
            float billingRate;
            billingRate = Float.parseFloat(billingRateInput.getText());
            if(!(billingRate > 5.6 && billingRate <= 12.8)){
                isGood = false;
                billingRateError.setVisible(true);
                billingRateInput.requestFocus();
            }
        } catch (NumberFormatException e){
            isGood = false;
            billingRateError.setVisible(true);
            billingRateInput.requestFocus();
        }
        return isGood;
    }

    private WorkOrder makeReturnOrder(){
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        
        String retName = nameInput.getText();
        String retDept = departmentInput.getSelectedItem().toString();
        Date retReq = new Date();
        Date retFul = new Date();
        try {
            retReq = df.parse(dateReqInput.getText());
            retFul = df.parse(dateFulInput.getText());
        } catch (ParseException p){
            System.out.println("Problem parsing dates.");
        }
        String retDesc = descriptionInput.getText();
        float retBR = Float.parseFloat(billingRateInput.getText());
        return new WorkOrder(retName, retDept, retReq, retFul, retDesc, retBR);
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
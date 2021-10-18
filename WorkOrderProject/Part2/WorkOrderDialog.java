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

    JButton sasButton;
    JButton saeButton;
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
    
    DataManager dataManager;
    int oldIndex; //Old index refers to the index of the item we are editing, this will be passed later to the edit method
    boolean isEditing; //True if the user pressed the edit button

    public WorkOrderDialog(DataManager dataManager){ //For adding
        this.dataManager = dataManager;
        isEditing = false;
        createGUI();
        setUp();
    }

    public WorkOrderDialog(DataManager dataManager, WorkOrder editedOrder, int oldIndex){ //This constructor is called when the user presses the edit button
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        isEditing = true;
        this.oldIndex = oldIndex;
        this.dataManager = dataManager;
        this.editedOrder = editedOrder;
        createGUI();
        //This inserts all information from the edited item into the editing Dialog
        nameInput.setText(editedOrder.name);                                    
        departmentInput.setSelectedItem(editedOrder.department);
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
        
        String[] depts = {"SALES", "HARDWARE", "ELECTRONICS"};
        departmentInput = new JComboBox<String>(depts);

        dateReqInput = new JTextField(15);
        dateReqInput.setInputVerifier(new OrderDateVerifier(dateReqError));
        
        dateFulInput = new JTextField(15);
        dateFulInput.setInputVerifier(new OrderDateVerifier(dateFulError));

        descriptionInput = new JTextField(15);

        billingRateInput = new JTextField(15);
        billingRateInput.setInputVerifier(new OrderRateVerifier(billingRateError));
        
        
        sasButton = new JButton("Submit And Continue");
        sasButton.addActionListener(this);

        saeButton = new JButton("Submit and Exit");
        saeButton.addActionListener(this);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);
        cancelButton.setVerifyInputWhenFocusTarget(false);

        inputPanel = new JPanel();
        buttonPanel = new JPanel();
        if(!isEditing){
            buttonPanel.add(sasButton);
        }
        buttonPanel.add(saeButton);
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
        }
        if(e.getSource() == saeButton){
            boolean isGood = validateInput();
            if(isGood){
                if(isEditing){                      //if they are editing a work order
                    WorkOrder returnOrder = makeReturnOrder();
                    dataManager.ReplaceItem(returnOrder, oldIndex);
                } else {                            //if they are adding a work order
                    WorkOrder returnOrder = makeReturnOrder();
                    dataManager.AddItem(returnOrder);
                }
                dispose();
            }
        }
        if(e.getSource() == sasButton){
            boolean isGood = validateInput();
            if(isGood){
                if(isEditing){                      //if they are editing a work order
                    WorkOrder returnOrder = makeReturnOrder();
                    dataManager.ReplaceItem(returnOrder, oldIndex);
                } else {                            //if they are adding a work order
                    WorkOrder returnOrder = makeReturnOrder();
                    dataManager.AddItem(returnOrder);
                }
                if(isGood){
                    nameInput.setText("");
                    departmentInput.setSelectedItem("SALES");
                    dateReqInput.setText("");
                    dateFulInput.setText("");
                    billingRateInput.setText("");
                    descriptionInput.setText("");
                }
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

        //Check dates
        try{
            long dateReq;
            long dateFul;
            //If the date fulfil IS NOT empty
            if(!dateFulInput.getText().trim().equals("")){
                dateReq = df.parse(dateReqInput.getText()).getTime();
                dateFul = df.parse(dateFulInput.getText()).getTime();
                if(dateFul < dateReq){
                    isGood = false;
                    dateFulInput.requestFocus();
                    dateReqError.setVisible(true);
                    dateFulError.setVisible(true);
                }
            } else { //If the date fulfil is empty
                dateReq = df.parse(dateReqInput.getText()).getTime();
                isGood = true;
            }
            
        } catch(ParseException e){
            System.out.println("Error parsing");
            isGood = false;
            dateFulInput.requestFocus();
            dateReqError.setVisible(true);
            dateFulError.setVisible(true);
        }
        return isGood;
    }

    private WorkOrder makeReturnOrder(){
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

        String retName = nameInput.getText();
        String retDept = departmentInput.getSelectedItem().toString();
        String retDesc = descriptionInput.getText();
        float retBR = Float.parseFloat(billingRateInput.getText());

        Date retReq = new Date();
        Date retFul = new Date();

        try {
            if(dateFulInput.getText().trim().equals("")){
                retFul = new Date(0);
            } else {
                retFul = df.parse(dateFulInput.getText());
            }
            retReq = df.parse(dateReqInput.getText());
        } catch (ParseException p){
            System.out.println("Problem parsing dates.");
        }
        return new WorkOrder(retName, retDept, retReq, retFul, retDesc, retBR);
    }

    private void setUp(){
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        setSize((int)d.getWidth()/3, (int)d.getHeight()/3);
        setLocation((int)d.getWidth()/3, (int)d.getHeight()/3);
        //Set title to corresponding action
        if(isEditing){
            setTitle("Edit Work Order");
        } else {
            setTitle("Add Work Order");
        }
        setVisible(true);
    }
}
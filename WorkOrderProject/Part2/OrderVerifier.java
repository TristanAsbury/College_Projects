import javax.swing.InputVerifier;
import javax.lang.model.util.SimpleTypeVisitor14;
import javax.swing.*;
import java.text.*;
import java.util.Date;

public class OrderVerifier extends InputVerifier {
    int type;
    boolean isValid;
    JLabel errorLabel;

    OrderVerifier(int type, JLabel errorLabel){
        
        this.type = type;    
        this.errorLabel = errorLabel;
    }

    public boolean verify(JComponent component){
        String input = ((JTextField)component).getText().trim();
        //If the field being verified is the:
        if(type == 0){          //Name
            isValid = true;
        } else if(type == 1){   //Department 
            isValid  = (input.equals("SALES") || input.equals("HARDWARE") || input.equals("ELECTRONICS") || input.equals("")); //If it is equal to a valid selection
            errorLabel.setText("Invalid department.");
        } else if(type == 2){   //Description
            isValid = true;
        } else if(type == 3){   //Billing rate
            try {
                if(input.equals("")){
                    isValid = true;
                } else {
                    float rateNum = Float.parseFloat(input);
                    if(!(rateNum > 5.6 && rateNum <= 12.8)){
                        isValid = false;
                        errorLabel.setText("Rate out of range.");
                    } else {
                        isValid = true;
                    }
                }         
            } catch(NumberFormatException e){
                isValid = false;
                errorLabel.setText("Not a number.");
            }
        } else if(type == 4){
            
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
            df.setLenient(false);
            ParsePosition pos = new ParsePosition(0);
            Date d = df.parse(input, pos);
            isValid = (pos.getIndex() == input.length() && d != null) || input.equals("");

        } else if(type == 5){
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yy");
            df.setLenient(false);
            ParsePosition pos = new ParsePosition(0);
            Date d = df.parse(input, pos);
            isValid = (pos.getIndex() == input.length() && d != null) || input.equals("");
        }
        if(!isValid){
            errorLabel.setVisible(true);
        } else {
            errorLabel.setVisible(false);
        }
        return isValid;
    }
}

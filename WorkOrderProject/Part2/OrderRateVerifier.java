import javax.swing.InputVerifier;
import javax.swing.*;

//This class is for verifying the dates of the request order
public class OrderRateVerifier extends InputVerifier {

    JLabel errorLabel;

    OrderRateVerifier(JLabel errorLabel){  
        this.errorLabel = errorLabel;
    }

    public boolean verify(JComponent component){
        String input = ((JTextField)component).getText().trim();
        boolean isValid = true;
        //If it's blank, it is fine
        if(input.equals("")){
            return true;
        } else {
            try {
                float rateNum = Float.parseFloat(input);
                if(!(rateNum > 5.6 && rateNum <= 25.0)){
                    isValid = false;
                }
            } catch (NumberFormatException e){
                isValid = false;
            }
        }
        errorLabel.setVisible(!isValid);

        return isValid;
    }
}

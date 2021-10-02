import javax.swing.*;
import java.text.*;
import java.util.Date;

//This class is for verifying the dates of the request order
public class OrderDateVerifier extends InputVerifier {
    int type;
    JLabel errorLabel;

    OrderDateVerifier(JLabel errorLabel){  
        this.errorLabel = errorLabel;
    }
    
    public boolean verify(JComponent component){
        String input = ((JTextField)component).getText().trim();
        boolean isValid = true;
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        df.setLenient(false);
        ParsePosition pos = new ParsePosition(0);
        Date d = df.parse(input, pos);
        if(input.equals("")){
            return true;
        }
        if(!(pos.getIndex() == input.length() && d != null)){
            isValid = false;
            errorLabel.setVisible(true);
        }
        
        return isValid;
    }
}

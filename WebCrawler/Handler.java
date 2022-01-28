import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

public class Handler extends HTMLEditorKit.ParserCallback {
    DataOutputStream dos;
    FileOutputStream fos;
    Pattern pattern = Pattern.compile("[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})");

    @Override
    public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos){
        
    }

    @Override
    public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos){
        if(t == HTML.Tag.A){
            Object att = a.getAttribute(HTML.Attribute.HREF);
            if(att != null){    //If there is an attribute
                String attributeText = a.getAttribute(HTML.Attribute.HREF).toString();  //Turn the attribute into a string
                Matcher matcher = pattern.matcher(attributeText);
                if(!attributeText.toLowerCase().startsWith("mailto:")){ //If the attribute doesn't have mailto, then its just a link
                    System.out.println("Found a link: " + attributeText);
                } else {                                                //Else, it is another email
                    boolean done = false;
                    while(!done){
                        if(matcher.find()){
                            System.out.println("Found an email: " + attributeText.substring(matcher.start(), matcher.end()));
                            matcher.region(matcher.end(), attributeText.length());
                        } else {
                            done = true;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void handleText(char[] data, int pos){
        String text = String.valueOf(data);         //This is the string that the matcher is looking in
        Matcher matcher = pattern.matcher(text);    //Matcher gets set a pattern
        boolean done = false;
        while(!done){
            if(matcher.find()){
                System.out.println("Found an email: " + text.substring(matcher.start(), matcher.end()));
                matcher.region(matcher.end(), text.length());
            } else {
                done = true;
            }
        }
    }
}



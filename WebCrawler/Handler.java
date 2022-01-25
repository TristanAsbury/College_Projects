import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Attribute;

public class Handler extends HTMLEditorKit.ParserCallback {
    DataOutputStream dos;
    FileOutputStream fos;
    List<URL> urlList;

    public Handler(){
    }

    @Override
    public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos){
        
    }

    @Override
    public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos){
        if(t == HTML.Tag.A){
            String attributeText = a.getAttribute(HTML.Attribute.HREF).toString();
            //If it is an email
            if(attributeText.contains("mailto")){
                System.out.println("Found email: " + attributeText.substring(7));
            } else {
                System.out.println("Found a link: " + a.getAttribute(HTML.Attribute.HREF).toString());
            }
        }
    }

    @Override
    public void handleText(char[] data, int pos){

    }
}



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.ldap.StartTlsRequest;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;

public class Handler extends HTMLEditorKit.ParserCallback {
    Pattern pattern = Pattern.compile("[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})");
    SiteNodeModel sites;
    SiteNode origin;
    long startExpansionTime = System.currentTimeMillis();

    public Handler(SiteNodeModel sites, SiteNode origin){
        this.origin = origin;
        this.sites = sites;
    }

    @Override
    public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos){
    }

    @Override
    public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos){
        if(!(System.currentTimeMillis() - startExpansionTime >= Params.MAX_EXPANSION_TIME)){
            if(t == HTML.Tag.A){
                Object att = a.getAttribute(HTML.Attribute.HREF);
                if(att != null){                                                                        //If there is an attribute
                    String attributeText = a.getAttribute(HTML.Attribute.HREF).toString();              //Turn the attribute into a string
                    Matcher matcher = pattern.matcher(attributeText);
                    if(!attributeText.toLowerCase().startsWith("mailto:") && origin.distance < Params.MAX_RADIUS){                 //If the attribute doesn't have mailto, then its just a link
                        String finishedURL = "";
                        if(attributeText.startsWith("http")){                                           //If the url found is NORMAL!!!!
                                finishedURL = attributeText;
                        } else if(attributeText.startsWith("/")){                                       //If relative path starting with '/'
                            if(origin.url.toString().charAt(origin.url.toString().length()-1) == '/'){  //If the origin ends in a '/'
                                finishedURL = origin.url.toString().substring(0, origin.url.toString().length()-1) + attributeText;
                            } else {
                                finishedURL = origin.url.toString() + attributeText;
                            }
                        } else {                                                                        //If the relative path doesn't start with '/' or 'http'                                  
                            if(origin.url.toString().charAt(origin.url.toString().length()-1) == '/'){  //If the origin ends in a '/'
                                finishedURL = origin.url.toString() + attributeText;
                            } else {                                                                    //Else, just add a '/'
                                finishedURL = origin.url.toString() + "/" + attributeText;
                            }
                        }
                        sites.addSite(finishedURL, origin);                                             
                    } else {                                                                            //Else, it is another email
                        boolean done = false;
                        while(!done){
                            if(matcher.find()){
                                String email = attributeText.substring(matcher.start(), matcher.end()); //Get the email found
                                origin.addEmail(email);                                                 //Add the email to the sitenode that was found
                                matcher.region(matcher.end(), attributeText.length());                  
                            } else {
                                done = true;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void handleText(char[] data, int pos){
        if(!(System.currentTimeMillis() - startExpansionTime >= Params.MAX_EXPANSION_TIME)){
            String text = String.valueOf(data);         //This is the string that the matcher is looking in
            Matcher matcher = pattern.matcher(text);    //Matcher gets set a pattern
            boolean done = false;
            while(!done){
                if(matcher.find()){
                    String email = text.substring(matcher.start(), matcher.end());
                    origin.addEmail(email);
                    matcher.region(matcher.end(), text.length());
                } else {
                    done = true;
                }
            }
        }

    }
}


import javax.swing.text.html.parser.ParserDelegator;
import java.io.*;
import java.net.*;

public class Scraper {
    ParserDelegator pd;
    InputStreamReader is;
    Handler myHandler;
    
    SiteNodeModel sites;   //Keeps track of the site nodes
    URL inputURL;
    int maxRadius;

    //InputURL is the URL that will be scraping
    public Scraper(URL inputURL, int maxRadius){
        this.maxRadius = maxRadius;
        this.inputURL = inputURL;
        this.sites = new SiteNodeModel(2);
        sites.addElement(new SiteNode(0, inputURL));
        doScraping();
    }

    public void doScraping(){
        while(!sites.allVisited()){     //While all the sites havent been visited
            myHandler = new Handler(sites, sites.getNextNode());
            
            //Parses all the information on the page
            try{
                is = new InputStreamReader(inputURL.openStream()); 
                pd = new ParserDelegator();
                pd.parse(is, myHandler, true);
                System.out.println("Checked: " + sites.getNextNode().url + " and found " + sites.getNextNode().emails.size() + " emails.");
                sites.getNextNode().visited = true;
            } catch (IOException ioe){
                System.out.println("Something went terribly wrong!");
            }

        }
    }
}

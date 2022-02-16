import javax.swing.text.html.parser.ParserDelegator;
import java.io.*;
import java.net.*;

public class Scraper {
    ParserDelegator pd;
    InputStreamReader is;
    Handler myHandler;
    SiteNodeModel sites;   //Keeps track of the site nodes
    URL inputURL;
    long startTime;
    long currentRuntime;

    //InputURL is the URL that will be scraping
    public Scraper(URL inputURL, int maxRadius){
        this.inputURL = inputURL;
        this.sites = new SiteNodeModel();
        sites.addElement(new SiteNode(0, inputURL));
        startTime = System.currentTimeMillis();
        currentRuntime = 0;
        doScraping();
    }

    public void doScraping(){
        while(!sites.allVisited() && currentRuntime < Attributes.MAX_RUNTIME){      //While all the sites havent been visited and the runtime isnt over max runTime

            currentRuntime = System.currentTimeMillis() - startTime;                   //Update current runtime in millis
            SiteNode currentNode = sites.getNextNode();
            myHandler = new Handler(sites, currentNode);
            
            //Parses all the information on the page
            try{
                is = new InputStreamReader(currentNode.url.openStream()); 
                pd = new ParserDelegator();
                pd.parse(is, myHandler, true);                                      //Parse the site
                currentNode.visited = true;
            } catch (IOException ioe){
                currentNode.visited = true;
                sites.removeElement(currentNode);
            }
        }
    }
}

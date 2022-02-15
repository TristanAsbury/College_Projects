import javax.swing.text.html.parser.ParserDelegator;
import java.io.*;
import java.net.*;
import java.util.Date;

public class Scraper {
    ParserDelegator pd;
    InputStreamReader is;
    Handler myHandler;
    SiteNodeModel sites;   //Keeps track of the site nodes
    URL inputURL;
    long startTime;
    long currentRuntime;

    //InputURL is the URL that will be scraping
    public Scraper(URL inputURL){
        this.inputURL = inputURL;
        this.sites = new SiteNodeModel();
        sites.addElement(new SiteNode(0, inputURL));
        startTime = new Date().getTime();
        currentRuntime = 0;
        doScraping();
    }

    public void doScraping(){
        while(!sites.allVisited() && currentRuntime < Params.MAX_RUNTIME){      //While all the sites havent been visited and the runtime isnt over max runTime
            currentRuntime = new Date().getTime() - startTime;                      //Update current runtime in millis
            SiteNode currentSite = sites.getNextNode();
            myHandler = new Handler(sites, currentSite);
            
            //Parses all the information on the page
            try{
                is = new InputStreamReader(currentSite.url.openStream()); 
                pd = new ParserDelegator();
                pd.parse(is, myHandler, true);                                  //Parse the site
                System.out.println("Checked: " + sites.getNextNode().url + " and found " + sites.getNextNode().emails.size() + " emails.");
                currentSite.visited = true;
            } catch (IOException ioe){
                System.out.println("Something went terribly wrong!");
                currentSite.visited = true;
                sites.removeElement(currentSite);
            }
        }
    }
}
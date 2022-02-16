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
    public Scraper(URL inputURL){
        this.inputURL = inputURL;
        this.sites = new SiteNodeModel();
        sites.addElement(new SiteNode(0, inputURL));
        startTime = System.currentTimeMillis();
        currentRuntime = 0;
        doScraping();
    }

    public void doScraping(){
        while(!sites.allVisited() && currentRuntime < Params.MAX_RUNTIME){      //While all the sites havent been visited and the runtime isnt over max runTime

            currentRuntime = System.currentTimeMillis() - startTime;            //Update current runtime in millis
            SiteNode currentNode = sites.getNextSite();                         //Get the next available node
            myHandler = new Handler(sites, currentNode);                        //Create a new handler, passing the site list and the origin node
            
            //Parses all the information on the page
            try{
                is = new InputStreamReader(currentNode.url.openStream());       //Create the input stream
                pd = new ParserDelegator();                                     //Create a new delegator
                pd.parse(is, myHandler, true);                                  //Parse the site
            } catch (IOException ioe){
                sites.removeElement(currentNode);                               //Don't display the site on the list if there was an error
            }
            currentNode.visited = true;                                         //Set the current node as visited, even if it had an IOException
        }
    }
}
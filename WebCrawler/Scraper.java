
import javax.swing.DefaultListModel;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.*;
import java.net.*;

public class Scraper {
    ParserDelegator pd;
    InputStreamReader is;
    Handler myHandler;
    DefaultListModel<SiteNode> sites;

    //InputURL is the URL that will be scraping
    public Scraper(URL inputURL, DefaultListModel<SiteNode> sites, int distance){
        this.sites = sites;
        myHandler = new Handler(sites, distance);

        try{
            is = new InputStreamReader(inputURL.openStream());
            pd = new ParserDelegator();
            pd.parse(is, myHandler, true);
        } catch (IOException ioe){
            System.out.println("Something went terribly wrong!");
        }
    }
}

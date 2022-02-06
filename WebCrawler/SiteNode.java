import java.net.URL;
import java.util.Vector;

public class SiteNode {
    int distance;
    boolean visited;
    Vector<String> emails;
    URL url;
    int numLinks;

    public SiteNode(int distance, URL url){
        this.numLinks = 0;
        this.visited = false;
        this.distance = distance;
        this.url = url;
        this.emails = new Vector<String>();
    }
}

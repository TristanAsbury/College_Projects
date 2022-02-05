import java.net.URL;
import java.util.Vector;



public class SiteNode {
    int distance;
    Vector<String> emails;
    URL url;

    public SiteNode(int distance, URL url){
        this.distance = distance;
        this.url = url;
    }
}

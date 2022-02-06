import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.DefaultListModel;

public class SiteNodeModel extends DefaultListModel<SiteNode> {
    int maxRadius;

    SiteNodeModel(int maxRadius){
        this.maxRadius = maxRadius;
    }

    public boolean allVisited(){
        //Search through all, if a node which hasnt been visited is found, return false
        for(int i = 0; i < this.size(); i++){
            if(this.get(i).visited == false){
                return false;
            }
        }
        return true;
    }

    public void addSite(String url, SiteNode origin){
        
        try{
            System.out.println("Attemping to add: " + url);
            URL addedURL = new URL(url);
            if(!isDuplicate(addedURL)){ //If the new URL is not a duplicate, then don't add it to the list, otherwise do
                this.addElement(new SiteNode(origin.distance+1, addedURL));
                System.out.println("Added url!");
            }
            origin.numLinks++;
        } catch (MalformedURLException mue ){
        }
    }

    public boolean isDuplicate(URL url){
        for(int i = 0; i < this.size(); i++){
            if(this.get(i).url.toString().equals(url.toString())){    //If the link is found in the list
                System.out.println("FOUND SITE IN LIST!");
                return true;
            }
        }
        return false;
    }
    
    public SiteNode getNextNode(){
        if(!allVisited()){
            for(int i = 0; i < this.size(); i++){
                if(this.get(i).visited == false){
                    return this.get(i);
                }
            }
        }
        return null;
    }
}

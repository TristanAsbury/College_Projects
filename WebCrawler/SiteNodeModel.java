import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.DefaultListModel;

public class SiteNodeModel extends DefaultListModel<SiteNode> {

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
            URL addedURL = new URL(url.toLowerCase());
            if(!isDuplicate(addedURL)){ //If the new URL is not a duplicate, then don't add it to the list, otherwise do
                if(this.size() > 0){
                    boolean foundSpot = false;
                    int index = 0;
                    while(!foundSpot && index < this.size()){                                                                           
                        if(url.toLowerCase().compareTo(this.get(index).url.toString()) >= 0){       //is the newly added greater than/equal to the compared? 
                            index++;                                                                //Go to the next position to compare
                        } else {
                            this.add(index, new SiteNode(origin.distance+1, addedURL));             //If not, then add where it is
                            foundSpot = true;                                                       //We found a spot
                        }
                    }
                    if(foundSpot == false){                                                         //If we havent found a spot
                        this.addElement(new SiteNode(origin.distance+1, addedURL));                 //Add the site to the end
                    }
                    //Go through all of the sites
                } else {
                    this.addElement(new SiteNode(origin.distance+1, addedURL));    
                }
                System.out.println("Added url!");
            }
            
        } catch (MalformedURLException mue){
            System.out.println("AAHHH");
        }
    }

    public boolean isDuplicate(URL url){
        for(int i = 0; i < this.size(); i++){
            if(this.get(i).url.toString().equals(url.toString())){    //If the link is found in the list
                return true;
            }
        }
        return false;
    }
    
    public SiteNode getNextSite(){
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

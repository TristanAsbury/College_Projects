import java.net.URL;
import java.util.Vector;

public class SiteNode {
    int distance;
    boolean visited;
    Vector<String> emails;
    URL url;

    public SiteNode(int distance, URL url){
        this.visited = false;
        this.distance = distance;
        this.url = url;
        this.emails = new Vector<String>();
    }

    public void addEmail(String email){
        boolean isDuplicate = false;
        for(int i = 0; i < emails.size(); i++){
            if(emails.get(i).equals(email)){
                isDuplicate = true;
            }
        }

        if(!isDuplicate){
            if(emails.size() > 0){
                String domain = email.substring(email.indexOf('@'), email.length());
                String userName = email.substring(0, email.indexOf('@'));
                String compareEmail = null;
                System.out.println("Username: " + userName);
                boolean foundSpot = false;
                int index = 0;
                while(!foundSpot && index < emails.size()){                                     //is the newly added greater than/equal to the compared?
                    compareEmail = emails.get(index);  
                    String compareDomain = compareEmail.substring(compareEmail.indexOf('@'), compareEmail.length());
                    if(domain.compareTo(compareDomain) == 0){                                               //If the domains are the same, do another sort    
                        while(!foundSpot && index < emails.size()){                                         //While we haven't found a spot and there are still emails left to compare
                            compareEmail = emails.get(index);                                               //The current email we are looking at
                            String compareUsername = compareEmail.substring(0, compareEmail.indexOf('@'));  //The current username we are looking at
                            compareDomain = compareEmail.substring(compareEmail.indexOf('@'), compareEmail.length());
                            System.out.println("Comparing " + email + " to " + compareEmail);
                            if(userName.compareTo(compareUsername) >= 0 && domain.compareTo(compareDomain) == 0){                                   //If the current email is greater than that we are comparing to
                                index++;                                                                    //Move to next position
                            } else {
                                emails.add(index, email);                                                   //Else, add it to current index
                                foundSpot = true;                                                           //We found a spot
                            }
                        }
                    } else if(domain.compareTo(compareDomain) >= 0) {                                       //If the domain is greater
                        index++;                                                                            //Go to next index
                    } else {                                                                                //Else
                        emails.add(index, email);                                                           //Add the email to the current index
                        foundSpot = true;                                                           
                    }
                }
                if(foundSpot == false){
                    emails.add(email);
                }
                //Go through all of the sites
            } else {
                emails.add(email);
            }
        }
    }
}

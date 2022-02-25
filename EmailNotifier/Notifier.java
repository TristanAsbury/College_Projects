import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Notifier implements ActionListener {
    SystemTray tray;
    PopupMenu trayPopup;
    Timer timer;

    Session session;
    Authenticator auth;
    Store store;
    Folder inboxFolder;
    Message[] messages;

    int intervalMinutes;
    Properties props;

    public Notifier(){
        System.out.println("Creating tray!");
        if(SystemTray.isSupported()){               //If there is a system tray that is supported.
            tray = SystemTray.getSystemTray();      //Then we will make a pointer to that tray
            
            trayPopup = new PopupMenu();            //Create a new popup menu for the tray item

            MenuItem simpleThing = new MenuItem("Hello!");  //Add the menu items
            trayPopup.add(simpleThing);                     //Add the menu items to the popup
            
            setupProps();
            setupTray();                                    //Setup the tray

        } else {
            System.out.println("Failed to find system tray!");
        }
    }

    private void setupProps(){
        props = new Properties();
        try {
            props.load(new FileInputStream("props.properties")); //If there is already a properties
        } catch (IOException e){
            PropertiesDialog propsDlg = new PropertiesDialog(props);
        }
        
        try {
            auth = null;
            session = Session.getInstance(props, auth);
            store = session.getStore("imap");
            store.connect("imap.gmx.com", "tasbury07@gmx.com", "tasbury07");

            System.out.println("Number of folders: " + store.getPersonalNamespaces().length);

            inboxFolder.close(false);
            store.close();

        } catch ( NoSuchProviderException np ){
            System.out.println("No such provider: " + props.getProperty("protocolProvider"));
            
        } catch (MessagingException me ) {
            System.out.println("Messaging Excpetion!");
            me.printStackTrace();
        }
    }

    private void setupTray(){
        System.out.println("Creating tray!");
        TrayIcon trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("icon.png"), "My popup example tooltip text.");
        trayIcon.setImageAutoSize(true);
        trayIcon.setPopupMenu(trayPopup);
        
        try{
            tray.add(trayIcon);
        } catch (AWTException e){
            System.out.println("Failed to add icon!");
            return;     //If the setup fails, return to the constructor, and pretty much end the program.
        }
        System.out.println("We made da tray icon!");
        timer = new Timer(Integer.parseInt(props.getProperty("interval")) * 60000, this);
        timer.setRepeats(true);
        timer.setActionCommand("CHECK");
        timer.start();      
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("CHECK")){
            //This is where the folder will be checked
            
        }
    }

    // private void exit(){

    // }
}

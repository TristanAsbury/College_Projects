import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

// import mail.com.sun.mail.imap.*;
// import mail.com.sun.mail.*;

public class Notifier implements ActionListener {
    SystemTray tray;
    PopupMenu trayPopup;
    Timer timer;
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

        timer = new Timer(1000, this);
        timer.setRepeats(true);
        timer.setActionCommand("CHECK");
        timer.start();      
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("CHECK")){
            System.out.println("Hello!");
        }
    }

    // private void exit(){

    // }
}

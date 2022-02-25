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
    Timer timer;
    
    Session session;
    Authenticator auth;

    Store store;
    Folder inboxFolder;
    Message[] messages;

    MessageDialog newMailDialog;

    int newMailCount;
    
    SystemTray tray;
    PopupMenu trayPopup;
    MenuItem exitItem, settingsItem, toggleSoundItem;

    int intervalMinutes;
    Properties props;

    public Notifier(){
        newMailCount = 0;
        System.out.println("Creating tray!");
        if(SystemTray.isSupported()){               //If there is a system tray that is supported.
            tray = SystemTray.getSystemTray();      //Then we will make a pointer to that tray
            
            trayPopup = new PopupMenu();            //Create a new popup menu for the tray item

            exitItem = new MenuItem("Exit");  //Add the menu items
            exitItem.addActionListener(this);
            trayPopup.add(exitItem);                     //Add the menu items to the popup

            settingsItem = new MenuItem("Settings");
            settingsItem.addActionListener(this);
            trayPopup.add(settingsItem);

            toggleSoundItem = new MenuItem("Sound");
            toggleSoundItem.addActionListener(this);
            trayPopup.add(toggleSoundItem);
            
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
            store = session.getStore("imaps");

            store.connect(props.getProperty("host"), props.getProperty("username"), props.getProperty("password"));

            inboxFolder = store.getFolder("INBOX");
            inboxFolder.open(Folder.READ_WRITE);

        } catch ( NoSuchProviderException np){
            System.out.println("No such provider: " + props.getProperty("protocolProvider"));
            
        } catch (MessagingException me) {
            System.out.println("Messaging Exception!");
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
        timer = new Timer(Integer.parseInt(props.getProperty("interval")) * 10000, this);
        timer.setActionCommand("CHECK");
        timer.setRepeats(true);
        timer.start();
        
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("CHECK")){
            System.out.println("Checking mail!");
            checkFolder(inboxFolder);
        }

        if(e.getSource().equals(exitItem)){
            try {
                inboxFolder.close(false);
                store.close();
            } catch (MessagingException me) {
                System.out.println("Error closing mail folder!");
            }
            
            System.exit(0);
        }
    }

    private void checkFolder(Folder mailFolder){
        Boolean checked = Boolean.valueOf(false);
        try {
            if(mailFolder.hasNewMessages()){    //IF THERE IS A NEW MESSAGE
                newMailCount = mailFolder.getNewMessageCount();
                if(newMailDialog == null){  //If there is no dialog present, create it 
                    newMailDialog = new MessageDialog(checked, mailFolder, mailFolder.getNewMessageCount());
                } else if(!(mailFolder.getNewMessageCount() == newMailCount)){    //Else, if there is a dialog, delete it, and create a new one
                    newMailDialog.dispose();    //Dispose of the dialog
                    newMailDialog = new MessageDialog(checked, mailFolder, mailFolder.getNewMessageCount());    //Create a new one
                }
            } else {
                System.out.println("No new messages since last check.");
            }
        } catch (MessagingException me){
            System.out.println("Error accessing new mail!");
        }
    }
}
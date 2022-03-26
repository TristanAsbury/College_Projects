//package Client;

import javax.swing.JOptionPane;

class Main {
    public static void main(String[] args){
        String displayName = JOptionPane.showInputDialog(null, "Enter display name:");
        if(displayName == null || displayName.trim().equals("")){
            System.exit(0);
        }
        GUI mainGUI = new GUI(displayName);
    }
}
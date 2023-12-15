import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Esempio JFrame");
        
        // Aggiungi un WindowListener al JFrame
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Azione da eseguire all'uscita del JFrame
                int choice = JOptionPane.showConfirmDialog(frame, "Vuoi davvero uscire?", "Conferma uscita", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    System.exit(0); // Chiudi l'applicazione
                }
            }
        });

        // Altre impostazioni del JFrame
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);
    }
}
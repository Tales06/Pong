import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            try {
                new Pong();
            } catch (FileNotFoundException | UnsupportedEncodingException e) { 
                e.printStackTrace();
            }
            
        });
    }
}
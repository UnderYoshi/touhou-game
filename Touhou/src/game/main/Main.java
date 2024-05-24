package game.main;

import game.component.PanelGame;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *
 * @author under
 */

public class Main extends JFrame{

    /**
     * @param args the command line arguments
     */

    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 1000;

     public Main() {
        init();
     }

     private void init() {
        setTitle("Silly Game");
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        PanelGame panelGame = new PanelGame();
        add(panelGame);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                panelGame.start();
            }
        });


     }
    public static void main(String[] args) {
        Main main = new Main();
        main.setVisible(true);
    }
    
}

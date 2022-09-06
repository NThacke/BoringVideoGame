package village;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/*
 * 
 * This class is the abstract StartUpPanel. It is the information on the startUp menu.
 * 
 * Q: Why make a separate class, instead of creating a JPanel within PanelManager?
 * --> We like to abstract things and separate them as much as possible. This allows the
 *      the most modularity, which in turn allows easy creation/debugging of the program
 *
 */
public class StartUpPanel {

    private PanelManager client;
    private JPanel panel;
    private JLabel topLabel;
    private JButton newGame;
    private JButton loadGame;

    public StartUpPanel(PanelManager client) {
        
        this.client = client;
        this.panel = new JPanel();
        this.topLabel = new JLabel("Global Civilization");
        this.newGame = new JButton("new game");
        this.loadGame = new JButton("load game");

        init();
    }
    public JPanel getPanel() {
        return this.panel;
    }
    private void init() {


        //initializes the JPanel with all of the information of the startUp panel
        //we need to put everything together and call the functionality methods

        //make buttons functional
       
        newGameFunction();
        loadGameFunction();

        //border and layout
        panel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
        panel.setLayout(new GridLayout(0,1));

        //add components
        panel.add(topLabel);
        panel.add(newGame);
        panel.add(loadGame);

    }



    /*
     * Button Functions
     */

    private void newGameFunction() {
        //functionality for newGame button
        newGame.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //When you create a new game, you will create a new text file with a given name
                    //client.getNewGamePanel().changeMessage("");
                    //client.setCurrentPanel(client.getNewGamePanel());

                    client.setCurrentPanel(new NewGamePanel(client));
                }
            }
        );
    }

    private void loadGameFunction() {
        //Functionality for loading a game
        loadGame.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    client.setCurrentPanel(new LoadGamePanel(client));          //makes a new LoadGamePanel (so as to load all the files each time you open it -- fixes the following issue)
                    //client.setCurrentPanel(client.getLoadGamePanel());        //makes a new LoadGamePanel only at runtime. this does not allow one to create a new game and see it within the loadgamepanel unless the program is run again.
                }
            }
        );
    }


}
package village;
import javax.swing.*;

/*
 * This class is the PanelManager that manages all panels shown to the client.
 * 
 * The GUI is a JFrame that must display a JPanel. We will create our own abstract Panel classes
 * for each panel that we want to show to the user. These include, but are not limited to:
 *      
 *      Start Menu
 *      New Game Menu
 *      Load Game Menu
 *      
 *      Game Panels (subject to change)
 *          General UI (options to choose from, display resources, time, current objects, etc.)
 *          
 *          Menus for choosing options within the game
 *              --> We'll have to just create the game and see what we want to add that would be useful/fun/interactive
 * 
 *
 */
public class PanelManager {

    private JFrame frame;
    private JPanel currentPanel;
    private StartUpPanel startUpPanel;
    private NewGamePanel newGamePanel;
    private LoadGamePanel loadGamePanel;
    private DefaultGameMenu defaultGameMenu;

    public PanelManager() {

        //frame setup
        this.frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //internal property setup
        this.currentPanel = new JPanel();
        this.startUpPanel = new StartUpPanel(this);
        this.newGamePanel = new NewGamePanel(this);
        this.loadGamePanel = new LoadGamePanel(this);
        
        //this.defaultGameMenu = new DefaultGameMenu(this);

        //have the startUpPanel to show at start
        setCurrentPanel(startUpPanel);

        //set frame visible
        frame.setVisible(true);
    }

    public StartUpPanel getStartUp() {
        return this.startUpPanel;
    }
    public NewGamePanel getNewGamePanel() {
        return this.newGamePanel;
    }
    public LoadGamePanel getLoadGamePanel() {
        return this.loadGamePanel;
    }
    public DefaultGameMenu getDefaultGameMenu() {
        return this.defaultGameMenu;
    }
    public JPanel getCurrentPanel() {
        return this.currentPanel;
    }

    public void setCurrentPanel(BuildingManager buildingManager) {
        if(currentPanel!=null)
        frame.remove(currentPanel);

        this.currentPanel = buildingManager.getPanel();

        frame.add(currentPanel);
        frame.pack();
    }
    public void setCurrentPanel(DefaultGameMenu defaultMenu) {
        if(currentPanel!=null)
        frame.remove(currentPanel);

        this.currentPanel = defaultMenu.getPanel();

        frame.add(currentPanel);
        frame.pack();
    }
    public void setCurrentPanel(LoadGamePanel loadGamePanel) {
        if(currentPanel!=null)
        frame.remove(currentPanel);

        loadGamePanel.setSelectorText("Select a file");
        this.currentPanel = loadGamePanel.getPanel();

        frame.add(currentPanel);
        frame.pack();
    }
    public void setCurrentPanel(VillagePanel panel) {
        if(currentPanel!=null)
        frame.remove(currentPanel);

        this.currentPanel = panel.getPanel();
        frame.add(currentPanel);
        frame.pack();
    }
    public void setCurrentPanel(PopulationPanel panel) {
        if(currentPanel!= null)
        frame.remove(currentPanel);

        this.currentPanel = panel.getPanel();
        frame.add(currentPanel);
        frame.pack();
    }
    public void setCurrentPanel(NewGamePanel newGamePanel) {
        //remove current panel
        if(currentPanel!=null)
        frame.remove(currentPanel);

        //internal reference
        this.currentPanel = newGamePanel.getPanel();

        //add current panel and pack together
        frame.add(currentPanel);
        frame.pack();
    }

    public void setCurrentPanel(StartUpPanel startUpPanel) {

        //remove the current panel
        if(currentPanel!=null)
        frame.remove(currentPanel);

        //internal reference of current panel
        this.currentPanel = startUpPanel.getPanel();

        //add the current panel
        frame.add(currentPanel);

        //pack everything together in the frame
        frame.pack();
    }


    
}
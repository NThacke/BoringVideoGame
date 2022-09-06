package village;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;


public class VillagePanel {

    private PanelManager client;
    private Game game;
    private JPanel panel;

    private PriorityQueue<Village> villages; //min heap of the villages based on difficulty
    private LinkedList<JLabel> listOfLabels;
    private JButton back;

    public VillagePanel(PanelManager client, Game game) {

        this.client = client;
        this.game = game;
        this.panel = new JPanel();

        this.villages = game.getVillages();
        this.listOfLabels = new LinkedList<JLabel>();
        this.back = new JButton("back");
        createLabels();

        init();

        backButtonFunctional();
    }

    public JPanel getPanel() {
        return this.panel;
    }

    private void init() {
        //initializes the panel with JLabels and buttons
        //border and layout
        panel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
        panel.setLayout(new GridLayout(0,1));

        panel.add(new JLabel("Nearby Villages"));
        panel.add(new JLabel());
        addLabels();
        panel.add(new JLabel());
        panel.add(back);
    }
    private void addLabels() {
        //adds the linked list of labels to the JPanel
        Iterator<JLabel> iterator = listOfLabels.iterator();
        while(iterator.hasNext()) {
            panel.add(iterator.next());
        }
    }
    private void createLabels() {
        //creates the linked list of labels of villages
        Iterator<Village> iterator = villages.iterator();
        while(iterator.hasNext()) {
            listOfLabels.add(new JLabel(iterator.next().toString()));
        } 
    }

    private void backButtonFunctional() {
        back.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //go back to the startUpPanel
                    client.setCurrentPanel(game.getGameMenu());
                }
            }
        );
    }
}
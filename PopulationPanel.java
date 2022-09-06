package village;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;


/*
 * The abstract class representing the PopulationPanel
 * 
 */
public class PopulationPanel {

    //information regarding the population

    private PanelManager client;
    private Game game;
    private JPanel panel;

    private JLabel popCount;
    private JLabel workingCount;
    private JLabel unemployed;

    private JLabel totalLost; 
    private JLabel lostInBattle; //number of people lost in combat
    private JLabel lostFromDisease; 


    private JLabel researchers;
    private JLabel foodGatherers;

    private JButton addGatherer;
    private JButton addResearcher;
    private JButton removeGatherer;
    private JButton removeResearcher;

    private JButton back;

    public PopulationPanel(PanelManager client, Game game) {
        //internals
        this.client = client;
        this.game = game;
        this.panel = new JPanel();

        //population information
        this.popCount = new JLabel("Population: " + game.getPopulation());
        this.workingCount = new JLabel("Working: " + game.getWorkingCount());
        this.unemployed = new JLabel("Not working: " + game.getUnemployed());

        //workers information
        this.researchers = new JLabel("Researching: " + game.getResearchers());
        this.foodGatherers = new JLabel("Gathering food: " + game.getGatherers());

        //buttons
        this.addGatherer = new JButton("Add Gatherer");
        this.addResearcher = new JButton("Add Researcher");
        this.removeGatherer = new JButton("Remove Gatherer");
        this.removeResearcher = new JButton("Remove Researcher");

        this.back = new JButton("back");

        //methods
        init();
        makeButtonsFunctional();

    }

    public void update() {
        /*
         * For some reason, we must update the population panel by actually creating a new instance of a population panel, rather than modifying the texts already diplayed
         * This is interesting, as others panels (DefaultGameMenu for example) update via the latter method with no issue
         * 
         * What could cause this to happen?
         * -->Possiblities:
         *     
         *      1) There is some difference between the way that populationPanels and other panels are stored/implemeneted within the backend of the game
         *          --> I don't believe this to be the case, as every panel is stored internally with a private property, are instantiazed within the same methods, and all follow similar paradigms in their design.
         *  
         *      Personally, this is the most annoying aspect of programming this nproject. I cannot figure out why this occurrs, and all attempts have been futile and led to frustration.
         *      
         *      The only solution to work around this is to create a new instance of a populationPanel if the user is currently viewing a populationPanel.
         *      --> This works, and seems to the user as though it 'works', but as a developer, I despsie this approach. I cannot understand what causes this, which is why I try to not use work-arounds like this. However, it does work... just not as good as I would want it to!
         * 
         * 
         *         UPDATE!!
         * 
         *      The issue was that whenever we went into the populationPanel, we created a new instance of a population panel. The game's backend would try to update it's own reference to a population panel,
         *      but whenever we loaded one, we created a new one that is a different reference than what would be stored internally! This meant that whenever we tried to update something, we would do it to a panel
         *      that would never be displayed again (it was only ever seen by the backend, the user actually never saw this! Remember: each time a button to go into the populationPanel was pressed, it would NOT access the backend pop panel,
         *      but would rather create its own new instance of a pop panel!), as anytime we looked at populationPanel, it was a new instance of a populationPanel!
         * 
         *      Software is fun.
         * 
         * 
         *      Lesson learned/paradigm for the future:
         *      
         *      Create new instances of properties only when constructing a new instance of that object.
         *      When you wish to modify a property, do so via an update() method, rather than creation of a new instance.
         *      
         *      This seems very simple and obvious in hindsight (of course it's better to simply update than create a whole new object!) but in the process of programming, one typically does not think as clearly as they do once the project is complete.
         */
        
        //updates all pop counts
       // client.setCurrentPanel(new PopulationPanel(client, game))

        
        updatePopCount();
        updateGatherers();
        updateResearchers();
        updateUnemployed();
        updateWorkingCount();
        
    }
    public JPanel getPanel() {
        return this.panel;
    }
    public void updatePopCount() {
        //System.out.println("updating population");
        popCount.setText("Population: " + game.getPopulation());
    }
    public void updateWorkingCount() {
        //System.out.println("updating working count");
        workingCount.setText("Working: " + game.getWorkingCount());
    }
    public void updateUnemployed() {
        //System.out.println("updating unemployed");
        unemployed.setText("Not Working: " + game.getUnemployed());
    }
    public void updateResearchers() {
        //System.out.println("updating researchers");
        researchers.setText("Researching: " + game.getResearchers());
    }
    public void updateGatherers() {
        //System.out.println("updating gatherers");
        foodGatherers.setText("Gathering food: " + game.getGatherers());
    }
    private void init() {
        //makes the JPanel initalized with all of labels and buttons
        //border and layout
        panel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
        panel.setLayout(new GridLayout(5,5));

        //row 1
        panel.add(back);
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        //row 2
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        //row 3
        panel.add(popCount);
        panel.add(new JLabel());
        panel.add(foodGatherers);
        panel.add(new JLabel());
        panel.add(researchers);

        //row 4
        panel.add(workingCount);
        panel.add(new JLabel());
        panel.add(addGatherer);
        panel.add(new JLabel());
        panel.add(addResearcher);

        //row 5
        panel.add(unemployed);
        panel.add(new JLabel());
        panel.add(removeGatherer);
        panel.add(new JLabel());
        panel.add(removeResearcher);



    }
    private void makeButtonsFunctional() {
        addGathererFunctional();
        addResearcherFunctional();
        removeGathererFunctional();
        removeResearcherFunctional();
        backButtonFunctional();
    }

    //functions of add and remove buttons
    private void addGathererFunctional() {
        addGatherer.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //call the backend method of addGatherer
                    game.addGatherer();
                }
            }
        );
    }

    private void removeGathererFunctional() {
        removeGatherer.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //call backend method
                    game.removeGatherer();
                }
            }
        );
    }

    private void addResearcherFunctional() {
        addResearcher.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.addResearcher();
                }
            }
        );
    }

    private void removeResearcherFunctional() {
        removeResearcher.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.removeResearcher();
                }
            }
        );
    }

    //back button
    private void backButtonFunctional() {
        back.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    client.setCurrentPanel(game.getGameMenu()); //if static(non-changin) JLabels and buttons
                    //client.setCurrentPanel(new DefaultGameMenu(client, game)); //changing JLabels
                }
            }
        );
    }
}
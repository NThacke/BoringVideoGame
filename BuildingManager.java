package village;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/*
 * The abstract class representing the Building Manager where the user
 * chooses which buildings to build
 * 
 * **********
 * Buildings
 * **********
 * 
 * Cottages: 
 *      require 5 people to build. 
 *      Takes 30s to build. 
 *      Increases population
 *      No requirement for workers to work there
 * 
 * Farms
 *      require 10 people to build
 *      Takes 2 minutes to build
 *      Increases food
 *      Requires 5 people working at the building to increase food
 * 
 * Tannery
 *      require 10 people to build
 *      takes 2 minutes to build
 *      produces leather
 *      requires 5 people working to produce products
 * 
 * Mine
 *      requires 20 people to build
 *      takes 5 minutes to build
 *      has a chance to produce each of the following ores:
 *          copper <-> gold -> coal -> iron  -> something else
 *      requires 20 workers
 * 
 ****************      
 * Ore Trees
 * *************
 * The player begins by only being able to discover copper and gold from their mines. Once at least five ores of each have been discovered,
 * then coal can be discovered. Once coal is discovered, iron can be discovered, and so on
 * 
 * Each ore will have a random chance to be produced by each mine every second
 *      Copper: 1%
 *      Gold: 1%
 *      Coal: 1%
 *      Iron: 0.5%
 * 
 * We may increase the chances as we see fit while balancing the game.
 * 
 * **************
 * Buttons
 * **************
 * 
 * For each building, have these buttons:
 *      
 *      BuildingType
 *          brings the user to another panel that will display information regarding the building type
 *          --> what it does, how many people to build, how long it takes, etc.
 * 
 *      Build 'buildingType'
 *          begins building
 *                  
 *      Add workers (adds x amount of workers from the working count of this building type)
 *      Remove workers (removes x amount of workers from the working count of this building type)
 *          --> x is the amount of people that it takes to have one of these buildings produce
 *          --> for example, tannery would have x = 5, and farm would have x = 5
 * 
 *      Add all workers (adds whatever amount of workers it takes to fill every job working in these buildings)
 *      Remove all worrkers (removes every worker working in this building type)
 * 
 * 
 * ********
 * Labels
 * *********
 * 
 * For each building, have a label that displays information about the building:
 *      
 *      currentCount, numberInProgress, numberOfWorkers (working for that building type)
 *      
 *  
 * 
 *      
 *      
 *      
 */
public class BuildingManager{

    //Internals to talk between the game backend (Game) and the user's JFrame (PanelManager)
    private PanelManager client;
    private Game game;
    private JPanel panel;

    
    //Cottage Buttons

    private JLabel cottageCount;    //displays the number of cottages
    private JLabel cottageInProgress;   //dispalys number of cottages in progress
    private JButton cottage;        //brings the user to cottagePanel 
    private JButton buildCottage;   //builds a cottage
    //no worker buttons as cottages don't need workers

    //Farm buttons
    private JLabel farmCount;
    private JLabel farmInProg;
    private JButton farm;           //brings the user to farmPanel
    private JButton buildFarm;      //builds a farm
    private JButton addFarmWorker;  //adds workers to one farm
    private JButton addAllFarmWorkers;  //fills every farm position
    private JButton removeFarmWorker; //removes workers from one farm
    private JButton removeAllFarmWorkers;   //removes every worker working at farms

    //tannery buttons
    private JLabel tanneryCount;
    private JLabel tanneryInProg;
    private JButton tannery;        //brings user to tanneryPanel
    private JButton buildTannery;   //builds a tannery
    private JButton addTanneryWorker;   //adds workers to one tannery
    private JButton addAllTanneryWorkers;   //fills every tannery position
    private JButton removeTanneryWorker; //removes workers at one tannery
    private JButton removeAllTanneryWorkers;    //removes all workers at tanneries

    //mine buttons
    private JLabel mineCount;
    private JLabel mineInProg;
    private JButton mine;           //brings user to minePanel
    private JButton buildMine;      //builds a mine
    private JButton addMineWorker;
    private JButton addAllMineWorkers;
    private JButton removeMineWorker;
    private JButton removeAllMineWorkers;

    private JButton backButton;         //back button


    public BuildingManager(PanelManager client, Game game) {
        this.client = client;
        this.game = game;
        this.panel = new JPanel();

        //Cottage Buttons Initialization
        this.cottageCount = new JLabel(String.valueOf(game.getCottageCount() + "    " + game.getCottagesBeingBuilt()) + "   " + "100%");
        this.cottage = new JButton("Cottage");
        this.buildCottage = new JButton("Build cottage");
        
        //Farm button initalizations
        this.farmCount = new JLabel(String.valueOf(game.getFarmCount() + "  " + game.getFarmsInProg()) + "  " + "0%");
        this.farm = new JButton("Farm");
        this.buildFarm = new JButton("Build Farm");
        this.addFarmWorker = new JButton("Add workers");    //adds workers to one farm                                  ** change this text display **
        this.addAllFarmWorkers = new JButton("Add all workers");        //fills every farm position with workers        ** change this text display **
        this.removeFarmWorker = new JButton("Remove workers");
        this.removeAllFarmWorkers = new JButton("Remove all workers");

        //Tannery Button initalizations
        this.tanneryCount = new JLabel(String.valueOf(game.getTanneryCount() + "    " + game.getTanneryInProg()) + "    " + "0%");                    //0 hardcode
        this.tannery = new JButton("Tannery");
        this.buildTannery = new JButton("Build tannery"); 
        this.addTanneryWorker =new JButton("Add worker");
        this.addAllTanneryWorkers = new JButton("Add all workers");
        this.removeTanneryWorker = new JButton("Remove worker");
        this.removeAllTanneryWorkers = new JButton("Remove all workers");

        //Mine Button initalizations
        this.mineCount = new JLabel(String.valueOf(game.getMineCount() + "  " + game.getMineInProg()) + "   " + "0%");
        this.mine = new JButton("Mine");
        this.buildMine = new JButton("Build Mine");
        this.addMineWorker = new JButton("Add worker");
        this.addAllMineWorkers = new JButton("Add all workers");
        this.removeMineWorker = new JButton("Remove worker");
        this.removeAllMineWorkers = new JButton("Remove all workers");


        //back Button initalization
        this.backButton = new JButton("back");

        update();
        makeButtonsFunctional();
        init();

    }

    public JPanel getPanel() {
        return this.panel;
    }

    public void update() {
        //updates all of the information shown to the user to be what is internally stored in the backend
        game.getPopPanel().update();

        cottageCount.setText(String.valueOf(game.getCottageCount() + "  " + game.getCottagesBeingBuilt()) + "   " + "100%");

        int[] farmInfo = game.getFarmInfo();
        double farms = farmInfo[0];
        double farmWorkers = farmInfo[2];
        double farmPercent = 100*farmWorkers/(5*farms);

        System.out.println("Number of farms: " + farms + "Number of farm workers: " + farmWorkers + " farmPercent: " + farmPercent);
        farmCount.setText(String.valueOf(game.getFarmCount() + "    " +game.getFarmsInProg()) + "   " + farmPercent + "%");

        int[] tanInfo = game.getTanInfo();
        double tanneries = tanInfo[0];
        double tanWorkers =tanInfo[2];
        double tanPercent = 100*tanWorkers/(10*tanneries);
        tanneryCount.setText(String.valueOf(game.getTanneryCount() + "  " + game.getTanneryInProg()) + "    " + tanPercent + "%");

        int[] mineInfo = game.getMineInfo();
        double mines = mineInfo[0];
        double mineWorkers = mineInfo[2];
        double minePercent = 100*mineWorkers/(20*mines);

        mineCount.setText(String.valueOf(game.getMineCount() + "    " +game.getMineInProg()) + "    " + minePercent + "%");
    }

    private void init() {
        //initalize the panel with the information
        panel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
        panel.setLayout(new GridLayout(6,7));

        //row 1
        panel.add(backButton);
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());

        //row 2, empty to have a space in the GUI
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());

        //row 3
        panel.add(cottageCount);
        panel.add(cottage);
        panel.add(buildCottage);
        panel.add(new JLabel());    //cottages have no workers, display empty JLabel as a space in the GUI
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());

        //row 4
        panel.add(farmCount);
        panel.add(farm);
        panel.add(buildFarm);
        panel.add(addFarmWorker);
        panel.add(addAllFarmWorkers);
        panel.add(removeFarmWorker);
        panel.add(removeAllFarmWorkers); 
        
        //row 5
        panel.add(tanneryCount);
        panel.add(tannery);
        panel.add(buildTannery);
        panel.add(addTanneryWorker);
        panel.add(addAllTanneryWorkers);
        panel.add(removeTanneryWorker);
        panel.add(removeAllTanneryWorkers);

        //row 6
        panel.add(mineCount);
        panel.add(mine);
        panel.add(buildMine);
        panel.add(addMineWorker);
        panel.add(addAllMineWorkers);
        panel.add(removeMineWorker);
        panel.add(removeAllMineWorkers);
    }

    private void makeButtonsFunctional() {

        cottageButtons();
        farmButtons();
        tanneryButtons();
        mineButtons();

        backButtonFunctional();
    }

    private void cottageButtons() {
        buildCottageFunctional();
        cottageFunctional();         //take user to cottagePanel
    }

    private void cottageFunctional() {
        cottage.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //bring user to the farmPanel
                    //the farmPanel will display what the farm does, how many people it takes to build, how long it takes to build, etc. Essentially shows information about a farm.
                    //client.setCurrentPanel(new CottagePanel(client, game));  //I will probably code these panels at a later time. I just want to have the backend working right now.
                }
            }
        );
    }

    /*
     * 
     * 
     * Farm Buttons
     * 
     */

    private void farmButtons() {
        farmFunctional();
        buildFarmFunctional();
        addFarmerFunctional();
        addAllFarmerFunctional();
        removeFarmerFunctional();
        removeAllFarmerFunctional();
    }

    private void farmFunctional() {
        farm.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //bring user to the farmPanel
                    //the farmPanel will display what the farm does, how many people it takes to build, how long it takes to build, etc. Essentially shows information about a farm.
                    //client.setCurrentPanel(new FarmPanel(client, game));
                }
            }
        );
    }

    private void buildFarmFunctional() {
        buildFarm.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //builds a farm
                    game.buildFarm();
                    update();
                }
            }
        );
    }

    private void addFarmerFunctional() {
        addFarmWorker.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //adds enough workers to a farm to make it produce food
                    game.addFarmer();
                    update();
                }
            }
        );
    }

    private void addAllFarmerFunctional() {
        addAllFarmWorkers.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //fills as many workers into farmers as possible
                    game.addAllFarmers();
                    update();
                }
            }
        );
    }

    private void removeFarmerFunctional() {
        removeFarmWorker.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //removes workers from one farm
                    game.removeFarmer();
                    update();
                }
            }
        );
    }

    private void removeAllFarmerFunctional() {
        removeAllFarmWorkers.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //removes every farmer working at farms
                    game.removeAllFarmers();
                    update();
                }
            }
        );
    }

    /*
     * Tannery buttons
     */

    private void tanneryButtons() {
        //tanneryFunctional();
        buildTanneryFunctional();
        addTanneryWorkerFunctional();
        addAllTanneryWorkers();
        removeTanneryWorker();
        removeAllTanneryWorkers();
    }

    private void buildTanneryFunctional() {
        buildTannery.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.buildTannery();
                    update();                    //update building panel            --> we could actually have a game.update() method that would do this for us
                }
            }
        );  
    }

    private void addTanneryWorkerFunctional() {
        addTanneryWorker.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.addTanneryWorker();
                    update();                    //update building panel            --> we could actually have a game.update() method that would do this for us
                }
            }
        );  
    }
    private void addAllTanneryWorkers() {
        addAllTanneryWorkers.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.addAllTanneryWorkers();
                    update();                    //update building panel            --> we could actually have a game.update() method that would do this for us
                }
            }
        );  
    }
    private void removeTanneryWorker() {
        removeTanneryWorker.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.removeTanneryWorker();
                    update();                    //update building panel            --> we could actually have a game.update() method that would do this for us
                }
            }
        );  
    }
    private void removeAllTanneryWorkers() {
        removeAllTanneryWorkers.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.removeAllTanneryWorkers();
                    update();                    //update building panel            --> we could actually have a game.update() method that would do this for us
                }
            }
        );  
    }

    

    /*
     * Mine Buttons
     */

    private void mineButtons() {
        //mineButton();
        buildMine();
        addMiner();
        addAllMiners();
        removeMiner();
        removeAllMiners();
    }

    private void mineButton() {
        //would bring the user to a mine panel when pressed
    }

    private void buildMine() {
        buildMine.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.buildMine();
                    update();                    //update building panel            --> we could actually have a game.update() method that would do this for us
                }
            }
        );  
    }
    private void addMiner() {
        addMineWorker.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.addMiner();
                    update();                    //update building panel            --> we could actually have a game.update() method that would do this for us
                }
            }
        );  
    }
    private void addAllMiners() {
        addAllMineWorkers.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.addAllMiners();
                    update();                    //update building panel            --> we could actually have a game.update() method that would do this for us
                }
            }
        );  
    }
    private void removeMiner() {
        removeMineWorker.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.removeMiner();
                    update();                    //update building panel            --> we could actually have a game.update() method that would do this for us
                }
            }
        );  
    }
    private void removeAllMiners() {
        removeAllMineWorkers.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.removeAllMiners();
                    update();                    //update building panel            --> we could actually have a game.update() method that would do this for us
                }
            }
        );  
    }




    private void buildCottageFunctional() {
        buildCottage.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //build a cottage
                    game.buildCottage();
                    update();
                }
            }
        );
    }

    private void backButtonFunctional() {
        backButton.addActionListener( 
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    client.setCurrentPanel(game.getGameMenu());         //set the client panel to be the game menu. menu's of the game can be retrieved from the game backened, which stores all of the panels that are used for itself
                }
            }
        );
    }
    
}
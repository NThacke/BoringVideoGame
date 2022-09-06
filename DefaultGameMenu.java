package village;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.util.Timer;

public class DefaultGameMenu {
    
    private PanelManager client; //GUI client
    private Game game;      //the game being displayed
    private JPanel panel;   //the actual JPanel that is used 

    private PriorityQueue<Village> villages;
    
    private JLabel topLabel;        //tell client that this is a game
    
    private JLabel HHMM;    //displays HHMM above the userTime
    private JLabel userTime; //displays HH:MM
    private JLabel dayLabel; //says "Day" on top of the day JLabel
    private JLabel day; //displays the day

    private JLabel population;
    private JLabel food;
    private JLabel researchLevel;

    private JLabel displayPlusOnePop; //diplays +1 for 1s whenever population increases
    private JLabel displayFoodPop;

    private JButton otherVillages;
    private JButton populationInformation;

    private JButton buildingManager;        //brings you to building manager panel

    private JButton save;
    private JButton exit;

    public DefaultGameMenu(PanelManager client, Game game) {
        this.client = client;
        this.game = game;
        this.panel = new JPanel();

        this.villages = game.getVillages();

        this.topLabel = new JLabel("Village Game");

        this.HHMM = new JLabel("HH:MM");
        this.userTime = new JLabel(game.getUserTime());
        this.day = new JLabel(String.valueOf(game.getDay()));
        this.dayLabel = new JLabel("Day");


        this.population = new JLabel("Population: " + String.valueOf(game.getPopulation()));
        this.food = new JLabel("Food: " + String.valueOf(game.getFood()));
        this.researchLevel = new JLabel("Research: " + String.valueOf(game.getResearchLevel()));

        this.displayPlusOnePop = new JLabel("");
        this.displayFoodPop = new JLabel("");

        this.populationInformation = new JButton("Population Manager");
        this.otherVillages = new JButton("Villages");
        this.buildingManager = new JButton("Building Manager");

        this.save = new JButton("save");
        this.exit = new JButton("exit");

        init();
        makeButtonsFunctional();

    }

    public JPanel getPanel() {
        return panel;
    }
    public void updatePopulation() {
        population.setText("Population: " + getAsString(game.getPopulation()) );
    }
    public void updateFood() {
        food.setText("Food: " + getAsString(game.getFood()) );
    }
    public void foodPopFarm(int f) {
        if(f==0)
        return;

        String num = "+" + String.valueOf(f);
        displayFoodPop.setText(num);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
              //set the label back to empty after 1000ms
              displayFoodPop.setText(" ");
              timer.cancel();
            }
          }, 1000, 1000);//wait 1000 ms before doing the action and do it evry 1000ms (1second)
    }
    public void foodPopPlus() {

        displayFoodPop.setText("+1");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            //set the label back to empty after 1000ms
            displayFoodPop.setText(" ");
            timer.cancel();
          }
        }, 1000, 1000);//wait 1000 ms before doing the action and do it evry 1000ms (1second)
    }
    public void foodPopMinus() {
        displayFoodPop.setText("-1");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            //set the label back to empty after 1000ms
            displayFoodPop.setText(" ");
            timer.cancel();
          }
        }, 1000, 1000);//wait 1000 ms before doing the action and do it evry 1000ms (1second)
    }
    public void displayPlusOnePop() {

        displayPlusOnePop.setText("+1");
        
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            //set the label back to empty after 1000ms
            displayPlusOnePop.setText(" ");
            timer.cancel();

          }
        }, 1000, 1000);//wait 0 ms before doing the action and do it evry 1000ms (1second)
    }
    public void updateTime() {
        //updates the time shwon to the user
        //get the time from the backend and make it be what is displayed as the JLabels for day and HHMM

        day.setText(String.valueOf(game.getDay()));
        userTime.setText(game.getUserTime());

    }


    private static String getAsString(int number) {
        //returns the number as a string with a 1,000,000,000,000 format (it has commas)
        String s = String.valueOf(number);
        //adds commas every three characters starting from the back
        if(s.length()>3) {  //only do this for numbers that are 3+ characters long
            int j =0;
            for(int i =s.length()-1; i>=0; i--) {
                j++;
                if(j%3==0) { //every 3 iterations add a comma
                    //split up s into two substrings: one before where we are and one after
                    String temp = s.substring(0, i);
                    String temp2 = s.substring(i, s.length());
                    //add a comma between
                    temp += ",";
                    //merge the string back
                    s = temp + temp2;
                }
                
            }
        }
        return s;
    }

    private void init() {
        //initializes the game menu/panel
        //border and layout
        panel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
        panel.setLayout(new GridLayout(7,5));

        //row 1
        panel.add(exit);
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(save);

        //row 2
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(topLabel);
        panel.add(dayLabel);
        panel.add(HHMM);

        //row 3
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(day);
        panel.add(userTime);

        //row 4
        panel.add(new JLabel());
        panel.add(displayPlusOnePop);
        panel.add(displayFoodPop);
        panel.add(new JLabel());
        panel.add(new JLabel());

        //row 5
        panel.add(new JLabel());
        panel.add(population);
        panel.add(food);
        panel.add(researchLevel);
        panel.add(new JLabel());

        //row 6
        panel.add(new JLabel());
        panel.add(new JLabel());
        panel.add(otherVillages);
        panel.add(new JLabel());
        panel.add(new JLabel());

        //row 7
        panel.add(new JLabel());
        panel.add(populationInformation);
        panel.add(new JLabel());
        panel.add(buildingManager);
        panel.add(new JLabel());
        

    }
    private void makeButtonsFunctional() {
        villageButtonFunctional();
        populationInformationFunctional();
        buildingMangerFunctional();

        saveButtonFunctional();
        exitButtonFunctional();
    }
    private void buildingMangerFunctional() {
        //Go to building manager panel
        buildingManager.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.getBuildingManager().update();
                    client.setCurrentPanel(game.getBuildingManager());
                }
            }
        );
    }
    private void populationInformationFunctional() {
        //go to population manager panel
        populationInformation.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //client.setCurrentPanel(new PopulationPanel(client, game));
                    client.setCurrentPanel(game.getPopPanel());
                }
            }
        );
    }
    private void villageButtonFunctional() {
        //go to nearby villages panel
        otherVillages.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //load the other village panel that displays the other villages
                   client.setCurrentPanel(new VillagePanel(client, game));
                }
            }
        );
    }

    private void saveButtonFunctional() {
        //save the game
        save.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.saveGame();
                }
            }
        );
    }

    private void exitButtonFunctional() {
        //exit the game (back to start menu)
        exit.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.saveGame();
                    client.setCurrentPanel(client.getStartUp());
                }
            }
        );
    }


}
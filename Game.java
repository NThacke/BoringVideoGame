package village;
import java.io.File;
import java.util.*;

import javax.swing.Action;

import java.io.PrintStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;

/*
 * This is the backend of the game. We want to have it so that you tell people to
 * be working in certain areas. You dictate the amount of people working in each area.
 * 
 * The production of each area (farming, hunting, resarch) depends on the number of people
 * working there, as well as the research level.
 * 
 * Time always progresses, and with each time interval, units of food and other resources are produced.
 * The population grows with each time interval dependent on the current population
 * 
 * Crisis:
 *      Attacked by another village
 *      Disease
 *      
 * 
 * 
 * As the population grows, new opportunities arise for the player to do.
 * --> Production becomes a % of the population in each industry
 * -->
 */

public class Game {

    private PanelManager client;        //panel manager
    private DefaultGameMenu defaultGameMenu; //the default menu where you choose options as a leader. This has sub menus of choices
    private PopulationPanel populationPanel;
    private BuildingManager buildingManager;    //the building manager menu
    private String filename;            //the file of savegame




    private int POPULATION;             //population level
    private int FOOD;                   //food level
    private int RESEARCH_LEVEL;         //research level
    private long time;                  //how long the game has been played for
    private int day;                    //the day
    private String userTime;            //the actual time displayed to the user (HH:MM)
    
    private int workingCount;           //number of workers
    private int researcher;             //number of researchers
    private int gatherers;              //number of gatherers
                                        

                                        //[0] number of buildings
                                        //[1] number of buildings in progress
                                        //[2] number of workers working at this building type
                                        //[3] number of people it takes to build .... (why is this a thing that's stored? We may make it so it decreases as the user progress, but that's not really necessary right now).

                                        //                                      [0]             [1]                 [2]             [3]
    private int[] cottageInfo;          //stores, with each respective index: cottages, cottagesInProgress, cottageWorkCount, cottageBuildNumber
    private int[] farmInfo;             //stores, with each respective index: numberOfFarms, farmsInProgress, farmWorkCount,  farmBuildNumber 
    private int[] tanneryInfo;          //                                    numberOfTans   tansInProg         tanWorkCount    tanBuildNumber
    private int[] mineInfo;              //....                                numberOfMines,  minesInprogress, mineWorkCount, mineBuildNumber

    private Stack<Integer> cottageTimers;  //the number of seconds left to build each cottage. When we build a cottage, we add a 30 to the stack. Every second, we remove every element from this stack, subtract one from it, and put it back in the Stack. To do this, a temporary Stack is used. See the updateCottageTimers() method for further information.
                                           //we need to store the timers in order to save/load them

    private Stack<Integer> farmTimers;      //number of seconds left to build each farm

    private Stack<Integer> tanTimers;
    private Stack<Integer> mineTimers;

    private PriorityQueue<Village> villages; //min heap of the villages based on difficulty

    public Game(PanelManager client, String filename) {

        this.client = client;
        this.filename = filename;
        //Initalize timers
        cottageTimers = new Stack<Integer>();   //needs to be created before loading as we will insert elements into it from loading files
        farmTimers = new Stack<Integer>();       
        tanTimers = new Stack<Integer>();
        mineTimers = new Stack<Integer>();

        villages =  new PriorityQueue<Village>();   //      same as above ^
        
        
        
        cottageInfo = new int[4];
        farmInfo = new int[4]; 
        tanneryInfo = new int[4];
        mineInfo = new int[4];                   
        
        loadGame();     //loads the game from the filename, setting up all population, food, time, etc
       
        //Initalize panels related to the game
        defaultGameMenu = new DefaultGameMenu(client, this);        //make the GUI panel for game menu of this instance of game
        populationPanel = new PopulationPanel(client, this);
        
        buildingManager = new BuildingManager(client, this);

        
        client.setCurrentPanel(defaultGameMenu);
        play();         //play the game


    }
    public PopulationPanel getPopPanel() {
        return populationPanel;
    }
    public int[] getCottageInfo() {
        return cottageInfo;
    }
    public int[] getFarmInfo() {
        return farmInfo;
    }
    public int[] getMineInfo() {
        return mineInfo;
    }
    public int[] getTanInfo() {
        return tanneryInfo;
    }

    /*
     * 
     * 
     * Farms
     * 
     * 
     */

    private void farmFood() {
        //increase food supply by 10 for every farm in operation every 24 hrs
        if(userTime.equals("00:00")) {
            FOOD += 10*farmInfo[2]/5;
            defaultGameMenu.foodPopFarm(10*farmInfo[2]/5);
        }
    }
    public int getFarmCount() {
        return farmInfo[0];
    }
    public int getFarmsInProg() {
        return farmInfo[1];
    }

    public void buildFarm() {
        //attempts to build a farm. It takes farmInfo[3] to build a farm
        if(getUnemployed()>=farmInfo[3]) {
            workingCount += farmInfo[3];    //increase the amount of people working
            farmInfo[1]++;                  //increase the amount of farms in progress

            farmTimers.add(60);             //add a timer to farmTimer. 60s to build a farm
        }
    }

    public void addFarmer() {
        //attempts to add farmers to one farm building
        //System.out.println("unemployed: " + getUnemployed() + " working at farms: " + farmInfo[2] + " capcaity working at farms: " + 5*farmInfo[0]);
        if(getUnemployed() >= 5 && farmInfo[2] < 5*farmInfo[0])  {  //number of workers < 5 * number of buildings
            farmInfo[2] += 5;           //add 5 workers to farm workers
            workingCount += 5;          //add 5 workers to working count
            populationPanel.update();
        }
    }
    public void addAllFarmers() {
        //attempts to add as many as possible unemployed people to be farmers
        //recurisve method,
        //ends when unemployed is less than number of workers to work at farm, or when every farm position is full of workers.
        if(getUnemployed() < 5 || farmInfo[2] == 5*farmInfo[0])
        return;

        //add a farmer, and call this method
        addFarmer();
        addAllFarmers();
    }
    public void removeFarmer() {
        //attempts to remove farmers working at a farm
        if(farmInfo[2] > 0) { //if there's a worker,
            
            farmInfo[2] -= 5;   //remove 5 workers
            workingCount -= 5;
            populationPanel.update();
        }
    }
    public void removeAllFarmers() {
        //recurive method similar to addAllFarmers
        //return when no more workers
        if(farmInfo[2] == 0)
        return;

        removeFarmer();
        removeAllFarmers();
    }
    /*
     * 
     * Tanneries
     * 
     */

    public int getTanneryCount() {
        return tanneryInfo[0];
    }
    public int getTanneryInProg() {
        return tanneryInfo[1];
    }
    public void buildTannery() {
        //attempts to build a tannery
        if(getUnemployed() >=tanneryInfo[3]) {
            workingCount += tanneryInfo[3];
            tanneryInfo[1]++;

            tanTimers.add(120);
        }
    }
    public void addTanneryWorker() {
        if(getUnemployed() >= 10 && tanneryInfo[2] < 10*tanneryInfo[0]) {
            tanneryInfo[2] += 10;
            workingCount += 10;
        }
    }
    public void addAllTanneryWorkers() {
        if(getUnemployed() < 10 || tanneryInfo[2] == 10*tanneryInfo[0])
        return;

        addTanneryWorker();
        addAllTanneryWorkers();
    }
    public void removeTanneryWorker() {
        if(tanneryInfo[2]>0) {
            tanneryInfo[2] -= 10;
            workingCount -= 10;
        }
    }
    public void removeAllTanneryWorkers() {
        if(mineInfo[2] == 0)
        return;

        removeTanneryWorker();
        removeAllTanneryWorkers();
    }
    /*
     * 
     * Mines
     * 
     * 
     * 
     */
    public int getMineCount() {
        return mineInfo[0];
    }
    public int getMineInProg() {
        return mineInfo[1];
    }
    public void buildMine() {
        //attempts to build a mine
        if(getUnemployed()>=mineInfo[3]) {
            workingCount += mineInfo[3];    //increase the amount of people working
            mineInfo[1]++;                  //increase the amount of mines in progress

            mineTimers.add(300);             //add a timer to farmTimer. 300s = 5mins to build one mine
        }
    }
    
    public void addMiner() {
        if(getUnemployed() >= 20 && mineInfo[2] < 20*mineInfo[0]) {
            mineInfo[2]+= 20;   //increase worker count and workers at mine count
            workingCount += 20;
        }
    }
    public void addAllMiners() {
        if(getUnemployed() < 20 || mineInfo[2] == 20*mineInfo[0])
        return;

        addMiner();
        addAllMiners();
    }
    public void removeMiner() {
        if(mineInfo[2] > 0) {
            mineInfo[2] -= 20;
            workingCount -= 20;
        }
    }
    public void removeAllMiners() {
        if(mineInfo[2] == 0)
        return;
        removeMiner();
        removeAllMiners();
    }

    /*
     * 
     * Cottages
     * 
     * 
     */
    public void buildCottage() {
        //adds a cottage being built. It takes x people y seconds to build a cottage
        if(getUnemployed()>=cottageInfo[3]) {

            workingCount += cottageInfo[3];
            cottageInfo[2] += cottageInfo[3];   //cottageWorkCount += cottageBuildNumber;
            cottageInfo[1]++;                   //cottageInProgress++;

            //System.out.println("Building new cottage");
            //System.out.println("working: " + workingCount + "; number of people to build a cottage: " + cottageInfo[3]);
            //make a timer that will increase cottage count after 30s
            cottageTimers.add(30); //add a 30s timer to cottage timers; this can be changed in the future to be y amount of time
        }
        else {
            //tell user that they can't build a cottage
        }
    }
    public int getCottageCount() {
        //number of cottages that have been built
        return cottageInfo[0];
    }
    public int getCottagesBeingBuilt() {
        //number of cottages being built
        return cottageInfo[1];
    }
    public int getWorkingOnCottages() {
        //number of people working on cottages
        return cottageInfo[2];
    }
    public int cottageBuildNumber() {
        //number of people it takes to build a cottage
        return cottageInfo[3];
    }
    public void addGatherer() {
        //adds a gatherer if there is an unemployed worker
        //also update the population panel shown to user
        if(getUnemployed()>0) {
            gatherers++;
            workingCount++;
            populationPanel.update();
        }
    }
    public void addResearcher() {

        if(getUnemployed()>0) {
            researcher++;
            workingCount++;
            populationPanel.update();
        }
    }
    public void removeResearcher() {
        if(researcher>0) {
            researcher--;
            workingCount--;
            populationPanel.update();
        }
    }
    public void removeGatherer() {
        if(gatherers>0) {
            gatherers--;
            workingCount--;
            populationPanel.update();
        }
    }
    public int getWorkingCount() {
        return this.workingCount;
    }
    public int getUnemployed() {
        return this.POPULATION - this.workingCount;
    }
    public int getResearchers() {
        return this.researcher;
    }
    public int getGatherers() {
        return this.gatherers;
    }
    public int getPopulation() {
        return POPULATION;
    }
    public int getFood() {
        return FOOD;
    }
    public int getResearchLevel() {
        return RESEARCH_LEVEL;
    }
    public long getTotalTime() {
        return time;
    }
    public int getDay() {
        return day;
    }
    public String getUserTime() {
        return userTime;
    }
    public PriorityQueue<Village> getVillages() {
        return villages;
    }
    public BuildingManager getBuildingManager() {
        return this.buildingManager;
    }
    public DefaultGameMenu getGameMenu() {
        return this.defaultGameMenu;
    }
    
    public static String getAsString(int number) {
        //returns the number as a string with a 1,000,000,000,000 format (it has commas)
        String s = String.valueOf(number);
        //adds commas every three characters starting from the back
        if(s.length()>3) {  //only do this for numbers that are 4+ characters long
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
    private void updateTimers() {
        updateCottageTimers();
        updateFarmerTimers();
        updateTanneryTimers();
        updateMineTimers();

    }  

    private void updateMineTimers() {
        Stack<Integer> temp = new Stack<Integer>();

        while(!mineTimers.isEmpty()) {
            Integer v = mineTimers.pop();
            v--;
            if(v>0)
            temp.push(v);
            else {
                workingCount-=mineInfo[3];
                mineInfo[1] --;
                mineInfo[0] ++;

                buildingManager.update();
                populationPanel.update();
            }
        }
    }

    private void updateTanneryTimers() {
        Stack<Integer> temp = new Stack<Integer>();

        while(!tanTimers.isEmpty()) {
            Integer v = tanTimers.pop();
            v--;
            if(v>0)
            temp.push(v);
            else {
                workingCount -= tanneryInfo[3];
                tanneryInfo[1]--;
                tanneryInfo[0]++;

                buildingManager.update();
                populationPanel.update();
            }
        }
        while(!temp.isEmpty()) {
            tanTimers.push(temp.pop());
        }
    }
    private void updateFarmerTimers() {
        //updates the cottage timers by removing each element, subtracting one, and pushing it into a different stack if it is not zero
        // --> if it is zero, it builds a cottage by updating the backend information regarding cottages.

        // It does this until the Stack is empty. Once the cottage Stack is empty, it flushes the temporary stack by removing every element and pushing it into the original cottage Stack.

        Stack<Integer> temp = new Stack<Integer>();
        //pop each value, subtract one from it, and if it is larger than zero, push it onto a different stack
        while(!farmTimers.isEmpty()) {
            Integer v = farmTimers.pop();
            v--;
            if(v>0)
             temp.push(v);
            else {
                //finished building a Farm (timer is 0)
                workingCount -= farmInfo[3];         //remove number of workers working on a farm
                farmInfo[1]--;                           //remove number of farms in progress
                farmInfo[0]++;                           //increase the number of built farms

                    
                buildingManager.update();       //update the building manager
                populationPanel.update();
             }
        }
        //pop every element on the temp stack and push it into the cottageTimer stack
        while(!temp.isEmpty()) {
            farmTimers.push(temp.pop());
        }

    }
    private void updateCottageTimers() {
        //updates the cottage timers by removing each element, subtracting one, and pushing it into a different stack if it is not zero
        // --> if it is zero, it builds a cottage by updating the backend information regarding cottages.

        // It does this until the Stack is empty. Once the cottage Stack is empty, it flushes the temporary stack by removing every element and pushing it into the original cottage Stack.

        Stack<Integer> temp = new Stack<Integer>();
        //pop each value, subtract one from it, and if it is larger than zero, push it onto a different stack
        while(!cottageTimers.isEmpty()) {
            Integer v = cottageTimers.pop();
            //System.out.println("updating " + v);
            v--;
            if(v>0)
             temp.push(v);
            else {
                //finished building a cottage (timer is 0)
                workingCount -= cottageInfo[3];         //build the cottage by performing these actions
                cottageInfo[2] -= cottageInfo[3];           //cottageWorkCount -= cottageBuildNumber;
                cottageInfo[1]--;                           //cottagesInProgress--;
                cottageInfo[0]++;                           //cottages++;

                    
                buildingManager.update();       //update the building manager
                populationPanel.update();

             }
        }
        //pop every element on the temp stack and push it into the cottageTimer stack
        while(!temp.isEmpty()) {
            cottageTimers.push(temp.pop());
        }

    }
    private void play() {
        
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            //increment time
            incrementTime();
            updateTimers();
            farmFood();             //increase food by amount of farms in operation. Note, you only need to have them in operation at midnight to get food! I am NOT (at this time) implememnting something that will keep track of how often each one is in operation or anything of that nature.
            defaultGameMenu.updateTime();


          }
        }, 0, 1000);//wait 0 ms before doing the action and do it evry 1000ms (1second)

        //ever 5s, chance to increase food and population
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            //increment time
            //consumeFood();
            increasePopulation();
            increaseFood();

          }
        }, 0, 5000);//wait 0 ms before doing the action and do it evry 5000ms (5second)
        
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
              consumeFood();
  
            }
          }, 0, 10000);//wait 0 ms before doing the action and do it evry 5000ms (5second)
    }

    private void increaseFood() {
        //has a probability of increasing food count based on number of food gatherers
        if(gatherers*Math.random()>5) {
            FOOD++;
            defaultGameMenu.updateFood();
            defaultGameMenu.foodPopPlus();
        }
    }
    private void increasePopulation() {
        //has a probability of increasing population based on population size and cottages built
        if(cottageInfo[0]*Math.random() > 9.5) {
            POPULATION++;
            defaultGameMenu.updatePopulation();
            defaultGameMenu.displayPlusOnePop();
            populationPanel.update();                           //update population panel when population increases. The user may be looking at population panel when pop goes up
        }
    }
    private void consumeFood() {
        //has a probability of conuming food based on the current population count
        if(POPULATION*Math.random() > 9.9) {
            FOOD--;
            defaultGameMenu.updateFood();
            defaultGameMenu.foodPopMinus();
        }
    }
    private void incrementTime() {
        //every second, one minute passes in the game
        time++;     //time increases 1 second per second. this is used to show total time played
        userTime = addOne();
    }

    private String addOne() {
        //adds one minute to the userTime
        //displayed as HH:MM
        
        //if MM is less than 59, we add one to MM
        //otherwise, add one to HH and set MM to 00
        
        //if HH is 24, increment day and set HH to 00

        int HH = Integer.parseInt(userTime.substring(0, 2));
        int MM = Integer.parseInt(userTime.substring(3, userTime.length()));

        if(MM < 59) {
            MM++;
        }
        else {
            HH++;
            MM=0;
        }

        if(HH==24) {
            day++;
            HH=0;
        }

        //change the int back to String. If single digit, append a 0 as prefix
        String hour = "";
        String minute = "";

        if(HH<10)
        hour += "0";
        if(MM<10)
        minute += "0";

        hour += String.valueOf(HH);
        minute += String.valueOf(MM);

        return hour + ":" + minute;

    }



    private void loadGame() {
        //loads the game from the filename.
        //if empty or corrupt (non-readable), then create a new game
        
        try {

            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            
            //get data from the file and setUp the internal properties
            POPULATION = Integer.parseInt(scanner.nextLine());
            FOOD = Integer.parseInt(scanner.nextLine());
            RESEARCH_LEVEL = Integer.parseInt(scanner.nextLine());
            time = Long.parseLong(scanner.nextLine());
            day = Integer.parseInt(scanner.nextLine());
            userTime = scanner.nextLine();

            workingCount = Integer.parseInt(scanner.nextLine());
            researcher = Integer.parseInt(scanner.nextLine());
            gatherers = Integer.parseInt(scanner.nextLine());

            cottageInfo[0] = Integer.parseInt(scanner.nextLine());  //number of cottages
            cottageInfo[1] = Integer.parseInt(scanner.nextLine());  //number of cottages in progress
            cottageInfo[2] = Integer.parseInt(scanner.nextLine());  //number of people working on cottages
            cottageInfo[3] = Integer.parseInt(scanner.nextLine());  //number of people it takes to build one cottage
            
            farmInfo[0] = Integer.parseInt(scanner.nextLine());     //number of farms
            farmInfo[1] = Integer.parseInt(scanner.nextLine());     //number of farms in progress
            farmInfo[2] = Integer.parseInt(scanner.nextLine());     //number of people working at farms
            farmInfo[3] = Integer.parseInt(scanner.nextLine());     //number of people it takes to build one farm

            tanneryInfo[0] = Integer.parseInt(scanner.nextLine());     //number of Tanneries
            tanneryInfo[1] = Integer.parseInt(scanner.nextLine());     //number tanneries in prog
            tanneryInfo[2] = Integer.parseInt(scanner.nextLine());     //number of people working at tanneries
            tanneryInfo[3] = Integer.parseInt(scanner.nextLine());     //number of people it takes to build a tannery

            mineInfo[0] = Integer.parseInt(scanner.nextLine());     //number of mines
            mineInfo[1] = Integer.parseInt(scanner.nextLine());     //number of mines in progress
            mineInfo[2] = Integer.parseInt(scanner.nextLine());     //number of people working at mines
            mineInfo[3] = Integer.parseInt(scanner.nextLine());     //number of people it takes to build a mine


            loadCottageTimers(scanner);
            loadFarmerTimers(scanner);
            loadTanneryTimers(scanner);
            loadMineTimers(scanner);


            while(scanner.hasNextLine()) { //read the villages
                String s = scanner.nextLine();
                //name:name population:population researchLevel:researchLevel difficulty:difficulty format

                String name = "";
                int population = -1;
                int researchLevel = -1;
                int difficulty = -1;

                String current = "";

                
                for(int i =0; i<s.length(); i++) {

                    if(s.charAt(i) == ':') { //if we have ':' character, then we are defining the current property
                        i++;  //move away from ':' character
                        if(current.equals("name")) {
                            //read off the characters until a space
                            current = ""; //reset current
                            while(s.charAt(i) != ' ') {
                                name += s.charAt(i); //get the name
                                i++;
                            }
                        }
                        if(current.equals("population")) {
                            //reset current
                            //read off the characters until a space
                            String popString = "";
                            current = "";
                            while(s.charAt(i) != ' ') {
                                popString += s.charAt(i);   //read off the number as a string
                                i++;
                            }
                            population = Integer.parseInt(popString); //get the population string as an integer
                        }
                        if(current.equals("researchLevel")) {
                            String researchString = "";
                            current = "";
                            while(s.charAt(i) != ' ') {
                                researchString += s.charAt(i);
                                i++;
                            }
                            researchLevel = Integer.parseInt(researchString);
                        }
                        if(current.equals("difficulty")) {
                            
                            String difficultyString = "";
                            current = "";
                            while(i<s.length()) {
                                difficultyString += s.charAt(i);
                                i++;
                            }
                            difficulty = Integer.parseInt(difficultyString);
                            break;
                        }

                    }
                    else
                    current += s.charAt(i);
                }
                    if(population==-1)  //quick fix to not read an empty textline
                     break;
                    Village v = new Village(name, population, researchLevel, difficulty);
                    villages.add(v);
            
            }

            scanner.close();


        }
        catch(Exception e) {
            //Exception caught: either file has no pathname, or some of the information is corrupted
            //setUp new game

            System.out.println("EXCEPTION IN READING " + filename);
            POPULATION = 10;
            FOOD = 50;
            RESEARCH_LEVEL = 0;
            time = 0; day = 0; userTime = "00:00";

            workingCount = 0; researcher = 0; gatherers = 0;

            //number of buildings,  number in progress, number of workers,       workers to build one building 
            cottageInfo[0] = 0;     cottageInfo[1] = 0; cottageInfo[2] = 0;     cottageInfo[3] = 5;
            farmInfo[0] = 0;        farmInfo[1] = 0;    farmInfo[2] = 0;        farmInfo[3] = 5;
            tanneryInfo[0] =0;      tanneryInfo[1] =0;  tanneryInfo[2] =0;      tanneryInfo[3] = 10;
            mineInfo[0] = 0;        mineInfo[1] = 0;    mineInfo[2] = 0;        mineInfo[3] = 20;

            //make 20 new villages (this number can change)
            for(int i =0; i<20; i++) {
                villages.add(new Village( nameGenerator(), i%5));
            }
        }
    }
    
    private String nameGenerator() {
        //generates a random village name to assign to a village
        //to be changed at a later date
        int i = (int) (6*Math.random());
        switch(i) {
            case 0 : return "Village0";
            case 1 : return "Village1";
            case 2 : return "Village2";
            case 3 : return "Village3";
            case 4 : return "Village4";
            case 5 : return "Village5";
        }
        return "VillageGeneral";

    }
    private void loadCottageTimers(Scanner scanner) {
        //loads this instnace of cottageTimers from the filename
        
        int numTimers = Integer.parseInt(scanner.nextLine());   //number of timers to add
        for(int i =0; i<numTimers; i++) {
            cottageTimers.push(Integer.parseInt(scanner.nextLine()));
        }
    }
    private void loadFarmerTimers(Scanner scanner) {
        //loads this instance of farmerTimers
        int numTimers = Integer.parseInt(scanner.nextLine());
        for(int i =0; i<numTimers; i++) {
            farmTimers.push(Integer.parseInt(scanner.nextLine()));
        }
    }

    private void loadTanneryTimers(Scanner scanner) {
        //loads this instance of tannery timers
        int numTimers = Integer.parseInt(scanner.nextLine());     //number of timers
        for(int i =0; i<numTimers; i++) {
            tanTimers.push(Integer.parseInt(scanner.nextLine()));
        }
    }

    private void loadMineTimers(Scanner scanner) {
        //loads this instance of mine timers
        int numTimers = Integer.parseInt(scanner.nextLine());
        for(int i =0; i<numTimers; i++) {
            mineTimers.push(Integer.parseInt(scanner.nextLine()));
        }
    }

    private void saveFarmTimers() {
        //saves this instance of farmTimers to the filename internally stored

        System.out.println(farmInfo[1]);    //how many farms in progress

        Stack<Integer> temp = new Stack<Integer>();

        while(!farmTimers.isEmpty()) {
            Integer v = farmTimers.pop();
            System.out.println(v);
            temp.push(v);
        }
        while(!temp.isEmpty())
         farmTimers.push(temp.pop());
    }
    private void saveTanTimers() {
        System.out.println(tanneryInfo[1]);

        Stack<Integer> temp = new Stack<Integer>();
        while(!tanTimers.isEmpty()) {
            Integer v = tanTimers.pop();
            System.out.println(v);
            temp.push(v);
        }
        while(!temp.isEmpty())
            tanTimers.push(temp.pop());
    }
    private void saveMineTimers() {
        System.out.println(mineInfo[1]);
        Stack<Integer> temp = new Stack<Integer>();
        while(!mineTimers.isEmpty()) {
            Integer v = mineTimers.pop();
            System.out.println(v);
            temp.push(v);
        }
        while(!temp.isEmpty()) {
            mineTimers.push(temp.pop());
        }
    }
    private void saveCottageTimers() {
        //saves this instance of cottageTimers to the filename
        
        //for each value in cottage timers, println(value)
        System.out.println(cottageInfo[1]); //we need to know how many lines we will read from when loading
        Stack<Integer> temp = new Stack<Integer>();
        
        while(!cottageTimers.isEmpty()) {
            Integer v = cottageTimers.pop();    //get the element
            System.out.println(v);              //print it out
            temp.push(v);                       //push it into temp stack
        }
        //get all the temps back into the cottageTimer
        while(!temp.isEmpty()) {
            cottageTimers.push(temp.pop());
        }
        /*
        this implementation causes a very interesiting bug : it will output hundreds of thousands (300k+ normally) of lines of vector memory locations when saving while building a cottage
        Iterator<Integer> i = cottageTimers.iterator();
        while(i.hasNext()) {
            System.out.println(i);
        }
        */
    }
    public void saveGame() {
        try {

            System.setOut(new PrintStream(new File(filename)));
            System.out.println(POPULATION);
            System.out.println(FOOD);
            System.out.println(RESEARCH_LEVEL);
            System.out.println(time);
            System.out.println(day);
            System.out.println(userTime);

            System.out.println(workingCount);
            System.out.println(researcher);
            System.out.println(gatherers);

            System.out.println(cottageInfo[0]);     //number of cottages built
            System.out.println(cottageInfo[1]);     //number of cottages in progress
            System.out.println(cottageInfo[2]);     //number of people working on cottages
            System.out.println(cottageInfo[3]);     //number of people it takes to build one cottage

            System.out.println(farmInfo[0]);        //number of farms
            System.out.println(farmInfo[1]);        //number of farms in progress
            System.out.println(farmInfo[2]);        //number of people working at farms
            System.out.println(farmInfo[3]);        //number of people it takes to build a farm
            
            System.out.println(tanneryInfo[0]);
            System.out.println(tanneryInfo[1]);
            System.out.println(tanneryInfo[2]);
            System.out.println(tanneryInfo[3]);

            System.out.println(mineInfo[0]);
            System.out.println(mineInfo[1]);
            System.out.println(mineInfo[2]);
            System.out.println(mineInfo[3]);


            //print out the timers of cottages
            saveCottageTimers();
            saveFarmTimers();
            saveTanTimers();
            saveMineTimers();

            Iterator<Village> iterator = villages.iterator();
            //print out all the villages
            while(iterator.hasNext()) {
                Village v = iterator.next();
                if(v!=null) {
                    //StdOut.println(v.toString());
                    System.out.println(v.toString());
                }
             //System.out.println(iterator.next().toString());
            }
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out))); //set System.out back to terminal
            //System.out.println("hello world! I still work! You just saved the game. Goodbye now.");

        }
        catch(Exception e) {
            //unsucessful save
            System.out.println("Exception in saving " +filename);
        }
    }

}
package village;


/*
 * 
 * The village object that is used for each attacking village.
 * 
 * Every village has its own population and resource levels depending on its difficulty
 * 
 */
public class Village implements Comparable<Village>{

    private String name;            //the name of the village
    private int population;
    private int reserach_level;         //determines the quality of weaponry, and therefore the effectiveness of the population in combat
    private int difficulty; //0, 1, 2, 3.. etc depending on its size



    public Village(String name, int difficulty) {

        //Creates an instance of the village with the given name and difficulty
        //Used for creating a new game/village

        //The difficulty will determine the population size and its resource levels.

        //--> this allows randomized villages to be created with each playthrough!

        //one quirk: the names might need to be randomized
        this.name = name;
        setInternals();
    }

    public Village(String name, int population, int reserach_level, int difficulty) {
        //creates an instance of a village with the exact paramters. Used for loading the game.
        this.name = name;
        this.population = population;
        this.reserach_level = reserach_level;
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }
    public int getPopulation() {
        return population;
    }
    public int getDifficulty() {
        return difficulty;
    }

    public String toString() {
        //returns the village as a string that will be used to save, load from, and display to user
        return "name:" + name + " population:" + population + " researchLevel:" + reserach_level + " difficulty:" + difficulty;
    }

    public int compareTo(Village other) {
        return this.difficulty - other.difficulty; //compares their difficulties
    }
    private void setInternals() {
        //sets up the population and resources depending on the difficulty
        switch(difficulty) {
            case 0:  easy(); return;//easy mode
            case 1: medium(); return;
        }

        lazyMode();

    }
    private void lazyMode() {
        //too lazy to code higher difficulties at time being. let's just get this thing to work
        population = 100;
        reserach_level = 10;
        difficulty = 9;
    }
    private void easy() {
        //sets the internal village to be easy
        //--> low population, low research level

        //population ranges from 5-20
        //research level ranges from 0-1

        population = (int) (21*Math.random());  //0  to 20
        if(population < 5)
         population = 5;
        reserach_level = (int)(2*Math.random()); //0 to 1

    }

    private void medium() {
        //population ranges from 15-100 inclusive
        population = (int) (101*Math.random());     //max 100
        if(population < 15) //min 15
            population = 15;
        
        //research ranges from 1-5 inclusive
        reserach_level = (int)(6*Math.random());    //max 5
        if(reserach_level < 1)
            reserach_level = 1;                     //low 1
    }

    
}
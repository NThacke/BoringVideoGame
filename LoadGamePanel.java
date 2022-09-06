package village;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.*;


public class LoadGamePanel {

    private PanelManager client; //GUI client
    private JPanel panel;   //the actual JPanel that is used 

    private JLabel topLabel;        //tell client that this is load game page
    private JLabel currentSelection;    //tell client their current file selection
    private LinkedList<JButton> selectors;       //have a linkedList of JButtons. The buttons will be selectable to choose the file to load from.
    
    private JLabel splitter; //split up the files saved and the buttons
    private JButton load;                   //load the currently selected file
    private JButton delete;
    private JLabel splitter2;
    private JButton back;               //go back to previous (startUp) panel


    public LoadGamePanel(PanelManager client) {

        //set all internal properties
        this.client = client;
        this.panel = new JPanel();
        this.topLabel = new JLabel("Load Game");
        this.currentSelection = new JLabel("Select a file");
        this.selectors = new LinkedList<JButton>();
        this.splitter = new JLabel();
        this.load = new JButton("Load");
        this.delete = new JButton("Delete");
        this.splitter2 = new JLabel();
        this.back = new JButton("Back");

        //get the files that have previously been saved
        //this also creates JButtons for each of them
        getFiles();
        //make the buttons functional
        makeButtonsFunctional();


        //initalize everything
        init();
    }
    public void setSelectorText(String s) {
        currentSelection.setText(s);
    }
    public JPanel getPanel() {
        return this.panel;
    }
    private void init() {
        //border and layout
        panel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
        panel.setLayout(new GridLayout(0,1));
        
        //add the components
        panel.add(topLabel);
        panel.add(currentSelection);

        //JButtons to select a file. this is dynamically sized
        while(!selectors.isEmpty()) {
            panel.add(selectors.removeFirst());
        }
        panel.add(splitter);
        panel.add(load);
        panel.add(delete);
        panel.add(splitter2);
        panel.add(back);
    }
    private void loadButtonFunctional() {
        //loads the game instance with the currently selected filename
        load.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new Game(client, currentSelection.getText().substring(10));   //new Game
                }
            }
        );
    }
    private void backButtonFunctional() {
        back.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //go back to the startUpPanel
                    client.setCurrentPanel(client.getStartUp());
                }
            }
        );
    }
    private void deleteButtonFunctional() {
        //deletes the currently selected filename
        delete.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                     //delete the currently selected file
                     String filename = currentSelection.getText().substring(10);
                     File file = new File(filename);
                     if(file.delete()){
                        client.setCurrentPanel(new LoadGamePanel(client));  //reload the loadGamePanel
                    }
                }
            }
        );
    }
    private void makeButtonsFunctional() {
        //makes the buttons that select to a file functional
        //each of these buttons are stored in linkedList
        Stack<JButton> buttonStack = new Stack<JButton>();//use this to take from linked list and to put pack later

        while(!selectors.isEmpty()) {
            JButton button = selectors.removeFirst();
            button.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        //sets the currentSelection label to show the filename in the JButton
                        currentSelection.setText("Selected: " + button.getText());
                    }
                }
            );
            buttonStack.push(button);
        }

        //pop the buttons and put them back on the selector linked list
        while(!buttonStack.isEmpty())
            selectors.add(buttonStack.pop());

        //back, delete, and load buttons
        backButtonFunctional();
        deleteButtonFunctional();
        loadButtonFunctional();
    }
    private void getFiles() {
        //gets the files that are .txt and puts their names into the selector list

        //google "how to get files from specific directory"
        //https://stackabuse.com/java-list-files-in-a-directory/
        File f = new File(System.getProperty("user.dir"));
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return name.endsWith(".txt");
            }
        };
        String[] textFiles = f.list(filter);

        //for each of those files, make a new button for it
        for(int i =0; i<textFiles.length; i++) {
            selectors.add(new JButton(textFiles[i]));
        }

    }


}
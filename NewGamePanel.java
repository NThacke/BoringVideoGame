package village;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.*;


public class NewGamePanel {

    private PanelManager client;
    private JPanel panel;
    private JLabel topLabel;
    private JTextField textField;
    private JButton createButton;
    private JLabel message;
    private JButton backButton;

    private LinkedList<String> files; //the files that are currently saved

    public NewGamePanel(PanelManager client) {

        this.client = client;
        this.panel = new JPanel();
        this.topLabel = new JLabel("New Game");
        this.textField = new JTextField("filename");
        this.createButton = new JButton("Create Game");
        this.message = new JLabel();
        this.backButton = new JButton("Back");

        this.files = new LinkedList<String>();

        init();

    }

    public void changeMessage(String s) {
        message.setText(s);
    }
    private void init() {

        //make textfield, create button, and back button functional
        textFieldFunctional();
        createButtonFunctional();
        backButtonFunctional();

        //border and layout
        panel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
        panel.setLayout(new GridLayout(0,1));

        //add every component
        panel.add(topLabel);
        panel.add(textField);
        panel.add(createButton);
        panel.add(message);
        panel.add(backButton);
    }

    public JPanel getPanel() {
        return this.panel;
    }

    private void textFieldFunctional() {
        //makes the textField functional
        textField.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        createGame(textField.getText());
                    }
                    catch(Exception ef) {
                        changeMessage("File already taken");
                    }
                }
            }
        );
    }
    
    private void createButtonFunctional() {
        //makes the createButton functional
        createButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //get the filename from textfield and attempt to create a game from it
                    String filename = textField.getText();
                    try {
                        createGame(filename);
                    }
                    catch(Exception ef) {
                        //filename already taken
                        changeMessage("File already taken");
                    }
                }
            }
        );
    }

    private void backButtonFunctional() {
        //makes the backButton functional
        backButton.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //go back to the startUpPanel
                    client.setCurrentPanel(client.getStartUp());
                }
            }
        );
    }

    private void createGame(String filename) {

        //attempts to create a game with the given filename
        //exceptions would be when the given filename is already a file
        //--> in that case, do nothing. Perhaps implement a request to override the file at a later date

        getFiles(); //get the files currently saved
        
        filename += ".txt";

        if(files.contains(filename))
        throw new IllegalArgumentException();   //you can't create a file that's already been made


        //go to the actual game with the given filename as the save file
        System.out.println("Creating new game");
        new Game(client, filename);


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

        //for each file, add it to the internal files
        for(int i =0; i<textFiles.length; i++) {
            files.add(textFiles[i]);
        }

    }
    
}
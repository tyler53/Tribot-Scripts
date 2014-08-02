package scripts;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import org.tribot.api.Timing;
import org.tribot.script.interfaces.MouseActions;
import org.tribot.script.interfaces.Painting;

/**
 * Created by Tyler53 on 6/21/2014.
 * Traveler takes the player to the desired place, or finds the nearest bank and then walks there.
 */

@ScriptManifest(authors = {"Tyler53"}, name = "Traveler", category = "Traveling")
public class Traveler extends Script implements Painting, MouseActions, ActionListener{


    private static RSTile AL_KHARID_TILE = new RSTile(3301, 3203, 0);
    private static RSTile DUEL_ARENA_TILE = new RSTile(3367, 3268, 0);
    private static RSTile CATHERBY_TILE = new RSTile(2757, 3478);
    private static RSTile EAST_ARADOUGE_TILE = new RSTile(2662, 3305, 0);
    private static RSTile WEST_ARADOUGE_TILE = new RSTile(2529, 3307, 0);
    private static RSTile BARBARIAN_VILLAGE_TILE = new RSTile(3082, 3420, 0);
    private static RSTile DRAYNOR_VILLAGE_TILE = new RSTile(3093, 3224, 0);
    private static RSTile EDGEVILLE_TILE = new RSTile(3093, 3493, 0);
    private static RSTile FALADOR_TILE = new RSTile(2964, 3378);
    private static RSTile LUMBRIDGE_TILE = new RSTile(3225, 3219, 0);
    private static RSTile TREE_GNOME_STRONGHOLD_TILE = new RSTile(2461, 3443, 0);
    private static RSTile TREE_GNOME_VILLAGE_TILE = new RSTile(2525, 3167, 0);
    private static RSTile VARROCK_TILE = new RSTile(3212, 3424, 0);

    private RSTile TARGET_TILE;

    public boolean shouldFindBank = false;
    private boolean isFinished = false;
    public boolean shouldShowPaint = true;
    public boolean toggleBeingPressed = false;

    public String nameOfDestination;

    public boolean shouldBegin = false;

    JButton startButton;
    JButton bankButton;
    JFrame settingsFrame;
    JComboBox optionList;

    String[] options = {
            "Al Kharid","Al Kharid Duel Arena", "Catherby", "East Aradouge",
            "West Aradouge", "Barbarian Village","Draynor Village",
            "Edgeville", "Falador", "Lumbridge",
            "Tree Gnome Stronghold", "Tree Gnome Village", "Varrock"};
    RSTile[] destinationTiles = {AL_KHARID_TILE, DUEL_ARENA_TILE,CATHERBY_TILE, EAST_ARADOUGE_TILE,
            WEST_ARADOUGE_TILE, BARBARIAN_VILLAGE_TILE,DRAYNOR_VILLAGE_TILE,
            EDGEVILLE_TILE, FALADOR_TILE, LUMBRIDGE_TILE, TREE_GNOME_STRONGHOLD_TILE,
            TREE_GNOME_VILLAGE_TILE, VARROCK_TILE};

    @Override
    public void run() {
        sleep(100);

        showGUI();

        while (!shouldBegin){System.out.println("Should not begin yet");}

        System.out.println("Beginning Script");
        startScript();

        while (loop()){General.sleep(200);}
        while(finishLoop()){General.sleep(200);}
    }

    public boolean showingGUI = false;
    public void showGUI(){

        settingsFrame = new JFrame("Traveler");
        settingsFrame.setSize(400, 200);
        settingsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        JPanel mainPanel = new JPanel();

        optionList = new JComboBox(options);
        optionList.setSelectedIndex(0);
        optionList.addActionListener(this);

        mainPanel.add(new JLabel("Welcome to Traveler!"), BorderLayout.CENTER);
        mainPanel.add(new JLabel("Please select your destination:"), BorderLayout.CENTER);
        mainPanel.add(optionList);

        startButton = new JButton("Start Traveling");
        startButton.addActionListener(this);
        mainPanel.add(startButton);

        mainPanel.add(new JLabel("Or, click the button below to find the nearest bank:"), BorderLayout.CENTER);
        bankButton = new JButton("Find Nearest Bank");
        bankButton.addActionListener(this);
        mainPanel.add(bankButton);
        settingsFrame.add(mainPanel);
        settingsFrame.setVisible(true);
        showingGUI = true;


    }

    public void startScript(){

        System.out.println("Starting Script");

        Mouse.setSpeed(170);
        shouldShowPaint = true;
        toggleBeingPressed = false;
    }

    private boolean finishLoop(){
        isFinished = true;
        return true;
    }
    private boolean loop(){
        runToLocation();

        if(!shouldFindBank){
            if(!Player.getPosition().equals(TARGET_TILE)){
                return true;
            }
        }

        return false;
    }

    private void runToLocation(){
        if (shouldFindBank){
            if(WebWalking.walkToBank()){
                while(Player.isMoving()){
                    sleep(200);
                }
            }
        }else{
            WebWalking.walkTo(TARGET_TILE);
        }
    }

    private static final long startTime = System.currentTimeMillis();
    private long timeRunSoFar;

    private static final Rectangle Toggle_Paint = new Rectangle(389, 414, 101, 41);

    public void onPaint(Graphics g) {

        if (shouldShowPaint) {

            g.setColor(Color.BLACK);
            g.drawRoundRect(389, 414, 101, 41, 11, 11);
            g.setColor(Color.WHITE);
            g.fillRoundRect(390, 415, 100, 40, 10, 10);

            g.setColor(Color.RED);
            g.setFont(new Font("Verdana", Font.PLAIN, 15));
            g.drawString("Hide Graphics", 390, 440);

            g.setFont(new Font("Verdana", Font.BOLD, 14));

            if (!isFinished) {
                timeRunSoFar = System.currentTimeMillis() - startTime;
                g.setFont(new Font("Verdana", Font.BOLD, 14));
                g.setColor(Color.CYAN);
                g.drawString("Traveling to: " + nameOfDestination , 210, 315);
                g.setColor(Color.GREEN);
                g.drawString("You have been traveling for: " + Timing.msToString(timeRunSoFar), 210, 335);
            } else {
                g.setFont(new Font("Verdana", Font.BOLD, 25));

                g.setColor(Color.RED);
                g.fillRoundRect(20, 70, 485, 210, 105, 105);
                g.setColor(Color.DARK_GRAY);
                g.fillRoundRect(25, 75, 475, 200, 105, 105);

                g.setColor(Color.GREEN);
                g.drawRoundRect(25, 75, 475, 200, 105, 105);
                g.drawRoundRect(25, 75, 475, 200, 105, 105);
                g.drawRoundRect(25, 75, 475, 200, 105, 105);


                g.setColor(Color.CYAN);
                g.drawString("You arrived in " + Timing.msToString(timeRunSoFar), 100, 125);
                g.drawString("Thank you for using \"Traveler\"", 50, 150);
                g.drawString("By Tyler53", 195, 175);
                g.setColor(Color.GREEN);
                g.setFont(new Font("Verdana", Font.ITALIC, 20));
                g.drawString("Don't forget to vouch this bot on forums!", 45, 225);
            }
        }else{
            g.setColor(Color.BLUE);
            g.drawRoundRect(389, 414, 101, 41, 11, 11);
            g.setColor(Color.GREEN);
            g.fillRoundRect(390, 415, 100, 40, 10, 10);

            g.setColor(Color.BLACK);
            g.setFont(new Font("Verdana", Font.PLAIN, 13));
            g.drawString("Show Graphics", 390, 440);
        }
    }

    @Override
    public void mouseClicked(Point point, int i, boolean b) {
        if (Toggle_Paint.contains(point.getLocation())){
            if (shouldShowPaint){
                shouldShowPaint = false;
            }else{
                shouldShowPaint = true;
            }
        }
    }

    @Override
    public void mouseMoved(Point point, boolean b) {}

    @Override
    public void mouseReleased(Point point, int i, boolean b) {}

    @Override
    public void mouseDragged(Point point, int i, boolean b) {}

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == startButton){
            nameOfDestination = options[optionList.getSelectedIndex()];
            TARGET_TILE = destinationTiles[optionList.getSelectedIndex()];
            System.out.println("Traveling to: " + nameOfDestination);
            settingsFrame.setVisible(false);
            shouldFindBank = false;
            shouldBegin = true;
        }else if(e.getSource() == bankButton){
            nameOfDestination = "Nearest Bank";
            System.out.println("Finding Nearest Bank");
            settingsFrame.setVisible(false);
            shouldFindBank = true;
            shouldBegin = true;
        }

    }
}

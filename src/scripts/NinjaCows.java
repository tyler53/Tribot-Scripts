package scripts;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.MouseActions;
import org.tribot.script.interfaces.Painting;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

/**
 * Created by Tyler53 on 8/2/2014.
 * Ninja Cows kills cows in the Lumbridge Cow Pen.
 * THIS SCRIPT IS A WORK IN PROGRESS ~~~ IT IS NOT COMPLETED YET
 */

@ScriptManifest(authors = {"Tyler53"}, name = "Ninja Cows", category = "Combat")
public class NinjaCows extends Script implements ActionListener, Painting, MouseActions {

    //public boolean isRunning = false;
    public String forumURL = "http://www.tribot.org/forums/";
    private static int[] COW_ID = {522, 523, 524, 525};

    //GUI
    JFrame settingsFrame;
    JComboBox foodOptions;
    JTextField foodAmountField;
    JButton toggleChat;
    JButton startButton;

    //Settings
    public boolean hasFinishedSettings = false;
    public boolean isUsingFood = false;
    public int healthToEat = 10;
    public int foodID = 379;
    public int foodAMT = 10;
    public String foodName = "No Food";
    public boolean shouldUseChat = true;
    private static final String[] FOOD_OPTIONS = {"No Food", "Shrimp", "Cooked Chicken", "Cooked Meat", "Bread", "Herring",
            "Mackerel", "Trout", "Salmon", "Tuna", "Lobster", "Swordfish", "Monkfish", "Shark", "Manta Ray"};
    private static final int[] FOOD_IDS = {-1, 315, 2140, 2142, 2309, 347, 355, 333, 329, 361, 379, 373, 7946, 385, 391};

    //Paint
    public boolean shouldShowPaint = true;

    //Operational
    public boolean isBanking = false;
    public String statusString = "Null";

    //RSTiles
    public static RSTile COW_TILE_TOP = new RSTile(3253, 3291);
    public static RSTile COW_TILE_MID = new RSTile(3257, 3281);
    public static RSTile COW_TILE_BOTTOM = new RSTile(3259, 3261);
    public final RSTile[] COW_TILES = {COW_TILE_TOP, COW_TILE_MID, COW_TILE_BOTTOM};
    private final Polygon cowArea = new Polygon(new int[]{3242, 3242, 3246, 3253, 3253, 3265, 3265},
            new int[]{3296, 3283, 3278, 3272, 3255, 3255, 3296}, 7);

    //Misc.
    private Random randomGenerator;
    public static String[] botWorldStrings = {"What the heck?",  "did I just switch worlds?", "What just happened?",
            "Why am I in a different world", "um?", "I'm not supposed to be here lol",
            "that's odd...", "what the.."};


    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ** END VARIABLES ** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ** BEGIN ACTUAL SCRIPT ** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //

    @Override
    public void run() {

        randomGenerator = new Random();

        /*if (!isRunning)
            isRunning = true;*/

        statusString = "Waiting For Setup";
        startSettings();

        while (!hasFinishedSettings){System.out.println("Waiting for Settings.");sleep(1000);}
        while (loop()){General.sleep(200);}

        /*while(isRunning){
            while(!hasFinishedSettings){System.out.println("Waiting for settings");sleep(1000);}
            if (hasFinishedSettings) {
                loop();
                General.sleep(100);
            }
        }*/

    }

    private boolean loop(){
        if (Login.getLoginState() == Login.STATE.INGAME){

            handleBotWorlds();

            if (isUsingFood) {
                if (Player.getRSPlayer().getHealth() <= healthToEat) {
                    statusString = "Eating Food";
                    General.println("Low Health, eating some food");
                    return eatFood(foodID);
                }
            }

            if (!isBanking){
                if (!isInCowPen(Player.getRSPlayer().getPosition())){
                    statusString = "Walking to Cows";
                    General.println("We are not yet in the Cow Pen, going there now.");
                    moveToCowPen();
                }

                if (!Player.getRSPlayer().isInCombat()){
                    statusString = "Finding Next Cow";
                    killCows();
                }else{
                    statusString = "Fighting Cow";
                    //~~~~~~~~~~~~~~~~~ RUN ANTI-BAN COMPLIANCE i.e CHAT ETC. ~~~~~~~~~~~~~~~~~//
                }
            }
        }else{
            Login.login();
        }
        return true;
    }

    /**
     * Walk to cow pen.  If at cow pen, set isInCowPen to true so that we can continue,
     * if not, walk to the pen until we get there.
     */
    private void moveToCowPen() {
        WebWalking.walkTo(COW_TILES[randomGenerator.nextInt(COW_TILES.length)]);
        sleep(1000, 2000);

        if (!isInCowPen(Player.getRSPlayer().getPosition()))
            moveToCowPen();

        statusString = "Arrived at Cows";
        General.println("We have arrived at the Cow Pen, continuing.");
        General.sleep(2000, 3000);
    }

    private void killCows() {
        General.println("Finding nearest cow to attack.");
        RSNPC[] cowsToFight = NPCs.findNearest(COW_ID);
        if (cowsToFight.length > 0){
            if (cowsToFight[0] != null && !cowsToFight[0].isInCombat()){
                attackCow(cowsToFight[0]);
            }else if (!(cowsToFight.length < 2)){
                if (cowsToFight[1] != null && !cowsToFight[1].isInCombat()){
                    attackCow(cowsToFight[1]);
                }
            }
        }
    }
    private void attackCow(RSNPC cowToAttack){
        statusString = "Attacking Cow";
        General.println("Attacking Cow...");
        if (cowToAttack != null && cowToAttack.isOnScreen()){
            cowToAttack.click("Attack");
            General.sleep(500);
            if (Player.getRSPlayer().getAnimation() == -1){
                cowToAttack.click("Attack");
            }else{
                General.println("Successful Attack.");
                General.sleep(1000, 2000);
            }
        }else if (cowToAttack != null){
            Camera.turnToTile(cowToAttack.getAnimablePosition());
        }
    }

    public boolean eatFood(int id){
        if (GameTab.getOpen() != GameTab.TABS.INVENTORY)
            GameTab.open(GameTab.TABS.INVENTORY);

        RSItem[] foodToClick = Inventory.find(id);
        if (foodToClick.length > 0) {
            Mouse.drag(Mouse.getPos(), foodToClick[0].getArea().getLocation(), 1);
        }else {
            return ranOutOfFood();
        }

        foodToClick = Inventory.find(id);
        if (foodToClick.length == 0) {
            ranOutOfFood();
        }

        return true;
    }

    public boolean ranOutOfFood(){
        statusString = "Out of Food";
        General.println("Inventory has no food, banking");
        isBanking = true;
        if (WebWalking.walkToBank()) {
            if (bankForItems(foodID, foodAMT)) {
                General.println("Successfuly banked and retrieved food.  Heading back to cows...");
                return true;
            } else {
                General.println("An error in banking has occurred.");
                General.println("Error Code: BANK_144");
                General.println("Please report this bug on the Tribot forum thread: " + forumURL);
                return false;
            }
        }else{
            General.println("An error in banking has occurred.");
            General.println("Error Code: BANK_196");
            General.println("Please report this bug on the Tribot forum thread: " + forumURL);
            return false;
        }
    }

    public boolean bankForItems(int id, int amt){
        statusString = "Banking";
        General.println("Withdrawing food");

        if (!Banking.isBankScreenOpen()){
            if(!Banking.openBank()) {
                General.println("Could not open bank for some reason, stopping script.");
                General.println("Error Code: BANK_113");
                General.println("Please report this bug on the Tribot forum thread: " + forumURL);
                return false;
            }
            General.println("Depositing All Items.");
        }

        if (Banking.depositAll() < 1) {
            General.println("Missed the click for \"Deposit All\", trying again.");
            bankForItems(id, amt);
        }

        if (Banking.find(id).length > 0) {
            statusString = "Withdrawing Food";
            General.println("Withdrawing " + amt + " of " + foodName);
            Banking.withdraw(amt, id);
        }else {
            statusString = "Out of Food";
            General.println("No food left in bank. Logging out.");
            Login.logout();
            return false;
        }

        if (Inventory.find(id).length > 0){
            General.println("Have food in inventory.");
            General.sleep(1000, 2000);
            isBanking = false;
            return true;
        }

        return true;
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ** END SCRIPT FUNCTIONALITY ** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ** BEGIN SETTINGS METHODS ** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //

    private void startSettings(){

        settingsFrame = new JFrame("\"Ninja Cows\" Setup");
        settingsFrame.setSize(600, 500);
        settingsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        settingsFrame.setResizable(false);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        settingsFrame.setLocation(dim.width/2-settingsFrame.getSize().width/2,
                dim.height/2-settingsFrame.getSize().height/2);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.DARK_GRAY);

        JLabel intro1 = new JLabel("Welcome to the Ninja Cow Killer!");
        JLabel intro2 = new JLabel("Please choose the type of food to use:");
        intro1.setFont(new Font("serif", Font.BOLD, 30));
        intro2.setFont(new Font("serif", Font.ITALIC, 25));
        intro1.setForeground(Color.CYAN);
        intro2.setForeground(Color.WHITE);
        intro1.setAlignmentX(Component.CENTER_ALIGNMENT);
        intro2.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(intro1);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(intro2);

        mainPanel.add(Box.createVerticalStrut(10));

        foodOptions = new JComboBox(FOOD_OPTIONS);
        foodOptions.setSelectedIndex(0);
        foodOptions.setMaximumSize(new Dimension(250, 30));
        foodOptions.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(foodOptions);

        mainPanel.add(Box.createVerticalStrut(20));

        JLabel intro3 = new JLabel("How much food should we bring on each trip? (Max 27):");
        intro3.setFont(new Font("serif", Font.ITALIC, 25));
        intro3.setForeground(Color.WHITE);
        intro3.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(intro3);

        mainPanel.add(Box.createVerticalStrut(10));

        foodAmountField = new JTextField();
        foodAmountField.setMaximumSize(new Dimension(100, 30));
        foodAmountField.setHorizontalAlignment(JTextField.CENTER);
        foodAmountField.setAlignmentX(Component.CENTER_ALIGNMENT);
        foodAmountField.setText("10");
        mainPanel.add(foodAmountField);

        mainPanel.add(Box.createVerticalStrut(20));

        JLabel intro4 = new JLabel("Should we use the chat anti-ban? (Recommended):");
        intro4.setFont(new Font("serif", Font.ITALIC, 25));
        intro4.setForeground(Color.WHITE);
        intro4.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(intro4);

        mainPanel.add(Box.createVerticalStrut(10));

        toggleChat = new JButton("Chat Enabled - Click To Disable Chat (Not Recommended!)");
        toggleChat.addActionListener(this);
        toggleChat.setAlignmentX(Component.CENTER_ALIGNMENT);
        toggleChat.setForeground(Color.WHITE);
        toggleChat.setBackground(Color.BLUE);
        mainPanel.add(toggleChat);

        mainPanel.add(Box.createVerticalStrut(60));

        startButton = new JButton("Start Murdering Cows For Your Own Personal Gain");
        startButton.addActionListener(this);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setBackground(Color.GREEN);
        mainPanel.add(startButton);

        settingsFrame.add(mainPanel);
        settingsFrame.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton){
            hasFinishedSettings = true;
            foodID = FOOD_IDS[foodOptions.getSelectedIndex()];
            foodName = FOOD_OPTIONS[foodOptions.getSelectedIndex()];
            if (foodID == -1){
                isUsingFood = false;
                General.println("We are not using food.");
            }else{
                isUsingFood = true;
                General.println("Using " + foodName + " for food.");
            }

            if (shouldUseChat){
                General.println("Using Anti-Ban Chatter, Good Choice!");
            }else{
                General.println("Not using Anti-Ban Chatter :(");
            }
            settingsFrame.setVisible(false);
            settingsFrame.dispose();
            statusString = "Setup Finished";
        }else if (e.getSource() == toggleChat){
            if (shouldUseChat){
                shouldUseChat = false;
                toggleChat.setText("Chat Disabled - Click To Enable Chat (Recommended!)");
                toggleChat.setForeground(Color.GREEN);
                toggleChat.setBackground(Color.DARK_GRAY);
            }else{
                shouldUseChat = true;
                toggleChat.setText("Chat Enabled - Click To Disable Chat (Not Recommended!)");
                toggleChat.setForeground(Color.WHITE);
                toggleChat.setBackground(Color.BLUE);
            }
        }
    }







    /**
     * Setup for paint methods
     */
    private Image getImage(String url){
        try{
            return ImageIO.read(new URL(url));
        }catch (IOException e){
            return null;
        }
    }

    private final Image paintImage = getImage("http://i.imgur.com/Ia9PIzO.png");
    private final Image hideImage = getImage("http://i.imgur.com/oYtzvMk.png");
    private final Image hideImageGreen = getImage("http://i.imgur.com/XWSLGFS.png");

    private static final long startTime = System.currentTimeMillis();
    private long timeRunSoFar;

    @Override
    public void onPaint(Graphics g) {


        if (shouldShowPaint){

            Graphics2D gg = (Graphics2D)g;
            gg.drawImage(paintImage, 0, 270, null);

            gg.drawImage(hideImage, 470, 330, null);

            timeRunSoFar = System.currentTimeMillis() - startTime;
            g.setFont(new Font("Verdana", Font.BOLD, 20));
            g.setColor(new Color(175, 20, 20));
            g.drawString(Timing.msToString(timeRunSoFar), 228, 370);
            g.drawString(statusString, 215, 420);
        }else{
            Graphics2D gg = (Graphics2D)g;
            gg.drawImage(hideImageGreen, 470, 330, null);
        }

    }

    private static final Rectangle Toggle_Paint = new Rectangle(470, 330, 20, 20);

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
    public void mouseMoved(Point point, boolean b) {

    }

    @Override
    public void mouseDragged(Point point, int i, boolean b) {

    }

    @Override
    public void mouseReleased(Point point, int i, boolean b) {

    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ** END SETTINGS METHODS ** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ** BEGIN HELPER METHODS ** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //

    public boolean isInCowPen(RSTile t){
        if (!cowArea.contains(t.getX(), t.getY()))
            return false;

        return true;
    }

    public void handleBotWorlds(){
        if(Game.getCurrentWorld() == 385 || Game.getCurrentWorld() == 386){
            // Open friends tab to seem like we are checking world
            if (GameTab.getOpen() != GameTab.TABS.FRIENDS)
                GameTab.open(GameTab.TABS.FRIENDS);
            General.sleep(1000, 2000);

            //Print random text about having your world switched
            int indexToType = randomGenerator.nextInt(botWorldStrings.length);
            Keyboard.typeSend(botWorldStrings[indexToType]);
            statusString = "Leaving Bot World";
            General.println("Woah, looks like you've been placed in a Botting World!");
            General.println("Fun Fact: The Jagex bot detection system sometimes changes your world to \"Botting Worlds\"");
            General.println("These are worlds 385, and 386.  If you are placed in this world, you are being watched in that world");
            General.println("By hopping worlds, we are telling jagex that we are not a bot!");
            General.sleep(1000, 2000);
            General.println("Changing worlds...");
            WorldHopper.changeWorld(WorldHopper.getRandomWorld(true));
        }
    }

}

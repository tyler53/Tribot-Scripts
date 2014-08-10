package scripts;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by tyler53 on 8/10/2014.
 * Ninja Spiders kills Giant Spiders in level 3 of the Stronghold of Security.
 */

@ScriptManifest(authors = {"Tyler53"}, name = "Ninja Spiders", category = "Combat")
public class NinjaSpiders extends Script implements ActionListener /**implements AntiBanChatter*/{

    boolean isRunning = false;

    //AntiBanChatter chatter;
    /**
     * scriptState stores information about where we are in the process of killing giant spiders.
     * -1 = Banking
     * 0 = Walking to Spiders
     * 1 = Arrived at Entrance
     * 2 = Gone Down Entrance, Now in First Room
     * 3 = Passed First Room, Now in Chest Room
     * 4 = Gone Down Ladder, Now In Second Room
     * 5 = Gone Through Portal, Now in Chest Room
     * 6 = Gone Down Ladder, Now in Third Room
     * 7 = Fighting In Room
     * 8 = Lost Aggro, Need To Change Fight Rooms
     */
    public int scriptState = 0;
    /**
     * Used for going through the double door sets in Stronghold of Security
     * 0 = Opening First Door
     * 1 = Passed First Door, Opening Second Door
     * 2 = Passed Second Door, Can Continue
     */
    public int doorState = 0;

    public int currentFightRoom = 1;

    // TILE VARIABLES
    public static RSTile ENTRANCE_TILE = new RSTile(3081, 3421, 0);
    public static RSTile ENTRANCE_DOUBLE_DOORS = new RSTile(10, 10, 0);

    public static RSTile FIRST_CHEST_LADDER_TILE = new RSTile(1902, 5221, 0);
    public static RSTile SECOND_CHEST_LADDER_TILE = new RSTile(2025, 5218, 0);

    public static RSTile BOTTOM_DOORS_FROM_BOTTOM = new RSTile(10, 10, 0);
    public static RSTile MIDDLE_DOORS_FROM_BOTTOM = new RSTile(10, 10, 0);
    public static RSTile TOP_DOORS_FROM_BOTTOM = new RSTile(10, 10, 0);

    public static RSTile BOTTOM_DOORS_FROM_TOP = new RSTile(10, 10, 0);
    public static RSTile MIDDLE_DOORS_FROM_TOP = new RSTile(10, 10, 0);
    public static RSTile TOP_DOORS_FROM_TOP = new RSTile(10, 10, 0);

    //CORRECT DOOR ANSWERS
    public static String[] CORRECT_DOOR_ANSWERS = {"steal my password", "nobody", "nowhere", "report abuse", "virus scan my computer then change my password",
                                                    "famous person", "don't give him", "talk to any banker"};

    // OBJECT VARIABLES
    public static int ENTRANCE_OBJECT_ID = 20790;

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

    //GUI
    JFrame settingsFrame;
    JComboBox foodOptions;
    JTextField foodAmountField;
    JButton toggleChat;
    JButton startButton;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ** END VARIABLES ** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ** BEGIN ACTUAL SCRIPT ** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //

    @Override
    public void run() {
        if (!isRunning)
            isRunning = true;

        //chatter = new AntiBanChatter("Combat");

        if (Login.getLoginState() == Login.STATE.INGAME){
            if (Inventory.find(foodID).length < foodAMT){
                General.println("Not enough food, banking");
                bank();
            }
            RSNPC[] spiders = NPCs.findNearest(3);
            if (spiders[0] != null)
                if (Player.getRSPlayer().getPosition().distanceTo(spiders[0].getPosition()) < 10)
                    scriptState = 7;

            while(isRunning){
                sleep(loop());
            }
        }else{
            General.println("Waiting for Setup...");
            Login.login();
        }

    }

    public int loop(){
        //chatter.checkForPlayers();
        if (!(Inventory.find(foodID).length > 0)){
            if (scriptState != -1) {
                General.println("Out Of Food... Banking...");
                bank();
            }
        }

        if (Player.getRSPlayer().getHealth() < Player.getRSPlayer().getMaxHealth()/2)
            eatFood(foodID);

        switch (scriptState){
            case -1:
                bank();
                break;
            case 0:
                walkToSpiders();
                break;
            case 1:
                enterStronghold();
                break;
            case 2:
                passRoom(1);
                break;
            case 3:
                passChestRoom(1);
                break;
            case 4:
                passRoom(2);
                break;
            case 5:
                passChestRoom(2);
                break;
            case 6:
                passRoom(3);
                break;
            case 7:
                fightInRoom(currentFightRoom);
                break;
            case 8:
                changeRooms(currentFightRoom);
                break;
            default:
                break;
        }
        return 100;
    }

    /**
     * Walks to the tile right in front of Stronghold entrance
     */
    public void walkToSpiders(){
        if (WebWalking.walkTo(ENTRANCE_TILE)){
            scriptState = 1;
        }
    }

    /**
     * Enters Stronghold of Security
     * @return
     * true if successful
     */
    public boolean enterStronghold(){
        if (Walking.clickTileMM(ENTRANCE_TILE, 1)) {
            RSObject[] entranceObject = Objects.findNearest(10, ENTRANCE_OBJECT_ID);
            entranceObject[0].click("Climb-down");
            General.sleep(1000);
            RSObject[] portalObject = Objects.findNearest(30, "Portal");
            if (portalObject.length > 0)
                scriptState = 2;
        }

        return true;
    }

    /**
     * Passes through the given room (finds the portal and goes through it)
     *
     * @param stage
     * int - which room we are in
     *
     * @return
     * true if successful, false otherwise
     */
    public boolean passRoom(int stage){
        RSObject[] portalObject = Objects.findNearest(30, "Portal");

        if (stage == 1){
            if (portalObject.length > 0 && portalObject[0] != null)
                portalObject[0].click("Use");
            else
                return false;
            scriptState = 3;
        }else if (stage == 2){
            if (portalObject.length > 0 && portalObject[0] != null)
                portalObject[0].click("Use");
            else
                return false;
            scriptState = 5;
        }else if (stage == 3){
            passDoubleDoors(-1, -1);
        }else{
            return false;
        }

        return true;
    }

    /**
     * Passes through the given chest room (finds the ladder, and climbs down it)
     *
     * @param stage
     * int - which chest room we are in
     *
     * @return
     * true if successful, false otherwise
     */
    public boolean passChestRoom(int stage){
        if (stage == 1){
            if (WebWalking.walkTo(FIRST_CHEST_LADDER_TILE)) {
                RSObject[] ladder = Objects.findNearest(5, "Ladder");
                if (ladder.length > 0 && ladder[0] != null) {
                    ladder[0].click("Climb-down");
                    scriptState = 4;
                }
            }
        }else if (stage == 2){
            if (WebWalking.walkTo(SECOND_CHEST_LADDER_TILE)) {
                RSObject[] ladder = Objects.findNearest(5, "Ladder");
                if (ladder.length > 0 && ladder[0] != null) {
                    ladder[0].click("Climb-down");
                    scriptState = 6;
                }
            }
        }else{
            return false;
        }

        return true;
    }

    public void fightInRoom(int room){
        //TODO
    }

    /**
     * Changes rooms by walking to whichever room we are not in
     *
     * @param initialRoom
     * int - Current room we are fighting in
     */
    public void changeRooms(int initialRoom){
        if (passDoubleDoors(0, initialRoom) == 1){
            if (passDoubleDoors(1, initialRoom) == 2){
                if (passDoubleDoors(2, initialRoom) == 3){
                    if (initialRoom == 1)
                        currentFightRoom = 2;
                    else if (initialRoom == 2)
                        currentFightRoom = 1;
                    scriptState = 7;
                }
            }
        }
    }

    /**
     * Walks through a series of double doors leading to the next room to fight in.
     *
     * @param doorChoice
     * int - Which set of doors to open next
     * @param previousRoom
     * int - Which room we are coming from
     *
     * @return
     * which stage of doors we have just passed.
     */
    public int passDoubleDoors(int doorChoice, int previousRoom){

        if (previousRoom == -1){
            //We are at the entrance, open the first two doors and then fight in first room
            if (WebWalking.walkTo(ENTRANCE_DOUBLE_DOORS)){
                RSObject[] nearestDoors = Objects.findNearest(10, "Oozing barrier");
                if (nearestDoors.length > 1 && nearestDoors[0] != null){
                    nearestDoors[0].click("Open");
                    General.sleep(1000, 2000);
                    nearestDoors[1].click("Open");
                }
                if (answerQuestion()) {
                    scriptState = 7;
                    currentFightRoom = 1;
                    return -1;
                }
            }
        }else if (previousRoom == 1){
            //We are in the first room, open the doors for the path and then fight in second room
            if (doorChoice == 0){
                if(handlePass(BOTTOM_DOORS_FROM_BOTTOM))
                    return 1;
            }else if (doorChoice == 1){
                if (handlePass(MIDDLE_DOORS_FROM_BOTTOM))
                    return 2;
            }else if (doorChoice == 2 ){
                if (handlePass(TOP_DOORS_FROM_BOTTOM))
                    return 3;
            }
        }else if (previousRoom == 2){
            //We are in the second room, open the doors for the path and then fight in first room
            if (doorChoice == 0){
                if(handlePass(TOP_DOORS_FROM_TOP))
                    return 1;
            }else if (doorChoice == 1){
                if (handlePass(MIDDLE_DOORS_FROM_TOP))
                    return 2;
            }else if (doorChoice == 2 ){
                if (handlePass(BOTTOM_DOORS_FROM_TOP)){
                    return 3;
                }
            }
        }

        return -1;
    }

    /**
     * Handles the passage through the given set of double doors
     *
     * @param t
     * RSTile - Tile in front of double doors to pass
     *
     * @return
     * true if successful, false otherwise
     */
    public boolean handlePass(RSTile t) {
        if (WebWalking.walkTo(t)) {
            RSObject[] nearestDoors = Objects.findNearest(10, "Oozing barrier");
            if (nearestDoors.length > 1 && nearestDoors[0] != null) {
                nearestDoors[0].click("Open");
                General.sleep(1000, 2000);
                nearestDoors[1].click("Open");
            }
            if (answerQuestion())
                return true;
        }
        return false;
    }

    /**
     * Answers the question given by the second opened door
     *
     * @return
     * true if successfully answered, false otherwise
     */
    public boolean answerQuestion(){
        General.sleep(500, 1000);
        NPCChat.clickContinue(false);
        General.sleep(1000, 2000);
        String[] options = NPCChat.getOptions();
        if (options.length > 2){
            for (String s : CORRECT_DOOR_ANSWERS){
                for (String option : options){
                    if (option.toLowerCase().contains(s)) {
                        NPCChat.selectOption(option, false);
                        break;
                    }
                }
            }
        }else if (options.length > 1){
            NPCChat.selectOption("No", false);
        }else if (options.length == 1) {
            NPCChat.clickContinue(false);
            answerQuestion();
            return true;
        }
        General.sleep(1000, 2000);

        if (NPCChat.clickContinue(false))
            return true;

        return false;
    }

    /**
     * Banks for any reason (Usually food)
     */
    public void bank(){
        //TODO
        scriptState = -1;
    }

    public void eatFood(int id){
        RSItem[] foodItems = Inventory.find(foodID);
        if (foodItems.length > 0 && foodItems[0] != null){
            foodItems[0].click();
        }else{
            General.println("Out Of Food, Banking...");
            bank();
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ** END SCRIPT FUNCTIONALITY ** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ** BEGIN SETTINGS METHODS ** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //

    //TODO

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}

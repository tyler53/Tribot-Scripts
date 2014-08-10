package scripts;

import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

/**
 * Created by tyler53 on 8/10/2014.
 * Ninja Spiders kills Giant Spiders in level 3 of the Stronghold of Security.
 */

@ScriptManifest(authors = {"Tyler53"}, name = "Ninja Spiders", category = "Combat")
public class NinjaSpiders extends Script /**implements AntiBanChatter*/{

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

    boolean isRunning = false;

    //AntiBanChatter chatter;

    @Override
    public void run() {
        if (!isRunning)
            isRunning = true;

        //chatter = new AntiBanChatter("Combat");

        while(isRunning){
            sleep(loop());
        }
    }

    public int loop(){
        //chatter.checkForPlayers();
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

    public void walkToSpiders(){

    }

    public boolean enterStronghold(){


        return true;
    }

    public boolean passRoom(int stage){
        if (stage == 1){

        }else if (stage == 2){

        }else if (stage == 3){

        }else{
            return false;
        }

        return true;
    }

    public boolean passChestRoom(int stage){
        if (stage == 1){

        }else if (stage == 2){

        }else{
            return false;
        }

        return true;
    }

    public void fightInRoom(int room){

    }

    public void changeRooms(int initialRoom){

    }

    public void bank(){

    }
}

package scripts;

import org.tribot.api.General;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.Player;
import org.tribot.api2007.Players;
import org.tribot.api2007.Skills;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.script.interfaces.MessageListening07;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Tyler53 on 7/31/2014.
 *
 * This is a class intended for Anti-Ban use, to be integrated into Tribot Scripts.
 * It talks to, and responds to, players around you, and will add a very human like element to your script.
 */


public class AntiBanChatter implements MessageListening07{

    //Randomization
    public Random randomGenerator;

    //Checking Variables
    public boolean playersNearby = false;
    public ArrayList<String> playersAlreadyEngaged;
    public ArrayList<String> playersAlreadyLevelQueried;
    public ArrayList<String> playersAlreadySkillTrainingQueried;
    public ArrayList<String> playersToListenFor;
    public boolean shouldAskForSkill = true;
    public String skillNameToAsk;
    public Skills.SKILLS skillToAsk;

    //Chat presets
    public static String[] startChatStrings = {"Hey", "hey", "Yo", "yo", "sup", "Sup", "Aye", "aye", "Hello", "hello", "Hey there", "hey there"};
    public static String[] reciprocateQuestionStrings = {"how about you?", "How about you", "how bout you", "you?", "what's yours?", "and yours?"};
    public static String[] askSkillStrings = {"what skill are you training?", "whatcha training?", "what are u training?", "wat you training?"};
    public String skillToTell;

    //Messages to check for
    public static String[] questions = {"what", "wat", "is", "your", "ur", "level", "lvl"};
    public static String[] compliments = {"nice", "cool", "great", "neat", "awesome", "impressive"};
    public static String[] skillNamesArray = {"att", "str", "def", "range", "pray", "mag", "rune", "rc", "constr", "hit", "hp", "agil", "herb", "thiev", "thief",
            "craft", "fletch", "slay", "hunt", "mine", "mining", "smith", "fish", "cook", "fire", "fm", "wood", "wc", "farm"};

    //Timing
    TimerTask waitAfterGreeting;
    Timer timer;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ** END VARIABLES ** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ** BEGIN ACTUAL SCRIPT ** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //

    /**
     * Constructor for AntiBanChatter Class
     * ~~ REQUIRED ~~ call this in your Run() method to use this class if you are asking for skills! ~~ REQUIRED ~~
     *
     * This constructor should be called IF AND ONLY IF you are in a situation where it is NOT obvious which skill others are
     * training.  For example, if you are training combat skills, you would use this constructor because people in the
     * area could be training any number of combat skills.  However, if you are training fishing, you should use the other
     * constructor (the one with a String argument) because people around you will only be training fishing.
     *
     */
    public AntiBanChatter(String skillNameBeingTrained){

        shouldAskForSkill = true;
        skillToTell = skillNameBeingTrained;
        randomGenerator = new Random();
        playersAlreadyEngaged = new ArrayList<String>();
        playersAlreadyLevelQueried = new ArrayList<String>();
        playersAlreadySkillTrainingQueried = new ArrayList<String>();
        playersToListenFor = new ArrayList<String>();

        waitAfterGreeting = new TimerTask() {
            @Override
            public void run() {
                handleEndOfTimer();
            }
        };

        timer = new Timer();

        General.println("This script is using Tyler53's \"Ninja Anti-Ban Chatter\"");
    }

    /**
     * Constructor for AntiBanChatter Class
     * ~~ REQUIRED ~~ call this in your Run() method to use this class if you aren't asking for skills! ~~ REQUIRED ~~
     *
     * This constructor should be called IF AND ONLY IF you are in a situation where it IS obvious which skill is
     * being trained.  For example, if you are training combat skills, you would NOT use this constructor because people in the
     * area could be training any number of combat skills.  However, if you are training fishing, you SHOULD use this
     * constructor because people around you will only be training fishing.
     *
     * @param skillNameBeingTrained
     * String - the name of the skill that you will be training (i.e. "fishing")
     * @param skillBeingTrained
     * Skills.SKILL - the skill that you will be training (i.e. Skills.SKILL.FISHING)
     */
    public AntiBanChatter(String skillNameBeingTrained, Skills.SKILLS skillBeingTrained){
        shouldAskForSkill = false;
        skillNameToAsk = skillNameBeingTrained;
        skillToAsk = skillBeingTrained;

        randomGenerator = new Random();
        playersAlreadyEngaged = new ArrayList<String>();
        playersAlreadyLevelQueried = new ArrayList<String>();
        playersToListenFor = new ArrayList<String>();

        waitAfterGreeting = new TimerTask() {
            @Override
            public void run() {
                handleEndOfTimer();
            }
        };

        timer = new Timer();

        General.println("This script is using Tyler53's \"Ninja Anti-Ban Chatter\"");

    }

    /**
     * This method should be manually called using AntiBanChatter.checkForPlayers() in your script.
     * See tutorial on thread for more details.  ** ~~~~~~~~~~~~~~~~~~ ADD FORUM THREAD ~~~~~~~~~~~~~~~~~~ **
     *
     * Checks for other players that are nearby to see if we should talk to them.
     * If we are close enough, and haven't already talked to them, we talk to them to seem human,
     * but if they're too far away, we don't.
     *
     *  ## ~~~~~~~~ ## NOTE: IT IS YOUR RESPONSIBILITY TO HANDLE SLEEPING, THIS SCRIPT DOES NOT SLEEP ## ~~~~~~~~ ##
     *
     * @return
     * true if there is a nearby player close enough to talk to.  false otherwise.
     */
    public boolean checkForPlayers(){
        RSPlayer[] nearestPlayers = Players.getAll();

        for (RSPlayer p : nearestPlayers){
            if (p != null && p.getPosition().distanceTo(Player.getPosition()) < 15) {
                playersNearby = true;
                if (p != null && !playersAlreadyEngaged.contains(p.getName()))
                    engageInConversation(p.getName());
            }else
                playersNearby = false;
        }

        return playersNearby;
    }

    /**
     * Engages the given player in conversation, then adds them to the list of people we have already talked to.
     * Will only be called by checkForPlayers() if there is a player
     * close enough to talk to.
     *
     * @param playerNameToEngage
     * String name of player that we should engage in conversation
     */
    public void engageInConversation(String playerNameToEngage){
        int indexToType = randomGenerator.nextInt(startChatStrings.length);
        Keyboard.typeSend(startChatStrings[indexToType] + " " + playerNameToEngage.toLowerCase());
        playersAlreadyEngaged.add(playerNameToEngage);
        playersToListenFor.add(playerNameToEngage);
    }

    /**
     * Checks if the message sent to us is the beginning of a conversation.  If it is not, this method checks
     * if the message sent to us contains at least two of our keywords to see if we are being asked a question
     * about our levels.  If we are, we process the message as a question, if not, we process it as a statement.
     * This method also checks for and responds to compliments.
     *
     * @param mess
     * The message that we have received
     *
     * @return
     * true if something relevant was said and we responded, false otherwise
     */
    public boolean checkMessageType(String mess, String name){
        String message = mess.toLowerCase();

        for (String s : compliments){
            if (message.contains(s)){sayThanks();return true;}
        }

        /**
         * If the message is a greeting, we check to see if we have already greeted that person.
         * If we have, we continue to ask them a question.  If we have not greeted them, we greet them,
         * and start a timer to ask a question if they don't ask one soon.
         */
        for (String s : startChatStrings){
            if (message.contains(s)){
                if (playersAlreadyEngaged.contains(name)){
                    if (!shouldAskForSkill){
                        askForSkillLevel(skillNameToAsk);
                        return true;
                    }else{
                        askForSkillTraining();
                        return true;
                    }
                }
                else{
                    engageInConversation(name);
                    timer.schedule(waitAfterGreeting, 8331);
                    return true;
                }
            }
        }

        if (message.contains(Player.getRSPlayer().getName().toLowerCase().substring(0, 2))){
            if (playersAlreadyEngaged.contains(name)){
                if (!shouldAskForSkill){
                    askForSkillLevel(skillNameToAsk);
                    return true;
                }else{
                    askForSkillTraining();
                    return true;
                }
            }
            else{
                engageInConversation(name);
                timer.schedule(waitAfterGreeting, 8697);
                return true;
            }
        }

        int countFlaggedQuestionWords = 0;

        for (String s : questions)
            if (message.contains(s))
                countFlaggedQuestionWords++;

        if (playersToListenFor.contains(name)) {
            if (countFlaggedQuestionWords > 1) {
                timer.cancel();
                processQuestion(message, name);
                return true;
            } else {
                processStatement(message, name);
                return true;
            }
        }
        return false;
    }

    /**
     * Processes the message sent to us as a question.  Checks to see what level they are inquiring about,
     * then responds appropriately.
     *
     * @param mess
     * The question we have received
     */
    public void processQuestion(String mess, String name){
        String message = mess.toLowerCase();

        if(message.contains("lvl") || message.contains("level")){
            if (shouldAskForSkill) {
                if (message.contains("att")) {
                    respondToQuestion(name, "att", Skills.SKILLS.ATTACK);
                } else if (message.contains("str")) {
                    respondToQuestion(name, "str", Skills.SKILLS.STRENGTH);
                } else if (message.contains("def")) {
                    respondToQuestion(name, "def", Skills.SKILLS.DEFENCE);
                } else if (message.contains("range")) {
                    respondToQuestion(name, "range", Skills.SKILLS.RANGED);
                } else if (message.contains("pray")) {
                    respondToQuestion(name, "prayer", Skills.SKILLS.PRAYER);
                } else if (message.contains("magic") || message.contains("mage")) {
                    respondToQuestion(name, "mage", Skills.SKILLS.MAGIC);
                } else if (message.contains("rc") || message.contains("runecraft")) {
                    respondToQuestion(name, "runecraf", Skills.SKILLS.RUNECRAFTING);
                } else if (message.contains("const")) {
                    respondToQuestion(name, "construct", Skills.SKILLS.CONSTRUCTION);
                } else if (message.contains("hp") || message.contains("hit")) {
                    respondToQuestion(name, "hp", Skills.SKILLS.HITPOINTS);
                } else if (message.contains("agil")) {
                    respondToQuestion(name, "agil", Skills.SKILLS.AGILITY);
                } else if (message.contains("herb")) {
                    respondToQuestion(name, "herb", Skills.SKILLS.HERBLORE);
                } else if (message.contains("thief") || message.contains("thiev")) {
                    respondToQuestion(name, "thief", Skills.SKILLS.THIEVING);
                } else if (message.contains("craft")) {
                    respondToQuestion(name, "craft", Skills.SKILLS.CRAFTING);
                } else if (message.contains("fletch")) {
                    respondToQuestion(name, "fletch", Skills.SKILLS.FLETCHING);
                } else if (message.contains("slay")) {
                    respondToQuestion(name, "slayer", Skills.SKILLS.SLAYER);
                } else if (message.contains("hunt")) {
                    respondToQuestion(name, "hunter", Skills.SKILLS.HUNTER);
                } else if (message.contains("mine") || message.contains("mining")) {
                    respondToQuestion(name, "mining", Skills.SKILLS.MINING);
                } else if (message.contains("smith")) {
                    respondToQuestion(name, "smithing", Skills.SKILLS.SMITHING);
                } else if (message.contains("fish")) {
                    respondToQuestion(name, "fish", Skills.SKILLS.FISHING);
                } else if (message.contains("cook")) {
                    respondToQuestion(name, "cooking", Skills.SKILLS.COOKING);
                } else if (message.contains("fire") || message.contains("fm")) {
                    respondToQuestion(name, "fm", Skills.SKILLS.FIREMAKING);
                } else if (message.contains("wc") || message.contains("wood")) {
                    respondToQuestion(name, "wc", Skills.SKILLS.WOODCUTTING);
                } else if (message.contains("farm")) {
                    respondToQuestion(name, "farm", Skills.SKILLS.FARMING);
                }else{
                    respondToQuestion(name, skillToTell, null);
                }
            }else{
                if (message.contains("skill") || message.contains("train"))
                    respondToQuestion(name, skillNameToAsk, null);
                else
                    respondToQuestion(name, skillNameToAsk, skillToAsk);
            }
        }else if (message.contains("wat") || message.contains("what") || message.contains("skill") || message.contains("are") || message.contains("u") || message.contains("you") || message.contains("train")){
            respondToQuestion(name, skillToTell, null);
        }
    }

    public boolean respondToQuestion(String name, String skillName, Skills.SKILLS skill){

        if (skill != null){
            int ran = General.random(0,2);
            if(ran == 0){
                Keyboard.typeSend(Integer.toString(Skills.getCurrentLevel(skill)));
            }else if(ran == 1){
                Keyboard.typeSend("My " + skillName + "? "+ Integer.toString(Skills.getCurrentLevel(skill)));
            }else{
                Keyboard.typeSend("My " + skillName + " is "+ Integer.toString(Skills.getCurrentLevel(skill)));
            }
        }else{
            Keyboard.typeSend(skillToTell);
        }

        if (!playersAlreadyLevelQueried.contains(name)){
            int indexToType = randomGenerator.nextInt(reciprocateQuestionStrings.length);
            Keyboard.typeSend(reciprocateQuestionStrings[indexToType]);
            playersAlreadyLevelQueried.add(name);
        }

        return true;
    }

    /**
     * Processes the message sent to us as a statement.  Checks to see what is being said, and then processes accordingly.
     * If they have just told us what skill they are training, we ask what level it is.  If they give us a level,
     * we compliment them because we are decent human beings.
     *
     * @param mess
     * The statement we have received
     */
    public void processStatement(String mess, String name){
        String message = mess.toLowerCase();

        for (String s : skillNamesArray){
            if (message.contains(s)){
                timer.cancel();
                askForSkillLevel(s);
            }
        }

        if (message.contains("0") || message.contains("1") || message.contains("2") || message.contains("3") ||
                message.contains("4") || message.contains("5") || message.contains("6") || message.contains("7") ||
                message.contains("8") || message.contains("9") || message.contains("10")){
            respondToStatement("compliment");
        }
    }

    public void respondToStatement(String type){
        if (type.equals("compliment")) {
            int indexToType = randomGenerator.nextInt(compliments.length);
            Keyboard.typeSend(compliments[indexToType]);
        }
    }
    /**
     * Says thank you :)
     */
    public void sayThanks(){
        int ran = General.random(0, 3);
        if (ran == 0)
            Keyboard.typeSend("thanks!");
        else if (ran == 1)
            Keyboard.typeSend("thank you");
        else if (ran == 2)
            Keyboard.typeSend("thanks :)");
        else
            Keyboard.typeSend("why thank you!");
    }

    /**
     * Asks the player what skill they are training.
     */
    public void askForSkillTraining(){
        int indexToType = randomGenerator.nextInt(askSkillStrings.length);
        Keyboard.typeSend(askSkillStrings[indexToType]);
    }

    /**
     * Asks for the skill level of the given skill (the one that the person is training)
     * @param skill
     */
    public void askForSkillLevel(String skill){

        int ran = General.random(0, 5);
        if (ran == 0)
            Keyboard.typeSend("What's your " + skill + "?");
        else if (ran == 1)
            Keyboard.typeSend("What level is ur " + skill + "?");
        else if (ran == 2)
            Keyboard.typeSend("wats ur " + skill + " lvl?");
        else if (ran == 3)
            Keyboard.typeSend(skill + " lvl?");
        else if (ran == 4)
            Keyboard.typeSend("what lvl is your " + skill + "?");
        else
            Keyboard.typeSend("wat lvl is ur " + skill + "?");
    }

    public void handleEndOfTimer(){
        if (!shouldAskForSkill){
            askForSkillLevel(skillNameToAsk);
        }else{
            askForSkillTraining();
        }
    }



    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ** END SCRIPT ** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ ** BEGIN CHAT METHODS ** ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ //

    @Override
    public void playerMessageReceived(String name, String mess) {
        String message = mess.toLowerCase();
        checkMessageType(message, name);
    }
    @Override
    public void personalMessageReceived(String name, String mess) {

    }

    // ~~ NOT USED ~~ //

    @Override
    public void clanMessageReceived(String s, String s2) {

    }
    @Override
    public void tradeRequestReceived(String s) {

    }
    @Override
    public void serverMessageReceived(String s) {

    }
    @Override
    public void duelRequestReceived(String s, String s2) {

    }
}

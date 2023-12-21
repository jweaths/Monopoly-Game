import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.FileWriter;


/**
 * The Game class represents a game instance with players, a board, and dice.
 */
public class Game {
    public static String SAVE_FILE = "SaveGame.json";
    public static int STARTMONEY;
    private Board board;
    private Die die;
    public static List<Player> players;
    private boolean allColors;
    private GUI2 gui;

    public static class GameState{

        public GameState(int currentPlayer, int numOfPlayers, String boardStyle, List<String> playerNames){
            this.currentPlayer = currentPlayer;
            this.numOfPlayers = numOfPlayers;
            this.boardStyle = boardStyle;
            this.playerNames = playerNames;
        }
        public GameState() {
        }
        public int currentPlayer;
        public int numOfPlayers;
        public String boardStyle;

        public List<String> playerNames;
    }
    private GameState state;


    public String getBoardStyle() { return state.boardStyle; }

    public void cleanProperty(){
        for (Player p : players)
            p.nullCityUtility();
    }

    public Player getPlayer(int i) { // for sending to display players in GUI2

        return players.get(i);

    }

    public Game(GameFactory factory, GUI2 gui) {
        STARTMONEY = factory.getCash();
        this.board = factory.createBoard(gui);
        this.players = factory.createPlayers(this);
        this.gui = gui;
        this.allColors = factory.getAllColors();
        board = new Board(gui);
        die = new Die();

        ArrayList<String> names = new ArrayList<String>();

        for (int i = 0; i < factory.getNumPlayers(); i++){
            names.add(this.players.get(i).getName());
        }

        this.state = new GameState(
                0,
                factory.getNumPlayers(),
                factory.getBoardStyle(),
                names
        );
    }
    public Game(GameState state, GameFactory factory, GUI2 gui) {

        this.state = state;
        STARTMONEY = factory.getCash();
        this.board = factory.createBoard(gui);

        Game.players = new ArrayList<Player>();
        this.gui = gui;
        this.board = new Board(gui);
        this.die = new Die();

        for (String name : this.state.playerNames){
            Player p = Player.loadPlayer(name, gui, board, this);
            Game.players.add(p);
            System.out.println(p.getPosition());
        }
    }

    public static Game loadGame(GameFactory factory, GUI2 gui) throws Exception {



        ObjectMapper mapper = new ObjectMapper();

        GameState state = mapper.readValue(new File(SAVE_FILE), GameState.class);

        return new Game(state, factory, gui);

    }
    
    public void saveGame(){
        System.out.print("Saving game...");
        for (Player p : players){
            p.saveState();
        }
        try{
            ObjectMapper mapper = new ObjectMapper();

            String saveString = mapper.writeValueAsString(this.state);

            FileWriter f = new FileWriter(SAVE_FILE);
            f.write(saveString);
            f.close();
        }
        catch(Exception e)
        {
            System.out.print("Exception: ");
            System.out.println(e);
        }
   }

    public Player findPlayer(String imageIcon){
        for (Player p : players){
            if (imageIcon.equals(p.getImageIcon())){
                return p;
            }
        }
        return null;
    }


    public void subscribeToPlayers(PlayerObserver o){
        for (Player p : players){
            p.subscribe(o);
        }
    }

    public int switchTurn() {
        if(state.numOfPlayers == state.currentPlayer + 1)
            state.currentPlayer = -1;
        return ++state.currentPlayer;
    }

    public void makeMove(Die roll) {
        gui.getTextArea().setText(getCurrentPlayer().getName() + " rolled a " + (roll.diceOne + roll.diceTwo) + "\n");
        players.get(state.currentPlayer).move(roll.diceOne+ roll.diceTwo);

        if (roll.isDouble()) gui.getTextArea().append(getCurrentPlayer().getName() + " rolled a doubles! They can roll again\n");

        // Check if player's new position is a city and it's owned by someone else
        int position = players.get(state.currentPlayer).getPosition();

        // if current player is not an AI, call the action() function
        // otherwise, call the AI makeDecision() function later
        //if (getCurrentPlayer().getType().equals("Player"))
        board.getPosition(position).action(getCurrentPlayer());

        for(Player pl : players) {
            if (pl.getIsBankrupted())
                return;
        }
    }


    public Player getCurrentPlayer() {
        return players.get(state.currentPlayer);
    }

    public Player getPrevPlayer() {
        if(state.currentPlayer != 0)
            return players.get(state.currentPlayer-1);
        else return players.get(3);
    }

    public int getNumPlayers() {
        return state.numOfPlayers;
    }

    public Board getBoard() {
        return board;
    }

    public int getCurrentPlayerIndex() {
        return state.currentPlayer;
    }
    public int getPrevPlayerIndex() {
        if(state.currentPlayer != 0)
            return state.currentPlayer-1;
        else return 3;
    }
    public static void gameOver(GUI2 guiInstance) {
        // Determine the player with the highest value
        Player winner = getPlayerWithHighestValue();
        int propertyMoney =calculateTotalValue(winner) - winner.getMoney();
        // Print the winner and other players in descending order of value
        System.out.println("Game Over!");
        System.out.println("Player with the highest value: " + winner.getName());
        System.out.println(winner.getName() + " has total $" + calculateTotalValue(winner) +
                "($" + winner.getMoney() + " in money and $" + propertyMoney + "in properties)");
        System.out.println("Other players:");
        players.stream()
                .filter(player -> player != winner)
                .sorted((p1, p2) -> Integer.compare(calculateTotalValue(p2), calculateTotalValue(p1)))
                .forEach(p -> System.out.println(p.getName() + "-- Total Value(Including properties and money): "
                        + calculateTotalValue(p)));

        guiInstance.onGameOver();

        // Update the text box with the winner details
        guiInstance.getTextArea().append("\nThe Winner is : " + winner.getName() + "\n");
        guiInstance.getTextArea().append(winner.getName() + " has total $" + calculateTotalValue(winner) +
                "($" + winner.getMoney() + "-- money, $" + propertyMoney + "-- properties)\n");
        guiInstance.getTextArea().append("Other players:\n");
        players.stream()
                .filter(player -> player != winner)
                .sorted((p1, p2) -> Integer.compare(calculateTotalValue(p2), calculateTotalValue(p1)))
                .forEach(p -> guiInstance.getTextArea().append(p.getName() + "-- Total Value: "
                        + calculateTotalValue(p) + "\n"));


//        System.exit(0);
    }
    private static Player getPlayerWithHighestValue() {
        Player winner = players.get(0);
        int highestValue = calculateTotalValue(winner);

        for (int i = 1; i < players.size(); i++) {
            Player player = players.get(i);
            int playerValue = calculateTotalValue(player) ;

            if (playerValue > highestValue) {
                winner = player;
                highestValue = playerValue;
            }
        }

        return winner;
    }
    private static int calculateTotalValue(Player player) {
        int totalValue = player.getMoney();

        for (City city : player.getOwnedCities()) {
            totalValue += city.getPrice();
        }

        for (Utility utility : player.getOwnedUtilities()) {
            totalValue += utility.getPrice();
        }

        return totalValue;
    }

    public boolean getAllColors(){
        return allColors;
    }
}

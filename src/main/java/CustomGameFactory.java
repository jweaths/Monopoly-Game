import java.util.ArrayList;
import java.util.List;
/**
 * The CustomGameFactory class implements the GameFactory interface and provides a custom game setup
 * by specifying the number of players, AI players, starting money, and board style.
 */
public class CustomGameFactory implements GameFactory {
    private int numPlayers;
    private int money;
    private boolean allColors;
    private String boardStyle = "Classic";
    private int numOfAiPlayers;
    private GUI2 gui;
    private AIDifficulty difficulty = AIDifficulty.EASY;

    public CustomGameFactory(int numPlayers, int numOfAiPlayers, int cash, String boardStyle, GUI2 gui) {
        this.numPlayers = numPlayers;
        this.numOfAiPlayers = numOfAiPlayers;
        this.money = cash;
        this.boardStyle = new String(boardStyle);
        this.gui = gui; // added gui into contructor
        this.allColors = false;
    }

    @Override
    public Board createBoard(GUI2 gui) {
        return null;
    }

    @Override
    public Board createBoard() {
        return new Board(gui);
    }

    @Override
    public List<Player> createPlayers(Game game) {
        List<Player> players = new ArrayList<>();
        switch(numOfAiPlayers) {
            case 0:
                players.add(new Player("Player 1", money, gui)); // ensure that each player/AI has unique number for ID
                players.add(new Player("Player 2", money, gui));
                players.add(new Player("Player 3", money, gui));
                players.add(new Player("Player 4", money, gui));
                break;
            case 1:
                players.add(new Player("Player 1", money, gui)); // ensure that each player/AI has unique number for ID
                players.add(new Player("Player 2", money, gui));
                players.add(new Player("Player 3", money, gui));
                players.add(new AI("AI 4", money, gui, game, difficulty));
                break;
            case 2:
                players.add(new Player("Player 1", money, gui)); // ensure that each player/AI has unique number for ID
                players.add(new AI("AI 2", money, gui, game, difficulty));
                players.add(new Player("Player 3", money, gui));
                players.add(new AI("AI 4", money, gui, game, difficulty));
                break;
            case 3:
                players.add(new Player("Player 1", money, gui)); // ensure that each player/AI has unique number for ID
                players.add(new AI("AI 2", money, gui, game, difficulty));
                players.add(new AI("AI 3", money, gui, game, difficulty));
                players.add(new AI("AI 4", money, gui, game, difficulty));
                break;
            case 4:
                players.add(new AI("AI 1", money, gui, game, difficulty)); // ensure that each player/AI has unique number for ID
                players.add(new AI("AI 2", money, gui, game, difficulty));
                players.add(new AI("AI 3", money, gui, game, difficulty));
                players.add(new AI("AI 4", money, gui, game, difficulty));
                break;
            default:
                // Handle cases where numOfAiPlayers is not 1, 2, 3, or 4.
                // You might want to throw an exception or provide a default behavior.
                throw new IllegalArgumentException("Invalid number of AI players: " + numOfAiPlayers);
        }
        return players;
    }

    @Override
    public int getNumPlayers(){
        return numPlayers;
    }
    public void setNumPlayers(int numPlayers){
        this.numPlayers = numPlayers;
    }
    public boolean getAllColors(){
        return allColors;
    }
    public void setAllColors(boolean allColors){
        this.allColors = allColors;
    }
    @Override
    public int getNumOfAiPlayers(){
        return numOfAiPlayers;
    }
    public void setNumOfAiPlayers(int numOfAiPlayers){
        this.numOfAiPlayers = numOfAiPlayers;
    }

    public int getCash() {
        return money;
    }

    public void setCash(int cash) {
        this.money = cash;
    }
    @Override
    public void setAIDifficulty (AIDifficulty difficulty) { this.difficulty = difficulty; }


    @Override
    public String getBoardStyle() {
        return boardStyle;
    }
    @Override
    public AIDifficulty getAIDifficulty(){
        return this.difficulty;
    }

    public void setBoardStyle(String style) {
        this.boardStyle = style;
    }
}
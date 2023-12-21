import javax.accessibility.AccessibleIcon;
import java.util.List;

public interface GameFactory {
    Board createBoard(GUI2 gui);
    Board createBoard();
    List<Player> createPlayers(Game game);
    int getNumPlayers();
    void setNumPlayers(int numPlayers);
    int getNumOfAiPlayers();
    void setNumOfAiPlayers(int numOfAiPlayers);
    public AIDifficulty getAIDifficulty();
    public void setAIDifficulty (AIDifficulty difficulty);


    public boolean getAllColors();
    public void setAllColors(boolean allColors);
    int getCash();
    void setCash(int cash);

    String getBoardStyle();
    void setBoardStyle(String style);
}


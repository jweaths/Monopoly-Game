import java.util.Random;
/**
 * The AI class represents an AI player in a game.
 * It extends the Player class and provides additional functionality specific to AI players.
 */
enum AIStrategy {
    AGGRESSIVEBUY, SETPRIORITY, CAUTIOUS
}

enum AIDifficulty {
    EASY, MEDIUM, HARD
}

public class AI extends Player{
    private Random random;
    private AIStrategy currentStrategy;
    private float probabilityMultiplier;
    private AIDifficulty currentDifficulty;
    private Game game;

    public AI(String name, int money, GUI2 gui, Game game, AIDifficulty difficulty) {
        super(name, money, gui);
        this.game = game;
        this.currentDifficulty = difficulty;
        random = new Random();
        int strategy = random.nextInt(3);
        switch (strategy) {
            case 0:
                currentStrategy = AIStrategy.AGGRESSIVEBUY;
                break;
            case 1:
                currentStrategy = AIStrategy.SETPRIORITY;
                break;
            case 2:
                currentStrategy = AIStrategy.CAUTIOUS;
        }
        switch (difficulty) {
            case EASY:
                probabilityMultiplier = 0.5f;
                break;

            case MEDIUM:
                probabilityMultiplier = 0.75f;
                break;

            case HARD:
                probabilityMultiplier = 1.0f;
                break;
        }
    }
    @Override
    public String getType(){
        return "AI";
    }

    @Override
    public boolean makeDecision() {
        float probability;
        float discourage = 0.0f;
        int numOfRicherPlayers = 0;

        if (getMoney() < Game.STARTMONEY / 2) discourage += 0.2f;
        if (getOwnedCities().size() + getOwnedUtilities().size() > 10) discourage += 0.1f;

        for (Player i : Game.players) {
            if (i.getMoney() > this.getMoney()) {
                numOfRicherPlayers++;
            }
        }

        switch (currentStrategy) {
            case AGGRESSIVEBUY -> {
                probability = 0.8f * probabilityMultiplier;
                int propertyCount = getOwnedCities().size() + getOwnedUtilities().size();
                // AI will always purchase if they own less than 3 properties
                if (propertyCount < 3) return true;
                if (propertyCount < 10) probability += 0.1f;
            }
            case SETPRIORITY -> {
                probability = 0.7f * probabilityMultiplier;

                // if AI owns another city of this set
                if (getOnCity() != null && ownsSameColor(getOnCity())) probability += 0.2f;
            }
            case CAUTIOUS -> {
                probability = 0.6f * probabilityMultiplier;

                // if current AI is most poor player in the game
                if (numOfRicherPlayers == game.getNumPlayers() - 1) probability -= 0.2f;
                if (getOnCity() != null && ownsSameColor(getOnCity())) probability += 0.1f;
            }
            default -> probability = 0.5f;
        }

        probability -= discourage;

        return Math.random() < probability;
    }
}


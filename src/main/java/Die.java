import java.util.Random;
/**
 * The Die class represents a pair of dice that can be rolled.
 */
public class Die {
    public int diceOne, diceTwo;
    private Random random;

    public Die() {
        random = new Random();
        diceOne = 6;
        diceTwo = 6;
    }

    public int roll() {
        diceOne = random.nextInt(6) + 1;
        diceTwo = random.nextInt(6) + 1;
        //diceTwo = diceOne;
        return diceOne + diceTwo;
    }

    public int getRoll() {
        return diceOne + diceTwo;
    }

    public boolean isDouble() {
        return diceOne == diceTwo;
    }
}
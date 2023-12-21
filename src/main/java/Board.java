import java.util.ArrayList;
import java.util.List;
/**
 * The Board class represents the game board in the Monopoly game.
 * It contains a list of spaces that make up the board and provides methods
 * to access and initialize the board.
 */
public class Board {
    private List<Space> theBoard;
    private GUI2 gui;
    /**
     * Constructs a Board object and initializes the game board.
     */
    public Board(GUI2 gui) {
        theBoard = new ArrayList<>();
        this.gui = gui;
        initializeBoard();
    }
    /**
     * Initializes the game board by adding spaces in a specific order.
     */
    private void initializeBoard() {
        theBoard.add(0, new OtherSpace("Go!", gui)); // start square
        theBoard.add(1, new City("Mediterranean Avenue", 60, 2, 50, gui, propertyColor.BROWN));
        theBoard.add(2, new ActionCard("Community Chest", gui)); // chance
        theBoard.add(3, new City("Baltic Avenue", 60, 4, 50, gui, propertyColor.BROWN));
        theBoard.add(4, new OtherSpace("Income tax", gui)); // income tax
        theBoard.add(5, new Utility("Reading Railroad", 200, 25, gui));
        theBoard.add(6, new City("Oriental Avenue", 100, 6, 50, gui, propertyColor.LBLUE));
        theBoard.add(7, new ActionCard("Chance", gui));
        theBoard.add(8, new City("Vermont Avenue", 100, 6, 50, gui, propertyColor.LBLUE));
        theBoard.add(9, new City("Connecticut Avenue", 120, 8, 50, gui, propertyColor.LBLUE));
        theBoard.add(10, new OtherSpace("Jail", gui));
        theBoard.add(11, new City("St. Charles Place", 140, 10, 100, gui, propertyColor.PURPLE));
        theBoard.add(12, new Utility("Electrical Company", 150, 4, gui));
        theBoard.add(13, new City("States Avenue", 140, 10, 100, gui, propertyColor.PURPLE));
        theBoard.add(14, new City("Virginia Avenue", 160, 12, 100, gui, propertyColor.PURPLE));
        theBoard.add(15, new Utility("Pennsylvania Railroad", 200, 25, gui));
        theBoard.add(16, new City("St. James Place", 180, 14, 100, gui, propertyColor.ORANGE));
        theBoard.add(17, new ActionCard("Community Chest", gui));
        theBoard.add(18, new City("Tennessee Avenue", 180, 14, 100, gui, propertyColor.ORANGE));
        theBoard.add(19, new City("New York Avenue", 200, 16, 100, gui, propertyColor.ORANGE));
        theBoard.add(20, new OtherSpace("Free Parking", gui));
        theBoard.add(21, new City("Kentucky Avenue", 220, 18, 150, gui, propertyColor.RED));
        theBoard.add(22, new ActionCard("Chance", gui));
        theBoard.add(23, new City("Indiana Avenue", 220, 18, 150, gui, propertyColor.RED));
        theBoard.add(24, new City("Illinois Avenue", 240, 20, 150, gui, propertyColor.RED));
        theBoard.add(25, new Utility("B. & O. Railroad", 200, 25, gui));
        theBoard.add(26, new City("Atlantic Avenue", 260, 22, 150, gui, propertyColor.YELLOW));
        theBoard.add(27, new City("Ventnor Avenue", 260, 22, 150, gui, propertyColor.YELLOW));
        theBoard.add(28, new Utility("Water Works", 150, 4, gui));
        theBoard.add(29, new City("Marvin Gardens", 280, 24, 150, gui, propertyColor.YELLOW));
        theBoard.add(30, new OtherSpace("Go To Jail", gui));
        theBoard.add(31, new City("Pacific Avenue", 300, 26, 200, gui, propertyColor.GREEN));
        theBoard.add(32, new City("North Carolina Avenue", 300, 26, 200, gui, propertyColor.GREEN));
        theBoard.add(33, new ActionCard("Community Chest", gui));
        theBoard.add(34, new City("Pennsylvania Avenue", 320, 28, 200, gui, propertyColor.GREEN));
        theBoard.add(35, new Utility("Short Line", 200, 25, gui));
        theBoard.add(36, new ActionCard("Chance", gui));
        theBoard.add(37, new City("Park Place", 350, 35, 200, gui, propertyColor.DBLUE));
        theBoard.add(38, new OtherSpace("Luxury Tax", gui));
        theBoard.add(39, new City("Boardwalk", 400, 50, 200, gui, propertyColor.DBLUE));
    }

    /**
     * Returns the space at the specified position on the board.
     *
     * @param position The position of the space on the board.
     * @return The space at the specified position.
     */
    public Space getPosition(int position) {
        return theBoard.get(position);
    }

    public Space getSpace(String name) {
        for (Space s : theBoard){
            if (s.name.equals(name)){
                return s;
            }
        }
        return null;
    }
    /**
     * Returns the size of the game board.
     *
     * @return The size of the game board.
     */
    public int getSize() {
        return theBoard.size();
    }
}

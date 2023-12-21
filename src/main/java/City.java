/**
 * The City class represents a city space in a game. It extends the Space class and provides additional
 * functionality specific to cities.
 */

enum propertyColor {
    BROWN, LBLUE, PURPLE, ORANGE, RED, YELLOW, GREEN, DBLUE
}
public class City extends Space {
    public static final int MAXHOUSES = 4;
    private int price;
    private int rent;
    private int houseCost;
    private int numHouses = 0;
    private boolean hasHotel;
    private Player owner;
    private GUI2 gui;
    private propertyColor color;

    public City() {super();}
    public City(String name, int price, int rent, int houseCost, GUI2 gui, propertyColor color) {
        this.isProperty = true;
        this.name = name;
        this.price = price;
        this.rent = rent;
        this.houseCost = houseCost;
        hasHotel = false;
        this.owner = null; // Initially, no one owns the city.
        this.gui = gui;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
    public int getHouseCost() { return houseCost; }
    public int getRent() {
        return rent;
    }
    public int getNumHouses() { return numHouses; }
    public boolean getHasHotel() { return hasHotel; }

    public void incrementNumOfHouses() {
        this.numHouses++;
    }

    public void addHouse() {
        if (numHouses == 0) {
            rent *= 5;
        }
        else {
            rent *= 2;
        }
        numHouses++;
    }

    public void addHotel() {
        if (numHouses == 0) {
            rent *= 80;
        }
        else {
            rent *= 5 - numHouses;
        }

        numHouses = 0;
        hasHotel = true;
    }

    public propertyColor getColor() { return color; }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    // Check if city is available to purchase
    public boolean isAvailable() {
        return owner == null;
    }

    @Override
    public void action(Player player) {
        player.setOnCity(this);
        gui.getTextArea().append(player.getName() + " has landed on: " + this.name);

        // if property is available to be purchased
        if (isAvailable()) {
            if(gui.getTutor())
                gui.getTextArea().append("\nThe Buy City button" +
                        " gives you the option to purchase" +
                        " this city. When your opponents land on your city, they will pay you rent. " +
                        "(amount increases when more buildings are placed.)");
            // Purchase City? option appears on GUI
            if(player.getMoney() >= this.price) {
                player.setOnCity(this);
            }
            else{
                System.out.println("This city is available for purchase at a price of " + price);
                gui.getTextArea().append("This city is available for purchase at a price of " + price + ".");
                System.out.println(player.getName() + " has $" + player.getMoney());
                gui.getTextArea().append(" " + player.getName() + " has $" + player.getMoney() +".");
                System.out.println("Insufficient funds to buy the Property");
                gui.getTextArea().append(" Insufficient funds to buy the Property");
            }

        }
        // if property is owned by another player
        else if (getOwner() != player) {

            gui.getTextArea().setText("This property is owned by: " + owner.getName());
            //player.payRent(rent);
            //owner.receiveRent(rent);
            if(player.getMoney() >= this.rent) {
                int rent = this.rent;

                gui.getTextArea().append("\n" + player.getName() + " must pay " + owner.getName() + " " + rent + "$");
                player.payRent(rent);
                owner.receiveRent(rent);

            }
            else{
                player.transferAssets(getOwner());
                gui.getTextArea().append(player.getName() + " has  $" + player.getMoney() + ". ");

                gui.getTextArea().append("Insufficient funds! The Player is Bankrupted!");
                if(gui.getTutor())
                    gui.getTextArea().append("\nOnce bankrupted, the player is eliminated! Remember, " +
                            "the last player remaining wins");
                int bal = this.price - player.getMoney();
                player.payRent(this.price -bal);
                owner.receiveRent(this.price -bal);
                player.setIsBankrupted(true);
                //Game.gameOver();
            }
        }
        // if property is owned by current player
        else {
            if (player.ownsCurrentSet(this)) {
                if (numHouses < 4) {
                    gui.getTextArea().append("Would you like to build houses on this property?");
                    if(gui.getTutor())
                        gui.getTextArea().append("\n The more houses on the property, the more rent is charged" +
                                "\n once you have four houses on a property you can buy a hotel");
                }

                else {
                    gui.getTextArea().append("Would you like to build a hotel on this property?");
                }
            }

            else {
                gui.getTextArea().setText("You own this property!");
            }
        }
    }
}

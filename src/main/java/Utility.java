/**
 * The Utility class represents a type of property on the game board.
 * It extends the Space class and defines specific actions that occur when a player lands on this type of property.
 */
public class Utility extends Space{
    private int price;
    private int rent;
    private int multiplier;
    private Player owner;
    private GUI2 gui;

    public Utility(String name, int price, int rent, GUI2 gui) {
        this.isProperty = true;
        this.name = name;
        this.price = price;
        this.rent = rent;
        this.owner = null; // Initially, no one owns the city.
        this.gui = gui;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getRent() {
        return rent;
    }

    public Player getOwner() {
        return owner;
    }

    public void increaseMult() {
        multiplier *= 2;
    }

    public void setMult(int mult) {
        multiplier = mult;
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
        player.setOnUtility(this);
        System.out.println("You have landed on: " + this.name);
        gui.getTextArea().append(player.getName() + " has landed on: " + this.name + ".");
        if (isAvailable()) {
            if(gui.getTutor())
                gui.getTextArea().append("\nThe Buy Utility" +
                        " gives you the option to purchase " +
                        "this utility. When your opponents land on it, they must pay you rent. " +
                        "(4x sum of roll if one utility owned, 10x sum of roll if both owned)");
            // Purchase City? option appears on GUI
            if(player.getMoney() >= this.price) {
                player.setOnUtility(this);
            }
            else{

                gui.getTextArea().append(" This utility is available for purchase at a price of " + price + ".");

                gui.getTextArea().append(player.getName() + " has $" + player.getMoney() + ".");


                gui.getTextArea().append("Insufficient funds to buy the Property.");

                if(gui.getTutor())
                    gui.getTextArea().append("\nYou can earn more money by collecting rent, " +
                            "passing go, or drawing community chest cards!");
            }
            /*
            // Purchase City? option appears on the terminal after entering Y/N, the player pieces move
            System.out.println("This utility is available for purchase at a price of " + price);
            if (player.wantToBuyUtility(this)) {
                player.buyUtility(this);
                System.out.println("Congratulations! You have successfully purchased " + this.name);
            } else {
                System.out.println("You chose not to purchase " + this.name);
            }


             */

        }
        else {
            System.out.println("This property is owned by: " + owner.getName());
            gui.getTextArea().setText("This property is owned by: " + owner.getName() + ".");
            // player.payRent(rent);
            // owner.receiveRent(rent);

            if(player.getMoney() >= this.rent) {
                int rent = this.rent;
                System.out.println("Rent to be paid: $" + rent);
                gui.getTextArea().append("\n" + player.getName() + " must pay " + owner.getName() + " " + rent + "$");
                player.payRent(rent);
                owner.receiveRent(rent);
                /*
                System.out.println(player.getName() + " initially has $" + player.getMoney());
                gui.getTextArea().append(player.getName() + " initially has $" + player.getMoney());
                player.payRent(rent);
                owner.receiveRent(rent);
                System.out.println("Amount left after paying rent is: $" + player.getMoney());
                gui.getTextArea().append(" Amount left after paying rent is: $" + player.getMoney());
                System.out.println("After receiving the rent, Owner(" + owner.getName() + ") has $" + owner.getMoney());
                gui.getTextArea().append(". After receiving the rent, Owner(" + owner.getName() + ") has $" + owner.getMoney() + ".");

                 */
            }
            else{
                player.transferAssets(getOwner());
                System.out.println(player.getName() + " has  $" + player.getMoney());
                gui.getTextArea().append(" " + player.getName() + " has  $" + player.getMoney());
                System.out.println("Insufficient funds! The Player is Bankrupted");
                gui.getTextArea().append(". Insufficient funds! The Player is Bankrupted!");
                int bal = this.price - player.getMoney();
                player.payRent(this.price -bal);
                owner.receiveRent(this.price -bal);
                player.setIsBankrupted(true);
                //Game.gameOver();
            }
        }
    }
}

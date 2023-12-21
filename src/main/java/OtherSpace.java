/**
 * The OtherSpace class represents a type of space on the game board that is not a property.
 * It extends the Space class and defines specific actions that occur when a player lands on this type of space.
 */
public class OtherSpace extends Space {

    public static final int GO = 0;
    public static final int INCOMETAX = 4;
    public static final int JAIL = 10;
    public static final int FREEPARKING = 20;
    public static final int GOTOJAIL = 30;
    public static final int LUXTAX = 38;

    private GUI2 gui;

    OtherSpace(String name, GUI2 gui) {
        this.isProperty = false;
        this.name = name;
        this.gui = gui;
    }
    @Override
    public void action(Player player) {
        if (player.getPosition() == GO) {

            gui.getTextArea().append("Go square!");
            if(gui.getTutor())
                gui.getTextArea().append("\nEvery time a player passes or lands on the go square, " +
                        "they get $200!");
        }
        else if (player.getPosition() == INCOMETAX) {

            gui.getTextArea().append("Income Tax!\n");

            gui.getTextArea().append(player.getName() + " must pay $200");

            if(gui.getTutor())
                gui.getTextArea().append("\nYou landed on an tax space. The money you pay will go to " +
                        "the bank!\n");
            player.payRent(200);
        }
        else if (player.getPosition() == JAIL) {
            if (!player.getJailState()) {


                gui.getTextArea().append(player.getName() + " is visiting jail!");
                if(gui.getTutor())
                    gui.getTextArea().append("\nJail rules do not apply when visiting.");
            }
            else {
                if (player.getJailCards() > 0) {

                    gui.getTextArea().append(player.getName() + " uses a get out of jail free card");

                }
                else {
                    // player can roll or pay
                }
            }
        }
        else if (player.getPosition() == FREEPARKING) {

            System.out.println("Free parking!");
            gui.getTextArea().append("Free parking!");
        }
        else if (player.getPosition() == GOTOJAIL) {

            gui.getTextArea().append("Oh no! Go to jail!");
            if(gui.getTutor())
                gui.getTextArea().append("\n Jail Rules"); // need to know specific rules for this
            player.sendToJail();
        } else if (player.getPosition() == LUXTAX) {

            gui.getTextArea().append("Luxury Tax!");
            if(gui.getTutor())
                gui.getTextArea().append("\nYou landed on an tax space. The money you pay will go to " +
                        "the bank! \n");

            gui.getTextArea().append(player.getName() + " Must pay $100");
            player.payRent(100);
        }
    }
}

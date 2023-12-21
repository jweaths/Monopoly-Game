import java.util.Random;

/**
 * The ActionCard class represents an action card in the Monopoly game.
 * It extends the Space class and implements the action method to perform a specific action
 * when a player lands on an action card space.
 */
public class ActionCard extends Space {
    public static final int CC1 = 2;
    public static final int CC2 = 17;
    public static final int CC3 = 33;
    private Random random;

    private GUI2 gui;

    public ActionCard(String name, GUI2 gui) {
        random = new Random();
        this.name = name;
        this.isProperty = false;
        this.gui = gui;
    }
    @Override
    public void action(Player player) {
        if (player.getPosition() == CC1 || player.getPosition() == CC2 || player.getPosition() == CC3) {
            gui.getTextArea().append(player.getName() + " has landed on Community Chest!");
            if(gui.getTutor())
                gui.getTextArea().append("\nCommunity Chest cards involve finances. " +
                        "They will either give you money or take it away!\n");

        }
        else {
            gui.getTextArea().append(player.getName() + " has landed on Chance!");
            if(gui.getTutor())
                gui.getTextArea().append("\nChance Chest cards involve board movement. " +
                        "They can send you somewhere good or bad!\n");

        }
        int cardType = random.nextInt(4);

        switch (cardType) {
            case 0:
                player.setJailCards(player.getJailCards() + 1); // gives player a jail card
                gui.getTextArea().append(player.getName() + " receives 1 get out of jail free card!");
                if(gui.getTutor())
                    gui.getTextArea().append("\nUse these cards to get out of Jail!");

                break;
            case 1:
                player.sendToJail(); // sends player to jail
                gui.getTextArea().append("Oh no! " + player.getName() + " goes to jail!");
                break;
            case 2:
                player.receiveRent(100); // player receives money from the bank
                gui.getTextArea().append(player.getName() + " receives 100$ from the bank! New balance: " + player.getMoney());
                Audio.playAudio("src/main/resources/gotCash.wav");
                break;
            case 3:
                gui.getTextArea().append(player.getName() + " must pay 150$ to the bank! New balance: " + player.getMoney());
                Audio.playAudio("src/main/resources/lostMoney.wav");
                player.payRent(150); // player pays taxes to the bank
                if (player.getIsBankrupted()) player.transferAssetsBank();
                break;
        }
    }
}

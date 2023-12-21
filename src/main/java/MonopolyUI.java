import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MonopolyUI implements PlayerObserver{
    private Game game;
    private JFrame frame;
    private JButton rollDiceButton;
    private JButton buyCityButton;
    private JPanel gameBoardPanel;

    public MonopolyUI(Game game) {
        this.game = game;
        //initializeUI();
    }

/*
    private void initializeUI() {
        frame = new JFrame("Monopoly");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        rollDiceButton = new JButton("Roll Dice");
        rollDiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.rollDiceAndMove();
                updateUI();
            }
        });

        buyCityButton = new JButton("Buy City");
        buyCityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.buyCurrentCity();
                updateUI();
            }
        });


        gameBoardPanel = new JPanel();
        // You should add code to draw the game board on the JPanel here

        frame.getContentPane().add(gameBoardPanel, BorderLayout.CENTER);
        frame.getContentPane().add(rollDiceButton, BorderLayout.NORTH);
        //frame.getContentPane().add(buyCityButton, BorderLayout.SOUTH);
    }

 */

    private void updateUI() {
        // You should add code to update the UI based on the current game state here
    }

    public void showUI() {
        frame.setVisible(true);
    }

    public void onGameOver(){
        // update UI for game over screen
    }

    public void onPlayerState(Player p){
        String name = p.getName();

        if (name.indexOf('1' ) != -1) {
            // update UI for player one

        }else if (name.indexOf('2' ) != -1){
            // update UI for player two
        }
    }

}
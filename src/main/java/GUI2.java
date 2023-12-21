import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

public class GUI2 implements ActionListener , PlayerObserver {

    private boolean tutor = false;

    private int movesMade = 0;
    private JButton buyUtilityButton, endTurnButton, buyCityButton, buyHouseButton, quitButton, buyHotelButton, bailButton, muteButton, saveButton;
    private Point[] boardPositions;
    private static Die die = new Die();
    private boolean isAnimating = false;
    private boolean gameStarted = false;
    private List<JLabel> playerIcons = new ArrayList<>();
    private JButton button;
    private JFrame frame;
    private ImageIcon image;
    private JLayeredPane layeredPane;
    private JLabel diceLabel1;
    private JLabel diceLabel2;
    private boolean enableDiceRollButton = true;

    private PlayerStats p1;
    private PlayerStats p2;
    private PlayerStats p3;
    private PlayerStats p4;
    private PlayerStats PlayerMenuPrototype;


    private JTextArea text;;
    private static final int DISTPLAYERS = 20;
    private static final int DISTCARDS = DISTPLAYERS/4;
    private static final int MOVEUP = -80;
    String black = "black.png";

    String _1 = "1.png";
    String _2 = "2.png";
    String _3 = "3.png";
    String _4 = "4.png";
    String _5 = "5.png";
    String _6 = "6.png";


    public GUI2(boolean tutor) {// initializes panel and frame
        layeredPane = new JLayeredPane(); // create the layered pane
        layeredPane.setPreferredSize(new Dimension(1500, 1000)); // set size

        frame = new JFrame(); // creating frame to add panel onto
        frame.setSize(1500, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(layeredPane);

        this.tutor = tutor; // sets the tutorial mode on or off
    }


    public void setBackdrop(String fileName) {
        image = new ImageIcon(getClass().getResource(fileName));
        JLabel pictureLabel = new JLabel(image);
        pictureLabel.setBounds(435,415+MOVEUP,130, 170);

        layeredPane.add(pictureLabel, new Integer(4));
    }

    public void setOkButton(Game game) {
        Player currentPlayer = game.getCurrentPlayer();

        if (currentPlayer.getJailState() && currentPlayer.getJailCards() > 0) {
            getTextArea().setText(game.getCurrentPlayer().getName() + " uses a Get out of jail free card!");
            currentPlayer.setJailCards(currentPlayer.getJailCards() - 1);
        }
        movesMade++;

        // create quit button, save button, and mute button
        setQuitButton(game);
        setMuteButton();
        setSaveButton(game);

        if (currentPlayer.getType().equals("Player")) {
            if(enableDiceRollButton){
                button = new JButton("Roll");
                button.setBounds(460, 550 + MOVEUP, 80, 25);

                layeredPane.add(button, new Integer(5));

                if (currentPlayer.getJailState()) {
                    setBailButton(game);
                }

                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        removeButtons(game);
                        if (isAnimating) return;

                        boolean exitedJail = true;

                        die.roll();
                        Audio.playAudio("src/main/resources/diceRoll.wav");

                        if (diceLabel1 != null) {
                            layeredPane.remove(diceLabel1);
                            diceLabel1 = null;
                        }
                        if (diceLabel2 != null) {
                            layeredPane.remove(diceLabel2);
                            diceLabel2 = null;
                        }

                        if (currentPlayer.getJailState()) {
                            if (die.isDouble()) {
                                currentPlayer.leaveJail();
                                getTextArea().append(currentPlayer.getName() + " has rolled a doubles! They leave jail");
                            } else {
                                currentPlayer.setTurnsInJail(currentPlayer.getTurnsInJail() + 1);

                                if (currentPlayer.getTurnsInJail() >= 3) {
                                    currentPlayer.setTurnsInJail(0);
                                    currentPlayer.payRent(50);
                                    currentPlayer.leaveJail();
                                    getTextArea().append(currentPlayer.getName() + " has spent 3 turns in jail! They must pay to get out");
                                } else {
                                    exitedJail = false;
                                    getTextArea().setText(currentPlayer.getName() + " failed to roll a doubles! They stay in jail");
                                    removeButtons(game);
                                    layeredPane.remove(button);
                                    button = null;
                                    setEndTurnButton(game);
                                }
                            }
                        }
                        // TO DO: add conditional situation in case player is in jail (they cannot move unless they pay or roll doubles)

                        if (exitedJail) {
                            game.makeMove(die);

                            // Display new dice values
                            displayDice();

                            // logic for movement animation
                            moveOnBoard(game);
                    /*
                    if (buyCityButton != null) {
                        game.cleanProperty();
                        layeredPane.remove(buyCityButton);
                        buyCityButton = null;
                    } else if (buyUtilityButton != null) {
                        game.cleanProperty();
                        layeredPane.remove(buyUtilityButton);
                        buyUtilityButton = null;
                    }

                     */
                            layeredPane.remove(button);
                            button = null;
                            layeredPane.revalidate();
                            layeredPane.repaint();

                            // TO DO: create setButtons function instead of checking for each individually

                            // if current player is not AI

                            // city and utility buttons will be set accordingly
                            if (currentPlayer.getOnCity() != null && currentPlayer.getOnCity().isAvailable())
                                setBuyCityButton(game, die);

                            else if (currentPlayer.getOnUtility() != null && currentPlayer.getOnUtility().isAvailable())
                                setBuyUtilityButton(game, die);

                            // if player owns the entire property set they are on and can afford a house
                            if ((currentPlayer.getOnCity() != null) && ((currentPlayer.ownsCurrentSet(currentPlayer.getOnCity()) && currentPlayer.getMoney() > currentPlayer.getOnCity().getHouseCost()) || (!game.getAllColors() && currentPlayer.getOnCity().getOwner() == currentPlayer))) {

                                // if houses/hotels are still able to be purchased on this property
                                if (currentPlayer.getOnCity().getNumHouses() < City.MAXHOUSES) {
                                    setBuyHouseButton(game);
                                } else {
                                    setBuyHotelButton(game);
                                }
                            }

                            if (die.isDouble() && !currentPlayer.getJailState()) {
                                if (currentPlayer.getConsecutiveMoves() >= 3) {
                                    removeButtons(game);
                                    currentPlayer.sendToJail();
                                    moveOnBoard(game);

                                    Timer timer = new Timer(2000, new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            game.switchTurn();
                                            nextTurn(game);
                                        }
                                    });

                                    timer.setRepeats(false);
                                    timer.start();
                                } else {
                                    //removeButtons(game);
                                    setOkButton(game);
                                }
                            } else {
                                currentPlayer.setConsecutiveMoves(0);
                                // end button will only be created if current player is not AI
                                setEndTurnButton(game);
                            }
                            //removeButtons(game);
                        }
                    }
                });
            }
        }

        else {
            if (endTurnButton != null) {
                layeredPane.remove(endTurnButton);
                endTurnButton = null;
            }
            layeredPane.revalidate();
            frame.repaint();
            nextTurn(game);
        }
    }

    private void removeButtons(Game game) {
        if (buyHouseButton != null) {
            game.cleanProperty();
            layeredPane.remove(buyHouseButton);
            buyHouseButton = null;
        }

        if (buyCityButton != null) {
            game.cleanProperty();
            layeredPane.remove(buyCityButton);
            buyCityButton = null;
        }

        if (buyUtilityButton != null) {
            game.cleanProperty();
            layeredPane.remove(buyUtilityButton);
            buyUtilityButton = null;
        }

        if (buyHotelButton != null) {
            game.cleanProperty();
            layeredPane.remove(buyHotelButton);
            buyHotelButton = null;
        }

        if (bailButton != null) {
            game.cleanProperty();
            layeredPane.remove(bailButton);
            bailButton = null;
        }

        layeredPane.revalidate();
        frame.repaint();
    }

    private void nextTurn(Game game) {
        // clears text box
        //getTextArea().setText("");

        Timer timer = new Timer(1200, new ActionListener() { // delay for 2 seconds
            @Override
            public void actionPerformed(ActionEvent e) {

                if (!game.getCurrentPlayer().getType().equals("Player")) {
                    removeButtons(game);

                    movesMade++;
                    die.roll();
                    if (diceLabel1 != null) {
                        layeredPane.remove(diceLabel1);
                        diceLabel1 = null;
                    }
                    if (diceLabel2 != null) {
                        layeredPane.remove(diceLabel2);
                        diceLabel2 = null;
                    }

                    if (game.getCurrentPlayer().getJailState()) {
                        Audio.playAudio("src/main/resources/lostMoney.wav");
                        game.getCurrentPlayer().payRent(50);

                        if (game.getCurrentPlayer().getMoney() >= 0) {
                            game.getCurrentPlayer().setTurnsInJail(0);
                            game.getCurrentPlayer().leaveJail();
                            getTextArea().append(game.getCurrentPlayer().getName() + " has paid 50$ to leave jail!");
                        }
                    }

                    game.makeMove(die);


                    // Display new dice values
                    displayDice();

                    // logic for movement animation
                    moveOnBoard(game);


                    // TO CHANGE (AI decision making)
//                    if (game.getPrevPlayer().getOnCity() != null) {
//                        game.getPrevPlayer().makeDecision();
//                    }
                    if (game.getCurrentPlayer().getOnCity() != null || game.getCurrentPlayer().getOnUtility() != null) {

                        boolean aiDecision = game.getCurrentPlayer().makeDecision();


                        if (aiDecision && game.getCurrentPlayer().getOnCity() != null) {
                            System.out.println(game.getCurrentPlayer().getName() + " initially has $" + game.getCurrentPlayer().getMoney());
                            game.getCurrentPlayer().buyCity(game.getCurrentPlayer().getOnCity());
                            getTextArea().append("\n" + game.getCurrentPlayer().getName() + " decided to purchase the property");
                            getTextArea().append("\n" + "This city is available for purchase at a price of " + game.getCurrentPlayer().getOnCity().getPrice());
                            getTextArea().append("\n" + "After Purchasing, the balance amount you have is " + game.getCurrentPlayer().getMoney());
                            System.out.println("This city is available for purchase at a price of " + game.getCurrentPlayer().getOnCity().getPrice());
                            System.out.println("After Purchasing, the balance amount you have is " + game.getCurrentPlayer().getMoney());
                            game.cleanProperty();
                        } else if (aiDecision && game.getCurrentPlayer().getOnUtility() != null) {
                            System.out.println(game.getCurrentPlayer().getName() + " initially has $" + game.getCurrentPlayer().getMoney());
                            game.getCurrentPlayer().buyUtility(game.getCurrentPlayer().getOnUtility());
                            //getTextArea().append("\n" + game.getCurrentPlayer()().getName() + " has purchased " + game.getCurrentPlayer()().getOnUtility().name + " for " + game.getCurrentPlayer()().getOnUtility().getPrice() + "$");
                            getTextArea().append("\n" + game.getCurrentPlayer().getName() + " decided to purchase the property");
                            getTextArea().append("\n" + "This utility is available for purchase at a price of " + game.getCurrentPlayer().getOnUtility().getPrice());
                            getTextArea().append("\n" + "After Purchasing, the balance amount you have is " + game.getCurrentPlayer().getMoney());
                            System.out.println("This utility is available for purchase at a price of " + game.getCurrentPlayer().getOnUtility().getPrice());
                            System.out.println("After Purchasing, the balance amount you have is " + game.getCurrentPlayer().getMoney());
                            game.cleanProperty();
                        } else if (!aiDecision && game.getCurrentPlayer().getOnCity() != null) {
                            System.out.println(game.getCurrentPlayer().getName() + " decided not to purchase the property");
                            getTextArea().append("\n" + game.getCurrentPlayer().getName() + " decided not to purchase the property");
                        } else if (!aiDecision && game.getCurrentPlayer().getOnUtility() != null) {
                            System.out.println(game.getCurrentPlayer().getName() + " decided not to purchase the property");
                            getTextArea().append("\n" + game.getCurrentPlayer().getName() + " decided not to purchase the property");
                        }

                    }

                    if (die.isDouble() && !game.getCurrentPlayer().getJailState()) {
                        if (game.getCurrentPlayer().getConsecutiveMoves() >= 3) {
                            System.out.println("Test");
                            game.getCurrentPlayer().sendToJail();
                            moveOnBoard(game);
                            Timer timer3 = new Timer(2000, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    game.switchTurn();
                                    setOkButton(game);
                                }
                            });

                            timer3.setRepeats(false);
                            timer3.start();
                        }

                        else {
                            Timer timer2 = new Timer(2000, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    nextTurn(game);
                                }
                            });
                            timer2.setRepeats(false);
                            timer2.start();
                        }
                    }

                    else {
                        game.getCurrentPlayer().setConsecutiveMoves(0);
                        /*
                        layeredPane.remove(endTurnButton);
                        endTurnButton = null;
                        layeredPane.revalidate();

                         */
                        game.switchTurn();
                        frame.repaint();
                        setOkButton(game);
                    }
                }
            }
        });

        timer.setRepeats(false);
        if (movesMade > 0) timer.start();
        else {
            setOkButton(game);
        }
    }

    public void moveOnBoard(Game game) {
        int currentPlayerPosition = game.getCurrentPlayer().getPosition();

        System.out.println("Sending player to " + currentPlayerPosition);

        JLabel currentPlayerIcon = playerIcons.get(game.getCurrentPlayerIndex());

        Point newPosition = new Point(boardPositions[currentPlayerPosition]);
        int yOffset = 70 / game.getNumPlayers();
        int xOffset = yOffset;
        if (currentPlayerPosition < 11)
            newPosition.y += yOffset * game.getCurrentPlayerIndex();
        else if (currentPlayerPosition < 21)
            newPosition.x -= xOffset * game.getCurrentPlayerIndex();
        else if (currentPlayerPosition < 31)
            newPosition.y -= yOffset * game.getCurrentPlayerIndex();
        else
            newPosition.x += xOffset * game.getCurrentPlayerIndex();
        animateMovement(currentPlayerIcon, newPosition, 15);

        if (game.getCurrentPlayer().getJailState()) {
            System.out.println("Current player: " + game.getCurrentPlayerIndex());
            //System.out.println(game.getPrevPlayer().getPosition());
            game.getCurrentPlayer().setPosition(10);
            newPosition = new Point(boardPositions[game.getCurrentPlayer().getPosition()]);
            //System.out.println(game.getPrevPlayer().getPosition());

            if (currentPlayerPosition < 11)
                newPosition.y += yOffset * game.getCurrentPlayerIndex();
            else if (currentPlayerPosition < 21)
                newPosition.x -= xOffset * game.getCurrentPlayerIndex();
            else if (currentPlayerPosition < 31)
                newPosition.y -= yOffset * game.getCurrentPlayerIndex();
            else
                newPosition.x += xOffset * game.getCurrentPlayerIndex();

            Timer timer = new Timer(1500, null);
            Point finalNewPosition = newPosition;
            timer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    animateMovement(currentPlayerIcon, finalNewPosition, 15);
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    public void setBailButton(Game game) {
        bailButton = new JButton("Pay bail");
        bailButton.setBounds(460, 520 + MOVEUP, 80, 25);
        layeredPane.add(bailButton, new Integer(5));

        bailButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Audio.playAudio("src/main/resources/lostMoney.wav");
                game.getCurrentPlayer().setTurnsInJail(0);
                game.getCurrentPlayer().payRent(50);
                game.getCurrentPlayer().leaveJail();
                getTextArea().append(game.getCurrentPlayer().getName() + " has paid 50$ to leave jail!");

                layeredPane.remove(bailButton);
                frame.repaint();
            }
        });
    }

    public void setMuteButton() {
        muteButton = new JButton("Toggle Music");
        muteButton.setBounds(560, 135, 120, 50);

        layeredPane.add(muteButton, new Integer(5));

        muteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Audio.toggleSound(Audio.bgmClip);
            }
        });
    }

    public void setBuyCityButton(Game game, Die die) {
        buyCityButton = new JButton("Buy City");
        buyCityButton.setBounds(460,520+MOVEUP, 80, 25);

        layeredPane.add(buyCityButton, new Integer(5));

        buyCityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Audio.playAudio("src/main/resources/buyProperty.wav");
                System.out.println(game.getCurrentPlayer().getName() + " initially has $" + game.getCurrentPlayer().getMoney());
                game.getCurrentPlayer().buyCity(game.getCurrentPlayer().getOnCity());
                System.out.println("This city is available for purchase at a price of " + game.getCurrentPlayer().getOnCity().getPrice());
                System.out.println("After Purchasing, the balance amount you have is " + game.getCurrentPlayer().getMoney());
                getTextArea().setText(game.getCurrentPlayer().getName() + " has purchased " + game.getCurrentPlayer().getOnCity().name + " for " + game.getCurrentPlayer().getOnCity().getPrice() + "$");

                if (tutor && !die.isDouble()) getTextArea().append("\nPress the End Turn button to continue.");

                //game.cleanProperty();
                layeredPane.remove(buyCityButton);
                frame.repaint();
            }
        });
    }

    public void setBuyUtilityButton(Game game, Die die) {
        buyUtilityButton = new JButton("Buy Utility");
        buyUtilityButton.setBounds(440,520+MOVEUP, 120, 25);

        layeredPane.add(buyUtilityButton, new Integer(5));

        buyUtilityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Audio.playAudio("src/main/resources/buyProperty.wav");
                System.out.println(game.getCurrentPlayer().getName() + " initially has $" + game.getCurrentPlayer().getMoney());
                game.getCurrentPlayer().buyUtility(game.getCurrentPlayer().getOnUtility());
                System.out.println("This utility is available for purchase at a price of " + game.getCurrentPlayer().getOnUtility().getPrice());
                System.out.println("After Purchasing, the balance amount you have is " + game.getCurrentPlayer().getMoney());
                getTextArea().append("\n" + game.getCurrentPlayer().getName() + " has purchased " + game.getCurrentPlayer().getOnUtility().name + " for " + game.getCurrentPlayer().getOnUtility().getPrice() + "$");

                if (tutor && !die.isDouble()) getTextArea().append("\nPress the End Turn button to continue.");

                game.cleanProperty();
                layeredPane.remove(buyUtilityButton);
                frame.repaint();
            }
        });
    }

    public void setBuyHouseButton(Game game) {
        buyHouseButton = new JButton("Buy house");
        buyHouseButton.setBounds(440, 520 + MOVEUP, 120, 25);

        layeredPane.add(buyHouseButton, new Integer(5));

        buyHouseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Audio.playAudio("src/main/resources/buildHouse.wav");
                game.getCurrentPlayer().buyHouse(game.getCurrentPlayer().getOnCity());

                int possibleHouses = City.MAXHOUSES - game.getCurrentPlayer().getOnCity().getNumHouses();

                int currentPlayerPosition = game.getCurrentPlayer().getPosition();
                Point housePosition = new Point(boardPositions[currentPlayerPosition]);

                // Random adjustments for x and y position within 10 pixels
                Random rand = new Random();
                housePosition.translate(rand.nextInt(21) - 10, rand.nextInt(21) - 10);

                ImageIcon originalHouseIcon = new ImageIcon("src/main/resources/house.png");
                Image scaledHouseImage = originalHouseIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
                ImageIcon scaledHouseIcon = new ImageIcon(scaledHouseImage);

                JLabel houseLabel = new JLabel(scaledHouseIcon);
                houseLabel.setBounds(housePosition.x, housePosition.y, 30, 30);
                layeredPane.add(houseLabel, new Integer(7));

                if (possibleHouses > 0) {
                    layeredPane.remove(buyHouseButton);
                    setBuyHouseButton(game);
                    frame.repaint();
                } else {
                    layeredPane.remove(buyHouseButton);
                    setBuyHotelButton(game);
                    frame.repaint();
                }
            }
        });
    }

    public void setBuyHotelButton(Game game) {
        buyHotelButton = new JButton("Buy hotel");
        buyHotelButton.setBounds(440, 520 + MOVEUP, 120, 25);

        layeredPane.add(buyHotelButton, new Integer(5));

        buyHotelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Audio.playAudio("src/main/resources/buildHouse.wav");

                game.getCurrentPlayer().buyHotel(game.getCurrentPlayer().getOnCity());

                int currentPlayerPosition = game.getCurrentPlayer().getPosition();
                Point housePosition = new Point(boardPositions[currentPlayerPosition]);

                ImageIcon originalIcon = new ImageIcon("src/main/resources/house.png");
                Image scaledImage = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH); // Scaling to 60x60
                ImageIcon scaledIcon = new ImageIcon(scaledImage);

                JLabel houseLabel = new JLabel(scaledIcon);
                houseLabel.setBounds(housePosition.x - 20, housePosition.y - 12, 60, 60);
                layeredPane.add(houseLabel, new Integer(8));

                layeredPane.remove(buyHotelButton);
                frame.repaint();
            }
        });
    }

    public void setEndTurnButton(Game game) {
        endTurnButton = new JButton("End turn");
        endTurnButton.setBounds(440, 550 + MOVEUP, 120, 25);

        layeredPane.add(endTurnButton, new Integer(5));
        endTurnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Test");
                layeredPane.remove(endTurnButton);

                if (buyCityButton != null) {
                    game.cleanProperty();
                    layeredPane.remove(buyCityButton);
                    buyCityButton = null;
                } else if (buyUtilityButton != null) {
                    game.cleanProperty();
                    layeredPane.remove(buyUtilityButton);
                    buyUtilityButton = null;
                }

                endTurnButton = null;
                layeredPane.revalidate();
                frame.repaint();
                game.switchTurn();
                nextTurn(game);
            }
        });
    }

    public void setSaveButton(Game game) {
        saveButton = new JButton("Save");
        saveButton.setBounds(710, 200, 70, 50);
        layeredPane.add(saveButton, new Integer(5));

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (game.getCurrentPlayer().getType().equals("Player") && button == null) {
                    game.switchTurn();
                }
                game.saveGame();
            }
        });
    }

    public void setQuitButton(Game game) {
        quitButton = new JButton("Quit");
        quitButton.setBounds(710, 135, 70, 50);
        layeredPane.add(quitButton, new Integer(5));
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Audio.stopClip(Audio.bgmClip);
                frame.dispose();
                GUI2 gui = new GUI2(true);
                MainMenu menu = new MainMenu(gui);
            }
        });
    }

    // TO DO: create master function to handle button creation
    public void setButtons(Game game) {

    }

    public void displayPoints(Point[] points) {
        for (Point point : points) {
            JLabel pointLabel = new JLabel();
            pointLabel.setOpaque(true);
            pointLabel.setBackground(Color.BLACK);
            pointLabel.setBounds(point.x, point.y, 5, 5);

            layeredPane.add(pointLabel, new Integer(5));
        }
    }

    private void setBoardPositions() {
        int cellsPerSide = 10;
        Point[] positions = new Point[cellsPerSide * 4];
        int boardSize = 670;
        int squareSize = boardSize/cellsPerSide;
        int xDisplacement = 210;
        int yDisplacement = xDisplacement + MOVEUP;

        // Set up positions along the bottom of the board
        for (int i = 0; i < cellsPerSide; i++) {
            positions[i] = new Point(boardSize - (i + 1) * squareSize + xDisplacement, boardSize - squareSize + yDisplacement);
        }
        // Set up positions along the right side of the board
        for (int i = 0; i < cellsPerSide; i++) {
            positions[cellsPerSide + i] = new Point(xDisplacement - squareSize, boardSize - (i + 1) * squareSize + yDisplacement);
        }
        // Set up positions along the top of the board
        for (int i = 0; i < cellsPerSide; i++) {
            positions[2 * cellsPerSide + i] = new Point((i - 1)  * squareSize + xDisplacement, yDisplacement - squareSize);
        }
        // Set up positions along the left side of the board
        for (int i = 0; i < cellsPerSide; i++) {
            positions[3 * cellsPerSide + i] = new Point(boardSize - squareSize + xDisplacement,(i - 1)  * squareSize + yDisplacement);
        }

        boardPositions = positions;
    }


    private void animateMovement(JLabel piece, Point newPosition, int delay) {
        if(isAnimating) return;

        isAnimating = true;

        Timer timer = new Timer(delay, null);
        timer.addActionListener(new ActionListener() {
            int speed = 10;
            @Override
            public void actionPerformed(ActionEvent e) {
                int dx = newPosition.x - piece.getX();
                int dy = newPosition.y - piece.getY();

                if (dx * dx + dy * dy <= speed * speed) {
                    piece.setLocation(newPosition.x, newPosition.y);
                    timer.stop();
                    isAnimating = false;
                    return;
                }

                double angle = Math.atan2(dy, dx);
                int nextX = piece.getX() + (int) (speed * Math.cos(angle));
                int nextY = piece.getY() + (int) (speed * Math.sin(angle));
                piece.setLocation(nextX, nextY);
            }
        });
        timer.start();
    }
    public void displayDice() {
        if(enableDiceRollButton) {
            String dice1 = String.valueOf(die.diceOne) + ".png";
            String dice2 = String.valueOf(die.diceTwo) + ".png";

            ImageIcon originalIcon1 = new ImageIcon(getClass().getResource(dice1));
            ImageIcon originalIcon2 = new ImageIcon(getClass().getResource(dice2));
            Image originalImage1 = originalIcon1.getImage();
            Image originalImage2 = originalIcon2.getImage();
            Image resizedImage1 = originalImage1.getScaledInstance(50, -1, Image.SCALE_SMOOTH);
            Image resizedImage2 = originalImage2.getScaledInstance(50, -1, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon1 = new ImageIcon(resizedImage1);
            ImageIcon resizedIcon2 = new ImageIcon(resizedImage2);
            diceLabel1 = new JLabel(resizedIcon1);
            diceLabel2 = new JLabel(resizedIcon2);

            diceLabel1.setBounds(445, 420 + MOVEUP, 50, 50);
            diceLabel2.setBounds(505, 420 + MOVEUP, 50, 50);

            layeredPane.add(diceLabel1, new Integer(5));
            layeredPane.add(diceLabel2, new Integer(5));

            setBackdrop(black);
        }
    }

    public void initializeTheBoard(Game game) {
        System.out.println("initializingTheBoard");

        setBoardPositions();

        String imagePath = game.getBoardStyle() + ".png";
        System.out.println("Attempting to load image from: " + imagePath);
        System.out.println("Board Style from Factory: " + game.getBoardStyle());
        ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));

        Image image = icon.getImage();
        image = image.getScaledInstance(820, -1, Image.SCALE_SMOOTH);
        icon = new ImageIcon(image);
        JLabel boardImage = new JLabel(icon);

        boardImage.setBounds(0, 0+MOVEUP, 1000, 1000);

        layeredPane.add(boardImage, new Integer(1)); // 1 is lowest layer

        setBackdrop(black);

        displayBackground();
// was here originally
        displayStats(game);

        displayMan(game);

        displayTextBox(game);

        displayTextArea();

        displayCards(5,6);



        displayDice();



        game.subscribeToPlayers(this); // for updating player stats



        //setOkButton(game);
        nextTurn(game);

        displayPlayers(game);
        //setBuildHouseButton(game);
        frame.setVisible(true);
    }

    public void displayPlayers(Game game) {
        System.out.println("adding Players");

        for(int i = 1; i < game.getNumPlayers()+1; i++) {

            ImageIcon originalIcon = new ImageIcon(getClass().getResource("P" + i + ".png"));
            Image originalImage = originalIcon.getImage();
            Image resizedImage = originalImage.getScaledInstance(50, -1, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(resizedImage);

            JLabel playerIcon = new JLabel(resizedIcon);


            playerIcon.setBounds(800 + (i - 1) * DISTPLAYERS, 800 + MOVEUP + (i - 1) * DISTPLAYERS, 50, 50);


            // returns player position from player



            playerIcons.add(playerIcon);

            layeredPane.add(playerIcons.get(i - 1), new Integer(6));

            //////////////////

            //int currentPlayerPosition = game.getCurrentPlayer().getPosition();


            int currentPlayerPosition = game.getPlayer(i-1).getPosition();


            System.out.println("Sending player" +(i)+ " to " + currentPlayerPosition);

            JLabel currentPlayerIcon = playerIcons.get(i-1);



            Point newPosition = new Point(boardPositions[currentPlayerPosition]);


            int yOffset = 70 / game.getNumPlayers();



            int xOffset = yOffset;


            if (currentPlayerPosition < 11)
                newPosition.y += yOffset * (i-1);

            else if (currentPlayerPosition < 21)
                newPosition.x -= xOffset * (i-1);

            else if (currentPlayerPosition < 31)
                newPosition.y -= yOffset * (i-1);

            else
                newPosition.x += xOffset * (i-1);

            currentPlayerIcon.setLocation(newPosition.x, newPosition.y); // sets player location

            if (game.getCurrentPlayer().getJailState()) {
                System.out.println("Current player: " + (i-1));

                game.getCurrentPlayer().setPosition(10);
                newPosition = new Point(boardPositions[game.getCurrentPlayer().getPosition()]);


                if (currentPlayerPosition < 11)
                    newPosition.y += yOffset * game.getCurrentPlayerIndex();
                else if (currentPlayerPosition < 21)
                    newPosition.x -= xOffset * game.getCurrentPlayerIndex();
                else if (currentPlayerPosition < 31)
                    newPosition.y -= yOffset * game.getCurrentPlayerIndex();
                else
                    newPosition.x += xOffset * game.getCurrentPlayerIndex();


            }





        }

        frame.setVisible(true);
    }


    public void displayStats(Game game) {
        System.out.println("initializingTheStatsDisplay");


        for(int i = 1; i < game.getNumPlayers()+1; i++) {

            int hShift = 0; // for display positioning
            int vShift = 0;

            // set bounds for all player menus
            PlayerMenuPrototype = new PlayerStats(i);

            if( i > 2 ) // for setting display position
                vShift = 250;
            else vShift = 0;

            if( i == 2 || i == 4)
                hShift = 200;
            else hShift = 0;

            PlayerMenuPrototype.setStatDisplay(950 + hShift, -50 + vShift, 250, 400);
            layeredPane.add(PlayerMenuPrototype.statDisplay, new Integer(6)); // add to layeredPane on lower layer

            if(i == 1) { // set money1 and so on
                p1 = PlayerMenuPrototype.clone();
                p1.setMoneyBounds(1100 + hShift, 100+vShift, 100, 100 );
                layeredPane.add(p1.getMoneyLabel(), new Integer(7));
                p1.setPropertyBounds(1120 + hShift, 155+vShift, 100, 100);
                layeredPane.add(p1.getPropertyLabel(), new Integer(7));
            }

            else if(i == 2) {
                p2 = PlayerMenuPrototype.clone();
                p2.setMoneyBounds(1100 + hShift, 100+vShift, 100, 100 );
                layeredPane.add(p2.getMoneyLabel(), new Integer(7));
                p2.setPropertyBounds(1120 + hShift, 155+vShift, 100, 100);
                layeredPane.add(p2.getPropertyLabel(), new Integer(7));
            }

            else if(i == 3) {
                p3 = PlayerMenuPrototype.clone();
                p3.setMoneyBounds(1100 + hShift, 100+vShift, 100, 100 );
                layeredPane.add(p3.getMoneyLabel(), new Integer(7));
                p3.setPropertyBounds(1120 + hShift, 155+vShift, 100, 100);
                layeredPane.add(p3.getPropertyLabel(), new Integer(7));
            }

            else if(i == 4) {
                p4 = PlayerMenuPrototype.clone();
                p4.setMoneyBounds(1100 + hShift, 100+vShift, 100, 100 );
                layeredPane.add(p4.getMoneyLabel(), new Integer(7));
                p4.setPropertyBounds(1120 + hShift, 155+vShift, 100, 100);
                layeredPane.add(p4.getPropertyLabel(), new Integer(7));
            }
        }
        frame.setVisible(true); // must come at the very end

    }

    public void displayCards(int chance, int chest) {
        System.out.println("adding cards");

        for(int i = 1; i < chance+1; i++) {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("chance" + ".png"));
            Image originalImage = originalIcon.getImage();
            Image resizedImage = originalImage.getScaledInstance(200, -1, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(resizedImage);

            JLabel chanceIcon = new JLabel(resizedIcon);

            chanceIcon.setBounds(510 + (i - 1) * DISTCARDS, 510 + MOVEUP + (i - 1) * DISTCARDS, 320, 320);

            layeredPane.add(chanceIcon, new Integer(4));
        }

        for(int i = 1; i < chest+1; i++) {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource("chest" + ".png"));
            Image originalImage = originalIcon.getImage();
            Image resizedImage = originalImage.getScaledInstance(150, -1, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(resizedImage);

            JLabel chestIcon = new JLabel(resizedIcon);

            chestIcon.setBounds(170 + (i - 1) * DISTCARDS, 170 + MOVEUP + (i - 1) * DISTCARDS, 250, 250);

            layeredPane.add(chestIcon, new Integer(4));
        }

        frame.setVisible(true);
    }


    public void displayMan(Game game) {
        System.out.println("waking up the monopoly man");

        URL url = getClass().getResource("man.gif");
        ImageIcon originalIcon = new ImageIcon(url); // get StatDisplay image
        Image originalImage = originalIcon.getImage();
        Image resizedImage = originalImage.getScaledInstance(150, 200, Image.SCALE_DEFAULT);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);

        JLabel manDisplay = new JLabel(resizedIcon);
        manDisplay.setBounds(1000, 450, 700, 500);

        //same thing for text box (figure this out tomorrow)
        layeredPane.add(manDisplay, new Integer(5)); // add to layeredPane on lower layer

        ImageIcon originalIcon1 = new ImageIcon(getClass().getResource("text.png")); // get StatDisplay image
        Image originalImage1 = originalIcon.getImage();
        Image resizedImage1 = originalImage.getScaledInstance(150, 200, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon1 = new ImageIcon(resizedImage);

        JLabel textDisplay = new JLabel(resizedIcon);

        textDisplay.setBounds(900, 400, 700, 500);

        // now for text
        frame.setVisible(true);
    }

    public void displayTextBox(Game game) { // textbox picture

        System.out.println("placing text box");

        URL url = getClass().getResource("text.png");
        ImageIcon originalIcon = new ImageIcon(url); // get StatDisplay image
        Image originalImage = originalIcon.getImage();
        Image resizedImage = originalImage.getScaledInstance(350, 200, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);

        JLabel textDisplay = new JLabel(resizedIcon);

        textDisplay.setBounds(750, 400, 700, 500);

        //same thing for text box (figure this out tomorrow)
        layeredPane.add(textDisplay, new Integer(5)); // add to layeredPane on lower layer
    }

    public void displayTextArea() { // text output

        text = new JTextArea();
        text.setBounds(950,555, 300, 125);
        if(tutor)
            text.setText("Welcome to Tutorial Mode! Buy properties to collect rent money " +
                    "from your opponents when they land on a space you own. The game ends " +
                    "when all players except one go bankrupt! \n\n");
        text.append("Press Roll to Begin!");

        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        layeredPane.add(text, new Integer(6)); // add to layeredPane on lower layer
        frame.setVisible(true);
    }

    public JTextArea getTextArea() { // for passing private data member
        return text;
    }
    public boolean getTutor() {
        return tutor;
    }

    public void setTutor(Boolean tutor) { this.tutor = tutor; }

    public void displayBackground() {

        System.out.println("setting backdrop");

        URL url = getClass().getResource("back0.jpeg");
        ImageIcon originalIcon = new ImageIcon(url); // get StatDisplay image
        Image originalImage = originalIcon.getImage();
        Image resizedImage = originalImage.getScaledInstance(1500, 1000, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);

        JLabel backDisplay = new JLabel(resizedIcon);

        backDisplay.setBounds(0, 0, 1500, 1000);

        layeredPane.add(backDisplay, new Integer(1)); // add to layeredPane on lower layer

    }

    public void onGameOver(){
        image = new ImageIcon(getClass().getResource("bankrupcy.png"));
        JLabel pictureLabel = new JLabel(image);
        pictureLabel.setBounds(0,0,frame.getWidth(), frame.getHeight());

        // If you want to display only the Bankruptcy image, uncomment the below line
//        layeredPane.removeAll();


        layeredPane.add(pictureLabel, new Integer(5));

        ImageIcon image2 = new ImageIcon(getClass().getResource("gameOver.png"));
        JLabel pictureLabel2 = new JLabel(image2);
        pictureLabel2.setBounds(435,415+MOVEUP,130, 170);
        layeredPane.add(pictureLabel2, new Integer(6));

        frame.repaint();
        this.enableDiceRollButton = false;
    }

    public void onPlayerState(Player p){
        String name = p.getName();

        if (name.contains("1")) {
            p1.setMoneyLabel(p.getMoney());
            p1.setPropertyLabel(p.getOwnedCities().size() + p.getOwnedUtilities().size());
        }
        else if (name.contains("2")){
            p2.setMoneyLabel(p.getMoney());
            p2.setPropertyLabel(p.getOwnedCities().size() + p.getOwnedUtilities().size());
        }
        else if (name.contains("3")){
            p3.setMoneyLabel(p.getMoney());
            p3.setPropertyLabel(p.getOwnedCities().size() + p.getOwnedUtilities().size());
        }
        else {
            p4.setMoneyLabel(p.getMoney());
            p4.setPropertyLabel(p.getOwnedCities().size() + p.getOwnedUtilities().size());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frame.dispose();
    }
    /*
    public void setBuildHouseButton(Game game) {
        JButton buildHouseButton = new JButton("Build House");
        buildHouseButton.setBounds(440, 550 + MOVEUP, 120, 25);

        layeredPane.add(buildHouseButton, new Integer(5));

        buildHouseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Player currentPlayer = game.getCurrentPlayer();
                City currentCity = currentPlayer.getOnCity();

                if (currentCity != null && currentCity.isAvailable() && currentPlayer.ownsCurrentSet(currentCity)) {
                    int currentHouses = currentCity.getNumHouses();
                    int maxHouses = City.MAXHOUSES;

                    if (currentHouses < maxHouses) {
                        // Allow the player to build a house
                        currentPlayer.buyHouse(currentCity, currentCity.getNumHouses());
                        currentCity.incrementNumOfHouses();

                        // Update the player's stats display
                        onPlayerState(currentPlayer);

                        // Redraw the board to show the newly built house
                        frame.repaint();
                    }
                }
            }
        });
    }

     */



}

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class MainMenu {
    private static JButton newGameButton;
    private static JButton loadGameButton;
    private static JButton settingsButton;
    private static JButton rulesButton;
    private static JButton quitButton;
    private static JFrame frame;
    private static JLayeredPane layeredPane;
    private JLabel bgLabel;
    private GameFactory factory;
    private String tutorialMode = "ON"; // for the drop down
    private GUI2 gui;

    MainMenu(GUI2 gui) {

        this.gui = gui;

        Audio.playAudio("src/main/resources/bgm.wav");
        layeredPane = new JLayeredPane();

        ImageIcon bgIcon = new ImageIcon(getClass().getResource("MainMenu.png"));

        if (bgIcon.getImageLoadStatus() != MediaTracker.COMPLETE) {
            System.out.println("Error loading image.");
        }

        Image bgImage = bgIcon.getImage();
        int imgWidth = (int)(bgImage.getWidth(null) * 1.2);
        int imgHeight = (int)(bgImage.getHeight(null) * 1.2);

        layeredPane.setPreferredSize(new Dimension(imgWidth, imgHeight));

        bgLabel = new JLabel(new ImageIcon(bgImage));
        bgLabel.setBounds(0, 0, imgWidth, imgHeight);
        layeredPane.add(bgLabel, new Integer(0));

        frame = new JFrame();
        frame.setSize(imgWidth, imgHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setResizable(true);


        factory = new CustomGameFactory(4,2,2000,"Classic", gui);
        //defaultSettings();


        if(!gui.getTutor())  // reads boolean and sets string to on or off
            tutorialMode = "OFF";
        else tutorialMode = "ON";

        setNewGameButton(gui);
        setLoadGameButton(gui);
        setSettingsButton();
        setRulesButton();
        setQuitButton();

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 0, 10, 0);

        buttonPanel.add(newGameButton, gbc);
        buttonPanel.add(loadGameButton, gbc);
        buttonPanel.add(settingsButton, gbc);
        buttonPanel.add(rulesButton, gbc);
        buttonPanel.add(quitButton, gbc);

        layeredPane.add(buttonPanel, new Integer(1));

        frame.add(layeredPane, BorderLayout.CENTER);

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                buttonPanel.setBounds(frame.getWidth() / 4, frame.getHeight() / 4, frame.getWidth() / 2, frame.getHeight() / 2);
                bgLabel.setIcon(new ImageIcon(bgImage.getScaledInstance(frame.getWidth(), frame.getHeight(), Image.SCALE_SMOOTH)));
                bgLabel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
            }
        });

        frame.setVisible(true);
    }

    void defaultSettings() {
        factory.setNumPlayers(4);
        factory.setNumOfAiPlayers(2);
        factory.setCash(2000);
        factory.setBoardStyle("Classic");

    }

    public void setNewGameButton(GUI2 a) {
        newGameButton = new JButton("New Game");
        styleButton(newGameButton);
        newGameButton.addActionListener(e -> {
            Audio.playAudio("src/main/resources/mainMenuClick.wav");
            Game game = new Game(factory, a);

            // for now, BGM stops when game is started
            // Audio.stopSound(Audio.bgmClip);

            a.initializeTheBoard(game);
            game.subscribeToPlayers(a);

            frame.dispose();
        });
    }

    public void setLoadGameButton(GUI2 a) {
        loadGameButton = new JButton("Load Game");
        styleButton(loadGameButton);
        loadGameButton.addActionListener(e -> {
            Audio.playAudio("src/main/resources/mainMenuClick.wav");
            try{
                Game game = Game.loadGame(factory, a);
                a.initializeTheBoard(game);
                game.subscribeToPlayers(a);
            } catch (Exception exception){
                System.out.println("Unable to load game " + exception);
            }
            frame.dispose();
        });
    }

    public void setSettingsButton() {
        settingsButton = new JButton("Settings");
        styleButton(settingsButton);
        settingsButton.addActionListener(e -> {
            showSettingsDialog();
            Audio.playAudio("src/main/resources/mainMenuClick.wav");
        });
    }

    public void showSettingsDialog() {
        System.out.println("boardStyleSelection: " + factory.getBoardStyle());
        JDialog settingsDialog = new JDialog();
        settingsDialog.setTitle("Game Settings");
        settingsDialog.setLayout(new BorderLayout());
        settingsDialog.setSize(800, 600);
        settingsDialog.setLocationRelativeTo(null);

        Color darkerBlue = new Color(0, 90, 160);
        Color inputBackground = new Color(0, 102, 204);
        Color inputForeground = Color.WHITE;
        Color focusColor = Color.GRAY;

        Font standardFont = new Font("Dialog", Font.PLAIN, 16);
        Font labelFont = new Font("Dialog", Font.BOLD, 20);

        int currentCash = factory.getCash();
        String currentBoardStyle = factory.getBoardStyle();
        int currentNumOfAiPlayers = factory.getNumOfAiPlayers();

        String[] boardStyles = {
                "Classic", "Bass-Fishing", "Breaking-Bad", "Chtulhu", "Ukraine",
                "Gay", "Ketchup", "Sponge Bob", "Ted Lasso", "David Bowie"
        };

        JComboBox<String> boardStyleSelection = new JComboBox<>(boardStyles);
        boardStyleSelection.setSelectedItem(currentBoardStyle);
        boardStyleSelection.setFont(standardFont);
        boardStyleSelection.setBackground(inputBackground);
        boardStyleSelection.setForeground(inputForeground);
        boardStyleSelection.setPreferredSize(new Dimension(200, 30));
        boardStyleSelection.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                boardStyleSelection.setBackground(focusColor);
            }
            public void focusLost(FocusEvent e) {
                boardStyleSelection.setBackground(inputBackground);
            }
        });

        JTextField cashInput = new JTextField(String.valueOf(currentCash));
        cashInput.setFont(standardFont);
        cashInput.setBackground(inputBackground);
        cashInput.setForeground(inputForeground);
        cashInput.setPreferredSize(new Dimension(200, 30));
        cashInput.setBorder(BorderFactory.createRaisedBevelBorder());
        cashInput.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                cashInput.setBackground(focusColor);
            }
            public void focusLost(FocusEvent e) {
                cashInput.setBackground(inputBackground);
            }
        });

        Integer[] aiPlayerOptions = {0, 1, 2, 3, 4};  // AI players up to 4
        JComboBox<Integer> aiPlayersSelector = new JComboBox<>(aiPlayerOptions);
        aiPlayersSelector.setSelectedItem(currentNumOfAiPlayers);
        aiPlayersSelector.setFont(standardFont);
        aiPlayersSelector.setBackground(inputBackground);
        aiPlayersSelector.setForeground(inputForeground);
        aiPlayersSelector.setPreferredSize(new Dimension(200, 30));
        aiPlayersSelector.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                aiPlayersSelector.setBackground(focusColor);
            }
            public void focusLost(FocusEvent e) {
                aiPlayersSelector.setBackground(inputBackground);
            }
        });

        AIDifficulty[] aiDifficultyOptions = {AIDifficulty.EASY, AIDifficulty.MEDIUM, AIDifficulty.HARD};
        JComboBox<AIDifficulty> aiDifficultySelector = new JComboBox<>(aiDifficultyOptions);
        aiDifficultySelector.setFont(standardFont);
        aiDifficultySelector.setBackground(inputBackground);
        aiDifficultySelector.setForeground(inputForeground);
        aiDifficultySelector.setPreferredSize(new Dimension(200, 30));
        aiDifficultySelector.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                aiDifficultySelector.setBackground(focusColor);
            }
            public void focusLost(FocusEvent e) {
                aiDifficultySelector.setBackground(inputBackground);
            }
        });

        String[] tutorialOptions = {
                "ON", "OFF"
        };


        // tutorial drop down
        JComboBox<String> tutorialSelection = new JComboBox<>(tutorialOptions);
        tutorialSelection.setSelectedItem(tutorialMode);
        tutorialSelection.setFont(standardFont);
        tutorialSelection.setBackground(inputBackground);
        tutorialSelection.setForeground(inputForeground);
        tutorialSelection.setPreferredSize(new Dimension(200, 30));
        tutorialSelection.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                tutorialSelection.setBackground(focusColor);
            }
            public void focusLost(FocusEvent e) {
                tutorialSelection.setBackground(inputBackground);
            }
        });



        JLabel boardStyleLabel = new JLabel("Board Style: ");
        boardStyleLabel.setFont(labelFont);
        boardStyleLabel.setForeground(Color.WHITE);

        JLabel cashLabel = new JLabel("Starting Cash: ");
        cashLabel.setFont(labelFont);
        cashLabel.setForeground(Color.WHITE);

        JLabel aiPlayersLabel = new JLabel("Number of AI Players: ");
        aiPlayersLabel.setFont(labelFont);
        aiPlayersLabel.setForeground(Color.WHITE);

        JLabel tutorialLabel = new JLabel("Tutorial Mode: ");
        tutorialLabel.setFont(labelFont);
        tutorialLabel.setForeground(Color.WHITE);

        JPanel settingsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        settingsPanel.add(boardStyleLabel);
        settingsPanel.add(boardStyleSelection);
        settingsPanel.add(cashLabel);
        settingsPanel.add(cashInput);
        settingsPanel.add(aiPlayersLabel);
        settingsPanel.add(aiPlayersSelector);
        boolean currentAllColors = factory.getAllColors();

        settingsPanel.setBackground(darkerBlue);
        settingsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        AIDifficulty currentDifficulty = factory.getAIDifficulty();
        aiDifficultySelector.setSelectedItem(currentDifficulty);

        JLabel aiDifficultyLabel = new JLabel("AI Difficulty Level: ");
        aiDifficultyLabel.setFont(labelFont);
        aiDifficultyLabel.setForeground(Color.WHITE);

        settingsPanel.add(aiDifficultyLabel);
        settingsPanel.add(aiDifficultySelector);
        settingsPanel.add(tutorialLabel);
        settingsPanel.add(tutorialSelection);

        JCheckBox allColorsCheckbox = new JCheckBox("One colour per property requirement", currentAllColors);
        allColorsCheckbox.setFont(standardFont);
        allColorsCheckbox.setBackground(darkerBlue);
        allColorsCheckbox.setForeground(Color.WHITE);

        settingsPanel.add(new JLabel(""));
        settingsPanel.add(allColorsCheckbox);

        JButton applyButton = new JButton("Apply");
        styleButton(applyButton);
        applyButton.addActionListener(e -> {
            factory.setCash(Integer.parseInt(cashInput.getText()));
            factory.setBoardStyle((String) boardStyleSelection.getSelectedItem());
            factory.setNumOfAiPlayers((Integer) aiPlayersSelector.getSelectedItem());

            gui.setTutor(tutorialSelection.getSelectedItem() != "OFF");

            settingsDialog.dispose();
        });

        applyButton.addActionListener(e -> {
            factory.setCash(Integer.parseInt(cashInput.getText()));
            factory.setBoardStyle((String) boardStyleSelection.getSelectedItem());
            factory.setNumOfAiPlayers((Integer) aiPlayersSelector.getSelectedItem());
            factory.setAIDifficulty((AIDifficulty) aiDifficultySelector.getSelectedItem());
            factory.setAllColors(allColorsCheckbox.isSelected()); // Set the allColors value based on checkbox state
            settingsDialog.dispose();

            gui.setTutor(tutorialSelection.getSelectedItem() != "OFF");

            Audio.playAudio("src/main/resources/mainMenuClick.wav");
        });

        applyButton.addActionListener(e -> {
            factory.setCash(Integer.parseInt(cashInput.getText()));
            factory.setBoardStyle((String) boardStyleSelection.getSelectedItem());
            factory.setNumOfAiPlayers((Integer) aiPlayersSelector.getSelectedItem());
            factory.setAIDifficulty((AIDifficulty) aiDifficultySelector.getSelectedItem());
            settingsDialog.dispose();

            gui.setTutor(tutorialSelection.getSelectedItem() != "OFF");

            Audio.playAudio("src/main/resources/mainMenuClick.wav");
        });

        JButton leaveButton = new JButton("Leave");
        styleButton(leaveButton);
        leaveButton.addActionListener(e -> {
            settingsDialog.dispose();
            Audio.playAudio("src/main/resources/mainMenuClick.wav");
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(applyButton);
        buttonPanel.add(leaveButton);
        buttonPanel.setBackground(darkerBlue);

        settingsDialog.getContentPane().setBackground(darkerBlue);
        settingsDialog.add(settingsPanel, BorderLayout.CENTER);
        settingsDialog.add(buttonPanel, BorderLayout.SOUTH);
        settingsDialog.setVisible(true);
    }

    public void setRulesButton() {
        rulesButton = new JButton("Rules");
        styleButton(rulesButton);
        rulesButton.addActionListener(e -> {
            showRulesDialog();
            Audio.playAudio("src/main/resources/mainMenuClick.wav");
        });
    }

    public void showRulesDialog() {
        JDialog rulesDialog = new JDialog();
        rulesDialog.setTitle("Monopoly Rules");
        rulesDialog.setLayout(new BorderLayout());
        rulesDialog.setSize(800, 600);
        rulesDialog.setLocationRelativeTo(null);

        Color darkerBlue = new Color(0, 90, 160);  // Darker blue color

        JTextArea rulesText = new JTextArea();
        rulesText.setText(
                "Monopoly Game Rules:\n\n" +

                        "• Setup:\n" +
                        "   - Each player receives a token and starts from the 'Go' space.\n" +
                        "   - Default starting amount for players: $2000 (modifiable in settings before starting).\n\n"+

                        "• Number of AI players:\n" +
                        "   - Default number of AI players: 2 (modifiable from 0-4 in settings before starting).\n\n"+

                        "• AI Difficulty:\n" +
                        "   - Default AI difficulty: EASY (modifiable to MEDIUM, or HARD in settings before starting).\n"+
                        "   - AI players' decision-making probabilities are influenced by their chosen difficulty level.\n\n"+

                        "• On Your Turn:\n" +
                        "   - Roll two six-sided dice; the token automatically moves the rolled number of spaces.\n" +
                        "   - Rolling doubles grants an extra turn.\n\n" +

                        "• Buying Property:\n" +
                        "   - Land on an unowned property to purchase it for the listed price on its card.\n" +
                        "   - Opting not to buy leaves the property unowned.\n\n" +

                        "• Paying Rent:\n" +
                        "   - Land on another player's property, pay them rent according to the property's deed card.\n\n" +

                        "• Special Spaces:\n" +
                        "   - 'Go to Jail': Token moves to the Jail space.\n" +
                        "   - 'Free Parking': No action; consider it a free space.\n" +
                        "   - 'Income Tax': Pay the bank $200.\n" +
                        "   - 'Luxury Tax': Pay the bank $100.\n\n" +

                        "• Houses & Hotels:\n" +
                        "   - Own all properties in a color group before purchasing houses or hotels.\n" +
                        "   - Houses must be built evenly; no uneven distribution in a group.\n\n" +

                        "• Going Bankrupt:\n" +
                        "   - Owed amount exceeding your cash results in bankruptcy.\n" +
                        "   - Debt to another player: Give player all remaining money and properties.\n" +
                        "   - Debt to the Bank: Hand over your available money.\n\n" +

                        "• Winning:\n" +
                        "   - The game concludes when a player goes bankrupt.\n" +
                        "   - The player with the highest property and liquid funds wins the Game!.\n\n" +

                        "Notes:\n " +
                        "   - Trading between players is not allowed in this version.\n" +
                        "   - You can only purchase houses/hotels on the property you are currently on."
        );
        rulesText.setWrapStyleWord(true);
        rulesText.setLineWrap(true);
        rulesText.setCaretPosition(0);
        rulesText.setEditable(false);
        rulesText.setFont(new Font("Arial", Font.PLAIN, 16));
        rulesText.setMargin(new Insets(20, 20, 20, 20));
        rulesText.setForeground(Color.WHITE);
        rulesText.setBackground(darkerBlue);

        JScrollPane rulesScrollPane = new JScrollPane(rulesText);
        rulesScrollPane.setBorder(null);
        rulesScrollPane.setViewportBorder(null);
        rulesScrollPane.setBackground(darkerBlue);

        // Styling the scrollbar (remains unchanged)
        JScrollBar scrollBar = rulesScrollPane.getVerticalScrollBar();
        scrollBar.setPreferredSize(new Dimension(15, Integer.MAX_VALUE));
        scrollBar.setBackground(new Color(0, 76, 153));
        scrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(0, 128, 255);
                this.trackColor = new Color(0, 76, 153);
            }
        });

        JButton gotItButton = new JButton("Got It!");
        styleButton(gotItButton);
        gotItButton.addActionListener(e -> {
            rulesDialog.dispose();
            Audio.playAudio("src/main/resources/mainMenuClick.wav");
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(darkerBlue);
        bottomPanel.add(gotItButton);

        rulesDialog.getContentPane().setBackground(darkerBlue);
        rulesDialog.add(rulesScrollPane, BorderLayout.CENTER);
        rulesDialog.add(bottomPanel, BorderLayout.SOUTH);
        rulesDialog.setVisible(true);
    }

    public void setQuitButton() {
        quitButton = new JButton("Quit");
        styleButton(quitButton);
        quitButton.addActionListener(e -> System.exit(0));
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(0, 102, 204));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Dialog", Font.BOLD, 20));

        button.setPreferredSize(new Dimension(320, 50));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
    }
}
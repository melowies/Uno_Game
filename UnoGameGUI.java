//file UnoGameGUI.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UnoGameGUI extends JFrame {
    private Deck deck;
    private Player[] players;
    private Card topCard;
    private int currentPlayerIndex;
    private boolean gameDirection; // true for clockwise, false for counterclockwise
    private Timer timer;

    private JPanel topCardPanel;
    private JPanel playerHandPanel;
    private JPanel[] playerPanels;
    private JLabel topCardLabel;
    private JButton drawButton;
    private JButton playButton;
    private JLabel timerLabel;
    private JLabel lastMoveLabel;
    private JLabel deckCountLabel;
    private JTextArea moveHistoryArea;
    private JButton unoButton;

    private int drawCardsAfterNext;

    public UnoGameGUI() {
        deck = new Deck();
        players = new Player[4];
        players[0] = new Player(false); // user
        for (int i = 1; i < players.length; i++) {
            players[i] = new Player(true); // computers
        }
        initializeGame();


        setTitle("Uno Game");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        topCardPanel = new JPanel(new BorderLayout());
        topCardLabel = new JLabel("Top Card: " + topCard);
        topCardPanel.add(topCardLabel, BorderLayout.NORTH);

        deckCountLabel = new JLabel("Cards left in deck: " + deck.getCardsLeft());
        topCardPanel.add(deckCountLabel, BorderLayout.SOUTH);

        playerHandPanel = new JPanel(new FlowLayout());
        playerHandPanel.setPreferredSize(new Dimension(800, 150));

        JScrollPane playerHandScrollPane = new JScrollPane(playerHandPanel);
        playerHandScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); 
        playerHandScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        drawButton = new JButton("Draw Card");
        drawButton.addActionListener(new DrawCardListener());

        playButton = new JButton("Play Card");
        playButton.addActionListener(new PlayCardListener());

        unoButton = new JButton("Uno!");
        unoButton.addActionListener(new UnoButtonListener());

        timerLabel = new JLabel("Time left: 30");
        lastMoveLabel = new JLabel("Last Move: ");

        JPanel controlPanel = new JPanel();
        controlPanel.add(drawButton);
        controlPanel.add(playButton);
        controlPanel.add(unoButton);
        controlPanel.add(timerLabel);

        playerPanels = new JPanel[4];
        for (int i = 0; i < playerPanels.length; i++) {
            playerPanels[i] = new JPanel();
            playerPanels[i].setBorder(BorderFactory.createTitledBorder("Player " + (i + 1)));
            if (i == 1) {
                topCardPanel.add(playerPanels[i], BorderLayout.CENTER);
            } else if (i == 2) {
                add(playerPanels[i], BorderLayout.WEST);
            } else if (i == 3) {
                add(playerPanels[i], BorderLayout.NORTH);
            }
        }
        

        moveHistoryArea = new JTextArea(5, 20);
        moveHistoryArea.setEditable(false);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(controlPanel, BorderLayout.NORTH);
        bottomPanel.add(playerHandPanel, BorderLayout.CENTER);
        bottomPanel.add(lastMoveLabel, BorderLayout.EAST);
        bottomPanel.add(playerHandScrollPane, BorderLayout.SOUTH);

        JScrollPane moveHistoryScrollPane = new JScrollPane(moveHistoryArea);
        moveHistoryScrollPane.setBorder(BorderFactory.createTitledBorder("Move History"));

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(moveHistoryScrollPane, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(bottomPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);
        add(mainPanel, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
        add(topCardPanel, BorderLayout.CENTER);
        add(moveHistoryScrollPane, BorderLayout.EAST);
        updatePlayerHand();
        updatePlayerPanels();
    }

    private void initializeGame() {
    deck = new Deck(); 
    for (Player player : players) {
        player.clearHand(); 
        for (int i = 0; i < 7; i++) {
            player.drawCard(deck);
        }
    }
    topCard = deck.drawCard();
    currentPlayerIndex = 0;
    gameDirection = true;
    drawCardsAfterNext = 0;
    resetTimer(); 
    }



    private void updatePlayerHand() {
        playerHandPanel.removeAll();
        Player currentPlayer = players[0];
        for (int i = 0; i < currentPlayer.getHandSize(); i++) {
            Card card = currentPlayer.getHand()[i];
            JButton cardButton = new JButton(card.toString());
            cardButton.addActionListener(new PlayCardListener(card));
            playerHandPanel.add(cardButton);
        }
        playerHandPanel.revalidate();
        playerHandPanel.repaint();
    }

    private void updatePlayerPanels() {
        for (int i = 0; i < playerPanels.length; i++) {
            playerPanels[i].removeAll();
            if (i != 0) { 
                for (int j = 0; j < players[i].getHandSize(); j++) {
                    JLabel cardLabel = new JLabel("Card");
                    playerPanels[i].add(cardLabel);
                }
            }
            playerPanels[i].revalidate();
            playerPanels[i].repaint();
        }
        deckCountLabel.setText("Cards left in deck: " + deck.getCardsLeft());
    }


    private void resetTimer() {
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(1000, new ActionListener() {
            int timeLeft = 30;

            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerLabel.setText("Time left: " + timeLeft);
                if (timeLeft <= 0) {
                    timer.stop();
                    autoPlayCard();
                }
            }
        });
        timer.start();
    }

    private void autoPlayCard() {
    Player currentPlayer = players[currentPlayerIndex];

    if (currentPlayer.getHandSize() == 0 && !currentPlayer.hasCalledUno()) {
        currentPlayer.drawMultipleCards(deck, 4); 
        lastMoveLabel.setText("Player " + (currentPlayerIndex + 1) + " didn't call Uno and drew 4 cards");
        moveHistoryArea.append("Player " + (currentPlayerIndex + 1) + " didn't call Uno and drew 4 cards\n");
        if (currentPlayer.getPlayableCard(topCard) == null) {
            currentPlayerIndex = nextPlayerIndex();
            updatePlayerPanels();
            if (players[currentPlayerIndex].isComputer()) {
                computerPlay(); 
            } else {
                updatePlayerHand();
                resetTimer();
            }
            return;
        }
    }

    Card playableCard = currentPlayer.getPlayableCard(topCard);
    if (playableCard != null) {
        currentPlayer.playCard(playableCard, deck);
        topCard = playableCard;
        topCardLabel.setText("Top Card: " + topCard);
        lastMoveLabel.setText("Last Move: Player " + (currentPlayerIndex + 1) + " played " + playableCard);
        moveHistoryArea.append("Player " + (currentPlayerIndex + 1) + " played " + playableCard + "\n");
        handleSpecialCards(playableCard);
    }else {
        currentPlayer.drawCard(deck);
        lastMoveLabel.setText("Last Move: Player " + (currentPlayerIndex + 1) + " drew a card");
        moveHistoryArea.append("Player " + (currentPlayerIndex + 1) + " drew a card\n");
    }
    
    updateDeckCount();
    if (currentPlayer.getHandSize() == 0 && !currentPlayer.hasCalledUno()) {
        displayRankings();
        return;
    }
    currentPlayerIndex = nextPlayerIndex();
    updatePlayerPanels();
    if (players[currentPlayerIndex].isComputer()) {
        computerPlay();
    } else {
        updatePlayerHand();
        resetTimer();
    }
}

    private void computerPlay() {
        resetTimer();
        Player currentPlayer = players[currentPlayerIndex];
        Card playableCard = currentPlayer.getPlayableCard(topCard);
        if (playableCard != null) {
            currentPlayer.playCard(playableCard, deck);
            topCard = playableCard;
            topCardLabel.setText("Top Card: " + topCard);
            lastMoveLabel.setText("Last Move: Player " + (currentPlayerIndex + 1) + " played " + playableCard);
            moveHistoryArea.append("Player " + (currentPlayerIndex + 1) + " played " + playableCard + "\n");
            updateDeckCount();
            handleSpecialCards(playableCard);
            showCardForDuration(playableCard);
        } else {
            currentPlayer.drawCard(deck);
            lastMoveLabel.setText("Last Move: Player " + (currentPlayerIndex + 1) + " drew a card");
            moveHistoryArea.append("Player " + (currentPlayerIndex + 1) + " drew a card\n");
            updateDeckCount();
        }
        if (currentPlayer.getHandSize() == 0) {
            displayRankings();
            return;
        }
        currentPlayerIndex = nextPlayerIndex();
        updatePlayerPanels();
        if (players[currentPlayerIndex].isComputer()) {
            computerPlay();
        } else {
            updatePlayerHand();
            resetTimer();
        }
    }

    private void handleSpecialCards(Card card) {
        if (card.getColor() == Card.Color.WILD) {
            if (players[currentPlayerIndex].isComputer()) {
                Card.Color selectedColor = Card.Color.values()[(int) (Math.random() * 4)];
                topCard = new Card(selectedColor, topCard.getValue());
                topCardLabel.setText("Top Card: " + topCard); 
            } else {
                selectWildColor();
            }
        }
        if (card.getValue() == Card.Value.DRAW_TWO) {
            drawCardsAfterNext += 2;
        } else if (card.getValue() == Card.Value.WILD_DRAW_FOUR) {
            drawCardsAfterNext += 4;
        } else if (card.getValue() == Card.Value.REVERSE) {
            gameDirection = !gameDirection;
        } else if (card.getValue() == Card.Value.SKIP) {
            currentPlayerIndex = nextPlayerIndex();
        }
    }

    private void selectWildColor() {
        String[] colors = {"Red", "Yellow", "Green", "Blue"};
        String selectedColor = (String) JOptionPane.showInputDialog(this, "Choose a color:", "Wild Card Color",
                JOptionPane.PLAIN_MESSAGE, null, colors, colors[0]);
        if (selectedColor != null) {
            topCard = new Card(Card.Color.valueOf(selectedColor.toUpperCase()), topCard.getValue());
            topCardLabel.setText("Top Card: " + topCard); 
        } else {
            topCard = new Card(Card.Color.RED, topCard.getValue()); 
            topCardLabel.setText("Top Card: " + topCard); 
        }
    }
    

    private void updateDeckCount() {
        deckCountLabel.setText("Cards left in deck: " + deck.getCardsLeft());
    }

    private void showCardForDuration(Card card) {
        Timer showCardTimer = new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        showCardTimer.setRepeats(false);
        showCardTimer.start();
    }

    private class DrawCardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Player currentPlayer = players[currentPlayerIndex];
            currentPlayer.drawCard(deck);
            updateDeckCount();
            lastMoveLabel.setText("Last Move: Player " + (currentPlayerIndex + 1) + " drew a card");
            moveHistoryArea.append("Player " + (currentPlayerIndex + 1) + " drew a card\n");
            drawCardsAfterNext++;
            currentPlayerIndex = nextPlayerIndex();
            updatePlayerPanels();
            if (players[currentPlayerIndex].isComputer()) {
                computerPlay();
            } else {
                updatePlayerHand();
                resetTimer();
            }
        }
    }

    private class PlayCardListener implements ActionListener {
        private Card cardToPlay;

        public PlayCardListener() {
        }

        public PlayCardListener(Card card) {
            this.cardToPlay = card;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Player currentPlayer = players[currentPlayerIndex];
            if (cardToPlay != null && cardToPlay.getColor() != null) {
                if (cardToPlay.getColor() == topCard.getColor() || cardToPlay.getValue() == topCard.getValue() || cardToPlay.getColor() == Card.Color.WILD) {
                    currentPlayer.playCard(cardToPlay, deck);
                    topCard = cardToPlay;
                    topCardLabel.setText("Top Card: " + topCard);
                    lastMoveLabel.setText("Last Move: Player " + (currentPlayerIndex + 1) + " played " + cardToPlay);
                    moveHistoryArea.append("Player " + (currentPlayerIndex + 1) + " played " + cardToPlay + "\n");
                    handleSpecialCards(cardToPlay);
                    if (currentPlayer.getHandSize() == 0) {
                        displayRankings();
                        return;
                    }
                    currentPlayerIndex = nextPlayerIndex();
                    updatePlayerPanels();
                    if (players[currentPlayerIndex].isComputer()) {
                        computerPlay();
                    } else {
                        updatePlayerHand();
                        resetTimer();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid card choice. Play a card matching the color or value of the top card.");
                }
            }
        }
    }

    private int nextPlayerIndex() {
        if (deck.getCardsLeft() == 0) {
            displayRankings();
            return currentPlayerIndex;
        }
        
        if (gameDirection) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
        } else {
            currentPlayerIndex = (currentPlayerIndex - 1 + players.length) % players.length;
        }
        
        int nextIndex = currentPlayerIndex;
    
        if (drawCardsAfterNext > 0) {
            players[nextIndex].drawMultipleCards(deck, drawCardsAfterNext);
            drawCardsAfterNext = 0;
            updateDeckCount();
        }
        return nextIndex;
    }
    
    

    private void displayRankings() {
        StringBuilder rankings = new StringBuilder("Game Over! Rankings:\n");
        for (int i = 0; i < players.length; i++) {
            rankings.append("Player ").append(i + 1).append(": ").append(players[i].getHandSize()).append(" cards\n");
        }
        JOptionPane.showMessageDialog(null, rankings.toString());
        int option = JOptionPane.showConfirmDialog(null, "Do you want to play again?", "Play Again", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            initializeGame();
            updatePlayerHand();
            updatePlayerPanels();
            resetTimer();
        } else {
            System.exit(0);
        }
    }

    private class UnoButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Player currentPlayer = players[currentPlayerIndex];
            if (currentPlayer.getHandSize() == 1) {
                currentPlayer.setCalledUno(true);
                lastMoveLabel.setText("Player " + (currentPlayerIndex + 1) + " called Uno!");
                moveHistoryArea.append("Player " + (currentPlayerIndex + 1) + " called Uno!\n");
            } else {
                JOptionPane.showMessageDialog(null, "You can only call Uno when you have exactly one card left!");
            }
        }
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UnoGameGUI game = new UnoGameGUI();
            game.setVisible(true);
        });
    }
}
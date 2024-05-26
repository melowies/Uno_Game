//file Deck.java
import java.util.Random;

class Deck {
    private static final int TOTAL_CARDS = 108;
    private Card[] cards;
    private int currentCardIndex;

    public Deck() {
        cards = new Card[TOTAL_CARDS];
        initializeDeck();
        shuffle();
    }

    private void initializeDeck() {
        int index = 0;
        for (Card.Color color : Card.Color.values()) {
            if (color == Card.Color.WILD) {
                for (int i = 0; i < 4; i++) {
                    cards[index++] = new Card(color, Card.Value.WILD);
                    cards[index++] = new Card(color, Card.Value.WILD_DRAW_FOUR);
                }
            } else {
                for (Card.Value value : Card.Value.values()) {
                    if (value != Card.Value.WILD && value != Card.Value.WILD_DRAW_FOUR) {
                        cards[index++] = new Card(color, value);
                        if (value != Card.Value.ZERO) {
                            cards[index++] = new Card(color, value);
                        }
                    }
                }
            }
        }
        currentCardIndex = 0;
    }

    public void shuffle() {
        Random random = new Random();
        for (int i = 0; i < cards.length; i++) {
            int j = random.nextInt(cards.length);
            Card temp = cards[i];
            cards[i] = cards[j];
            cards[j] = temp;
        }
    }

    public Card drawCard() {
        if (currentCardIndex >= TOTAL_CARDS) {
            return null;
        }
        return cards[currentCardIndex++];
    }

    public int getCardsLeft() {
        return TOTAL_CARDS - currentCardIndex;
    }

    public void returnCard(Card card) {
        if (currentCardIndex > 0) {
            cards[--currentCardIndex] = card;
        }
    }
}

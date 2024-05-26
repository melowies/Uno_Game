
public class Player {
    private boolean isComputer;
    private Card[] hand;
    private int handSize;
    private boolean calledUno;

    public Player(boolean isComputer) {
        this.isComputer = isComputer;
        this.hand = new Card[108]; 
        this.handSize = 0;
        this.calledUno = false;
    }

    public void drawCard(Deck deck) {
        if (handSize < hand.length) {
            hand[handSize++] = deck.drawCard();
        }
    }

    public void drawMultipleCards(Deck deck, int count) {
        for (int i = 0; i < count; i++) {
            drawCard(deck);
        }
    }

    public Card getPlayableCard(Card topCard) {
        for (int i = 0; i < handSize; i++) {
            Card card = hand[i];
            if (card.getColor() == topCard.getColor() || card.getValue() == topCard.getValue() || card.getColor() == Card.Color.WILD) {
                return card;
            }
        }
        return null;
    }

    public void playCard(Card card, Deck deck) {
        for (int i = 0; i < handSize; i++) {
            if (hand[i] == card) {
                handSize--;
                System.arraycopy(hand, i + 1, hand, i, handSize - i);
                break;
            }
        }
        if (handSize == 1 && !calledUno) {
            // player didn't call Uno
            drawMultipleCards(deck, 4);
        }
        calledUno = false; 
    }

    public Card[] getHand() {
        return hand;
    }

    public int getHandSize() {
        return handSize;
    }

    public boolean isComputer() {
        return isComputer;
    }

    public boolean hasCalledUno() {
        return calledUno;
    }

    public void setCalledUno(boolean calledUno) {
        this.calledUno = calledUno;
    }
}

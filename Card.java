//file Card.java
class Card {
    public enum Color { RED, GREEN, BLUE, YELLOW, WILD }
    
    public enum Value {
        ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, 
        DRAW_TWO, SKIP, REVERSE, WILD, WILD_DRAW_FOUR
    }

    private final Color color;
    private final Value value;

    public Card(Color color, Value value) {
        this.color = color;
        this.value = value;
    }

    public Color getColor() {
        return color;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return color + " " + value;
    }
}

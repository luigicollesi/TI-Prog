package server.game;

public class Card {
    private final String rank;
    private final String suit;

    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public String rank() {
        return rank;
    }

    public String suit() {
        return suit;
    }

    @Override
    public String toString() {
        return rank + "-" + suit;
    }
}

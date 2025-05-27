import java.util.Objects;

/**
 * Representa uma carta de baralho para o jogo de Blackjack.
 */
public class Card {
    public enum Suit { HEARTS, DIAMONDS, CLUBS, SPADES }
    public enum Rank {
        TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6),
        SEVEN(7), EIGHT(8), NINE(9), TEN(10),
        JACK(10), QUEEN(10), KING(10), ACE(11);

        private final int value;
        Rank(int v) { this.value = v; }
        public int getValue() { return value; }
    }

    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card other = (Card) o;
        return suit == other.suit && rank == other.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(suit, rank);
    }
}

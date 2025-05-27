import java.util.*;

public class Deck {
    private final Deque<Card> cards;

    public Deck() {
        List<Card> temp = new ArrayList<>();
        for (Card.Suit s : Card.Suit.values())
            for (Card.Rank r : Card.Rank.values())
                temp.add(new Card(s, r));
        Collections.shuffle(temp);
        cards = new ArrayDeque<>(temp);
    }

    public Card draw() {
        if (cards.isEmpty())
            throw new IllegalStateException("Deck is empty");
        return cards.poll();
    }
}

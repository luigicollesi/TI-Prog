import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Representa um baralho de 52 cartas, com métodos para
 * embaralhar e comprar (draw).
 */
public class Deck {
    private final Deque<Card> cards;

    public Deck() {
        // Inicializa e embaralha
        List<Card> temp = new ArrayList<>();
        for (Card.Suit s : Card.Suit.values()) {
            for (Card.Rank r : Card.Rank.values()) {
                temp.add(new Card(s, r));
            }
        }
        Collections.shuffle(temp);
        cards = new ArrayDeque<>(temp);
    }

    /**
     * Compra uma carta do topo do baralho.
     * @throws IllegalStateException se o baralho estiver vazio.
     */
    public Card draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Deck is empty");
        }
        return cards.poll();
    }

    /** 
     * Retorna quantas cartas restam no baralho. 
     */
    public int remaining() {
        return cards.size();
    }

    /**
     * Reembaralha todas as cartas (útil para novas rodadas).
     */
    public void reset() {
        List<Card> temp = new ArrayList<>(cards);
        temp.addAll(cards); // assume-se que você guardou as cartas usadas em outro lugar
        Collections.shuffle(temp);
        cards.clear();
        cards.addAll(temp);
    }
}

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um jogador de Blackjack com cartas viradas para cima e para baixo.
 */
public class Player {
    private final String name;
    private final List<Card> faceUp   = new ArrayList<>();
    private final List<Card> faceDown = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Adiciona carta visível (face para cima).
     */
    public void addFaceUp(Card c) {
        faceUp.add(c);
    }

    /**
     * Adiciona carta oculta (face para baixo).
     */
    public void addFaceDown(Card c) {
        faceDown.add(c);
    }

    /**
     * Retorna a melhor pontuação possível (Ás conta como 1 ou 11).
     */
    public int getBestScore() {
        List<Card> all = new ArrayList<>(faceUp);
        all.addAll(faceDown);

        int sum = 0;
        int aces = 0;
        for (Card c : all) {
            sum += c.getRank().getValue();
            if (c.getRank() == Card.Rank.ACE) aces++;
        }
        while (sum > 21 && aces > 0) {
            sum -= 10; // converte um Ás de 11 para 1
            aces--;
        }
        return sum;
    }

    /**
     * Indica se o jogador ultrapassou 21.
     */
    public boolean isBusted() {
        return getBestScore() > 21;
    }

    /**
     * Retorna todas as cartas, visíveis e ocultas.
     */
    public List<Card> getAllCards() {
        List<Card> all = new ArrayList<>(faceUp);
        all.addAll(faceDown);
        return all;
    }

    /**
     * Limpa as mãos para iniciar novo jogo ou rodada.
     */
    public void clearHand() {
        faceUp.clear();
        faceDown.clear();
    }

    @Override
    public String toString() {
        return name;
    }
}

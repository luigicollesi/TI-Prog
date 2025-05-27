import java.util.List;

public interface BlackjackUI {
    void displayMessage(String msg);
    void displayFaceUpCard(Player p, Card c);
    void displayNewCard(Player p, Card c);
    boolean askHitOrStand(Player p); // true = comprar
    void revealHand(Player p, List<Card> allCards, int score);
}

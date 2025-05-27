import java.util.*;

public class Player {
    private final String name;
    private final List<Card> faceUp   = new ArrayList<>();
    private final List<Card> faceDown = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }
    public String getName() { return name; }

    public void addFaceUp(Card c)   { faceUp.add(c); }
    public void addFaceDown(Card c) { faceDown.add(c); }

    public int getBestScore() {
        List<Card> all = new ArrayList<>();
        all.addAll(faceUp);
        all.addAll(faceDown);
        int sum = all.stream().mapToInt(c -> c.getRank().getValue()).sum();
        long aces = all.stream().filter(c -> c.getRank() == Card.Rank.ACE).count();
        while (sum > 21 && aces > 0) {
            sum -= 10;  // conta Ás como 1 em vez de 11
            aces--;
        }
        return sum;
    }

    public boolean isBusted() {
        return getBestScore() > 21;
    }

    public List<Card> getAllCards() {
        List<Card> all = new ArrayList<>(faceUp);
        all.addAll(faceDown);
        return all;
    }
}

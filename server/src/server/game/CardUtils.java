package server.game;

import java.util.List;

public final class CardUtils {
    private CardUtils() {}

    public static int computeTotal(List<Card> hand) {
        int total = 0;
        int aces = 0;

        for (Card card : hand) {
            String rank = card.rank();
            switch (rank) {
                case "J":
                case "Q":
                case "K":
                    total += 10;
                    break;
                case "A":
                    total += 11;
                    aces++;
                    break;
                default:
                    total += Integer.parseInt(rank);
            }
        }

        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }
        return total;
    }
}

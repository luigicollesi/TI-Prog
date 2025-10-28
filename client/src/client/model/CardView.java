package client.model;

public class CardView {
    private final String rank;
    private final String suit;
    private final boolean hidden;

    public CardView(String rank, String suit, boolean hidden) {
        this.rank = rank;
        this.suit = suit;
        this.hidden = hidden;
    }

    public String getRank() {
        return rank;
    }

    public String getSuit() {
        return suit;
    }

    public boolean isHidden() {
        return hidden;
    }
}

package server.db;

public class HistoryEntry {
    private final Boolean won;
    private final int value;
    private final String playerCardsJson;
    private final String dealerCardsJson;
    private final String createdAt;

    public HistoryEntry(Boolean won, int value, String playerCardsJson, String dealerCardsJson, String createdAt) {
        this.won = won;
        this.value = value;
        this.playerCardsJson = playerCardsJson;
        this.dealerCardsJson = dealerCardsJson;
        this.createdAt = createdAt;
    }

    public Boolean won() {
        return won;
    }

    public int value() {
        return value;
    }

    public String playerCardsJson() {
        return playerCardsJson;
    }

    public String dealerCardsJson() {
        return dealerCardsJson;
    }

    public String createdAt() {
        return createdAt;
    }
}

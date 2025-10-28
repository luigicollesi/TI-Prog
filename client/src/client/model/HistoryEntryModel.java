package client.model;

public class HistoryEntryModel {
    private final Boolean won;
    private final int value;
    private final String playerCardsJson;
    private final String dealerCardsJson;
    private final String createdAt;

    public HistoryEntryModel(Boolean won, int value, String playerCardsJson, String dealerCardsJson, String createdAt) {
        this.won = won;
        this.value = value;
        this.playerCardsJson = playerCardsJson;
        this.dealerCardsJson = dealerCardsJson;
        this.createdAt = createdAt;
    }

    public Boolean getWon() {
        return won;
    }

    public boolean isWin() {
        return Boolean.TRUE.equals(won);
    }

    public boolean isLoss() {
        return Boolean.FALSE.equals(won);
    }

    public boolean isDraw() {
        return won == null;
    }

    public int getValue() {
        return value;
    }

    public String getPlayerCardsJson() {
        return playerCardsJson;
    }

    public String getDealerCardsJson() {
        return dealerCardsJson;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}

package client.model;

import java.util.List;

public class PlayerView {
    private final String userId;
    private final String username;
    private final int balance;
    private final int bet;
    private final String status;
    private final List<CardView> cards;

    public PlayerView(String userId, String username, int balance, int bet, String status, List<CardView> cards) {
        this.userId = userId;
        this.username = username;
        this.balance = balance;
        this.bet = bet;
        this.status = status;
        this.cards = cards;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getBalance() {
        return balance;
    }

    public int getBet() {
        return bet;
    }

    public String getStatus() {
        return status;
    }

    public List<CardView> getCards() {
        return cards;
    }
}

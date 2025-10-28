package server.game;

import server.net.ClientHandler;

import java.util.ArrayList;
import java.util.List;

public class PlayerState {
    private final ClientHandler handler;
    private final List<Card> hand = new ArrayList<>();
    private int bet;
    private boolean standing;
    private boolean busted;

    public PlayerState(ClientHandler handler) {
        this.handler = handler;
    }

    public String getUserId() {
        return handler.getUserId();
    }

    public String getUsername() {
        return handler.getUsername();
    }

    public int getBalance() {
        return handler.getBalance();
    }

    public void setBalance(int value) {
        handler.updateBalance(value);
    }

    public ClientHandler getHandler() {
        return handler;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void clearHand() {
        hand.clear();
        standing = false;
        busted = false;
    }

    public void addCard(Card card) {
        hand.add(card);
        if (CardUtils.computeTotal(hand) > 21) {
            busted = true;
            standing = true;
        }
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public boolean isStanding() {
        return standing;
    }

    public void stand() {
        standing = true;
    }

    public boolean isBusted() {
        return busted;
    }

    public void setBusted(boolean busted) {
        this.busted = busted;
    }

    public int total() {
        return CardUtils.computeTotal(hand);
    }
}

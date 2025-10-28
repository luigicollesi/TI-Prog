package client.model;

import java.util.Collections;
import java.util.List;

public class DealSequence {
    private final String firstTurnUserId;
    private final List<CardDeal> dealerCards;
    private final List<PlayerDeal> playerDeals;

    public DealSequence(String firstTurnUserId, List<CardDeal> dealerCards, List<PlayerDeal> playerDeals) {
        this.firstTurnUserId = firstTurnUserId;
        this.dealerCards = dealerCards;
        this.playerDeals = playerDeals;
    }

    public String getFirstTurnUserId() {
        return firstTurnUserId;
    }

    public List<CardDeal> getDealerCards() {
        return Collections.unmodifiableList(dealerCards);
    }

    public List<PlayerDeal> getPlayerDeals() {
        return Collections.unmodifiableList(playerDeals);
    }

    public static class CardDeal {
        private final String rank;
        private final String suit;
        private final boolean faceUp;

        public CardDeal(String rank, String suit, boolean faceUp) {
            this.rank = rank;
            this.suit = suit;
            this.faceUp = faceUp;
        }

        public String getRank() {
            return rank;
        }

        public String getSuit() {
            return suit;
        }

        public boolean isFaceUp() {
            return faceUp;
        }
    }

    public static class PlayerDeal {
        private final String playerId;
        private final List<CardDeal> cards;

        public PlayerDeal(String playerId, List<CardDeal> cards) {
            this.playerId = playerId;
            this.cards = cards;
        }

        public String getPlayerId() {
            return playerId;
        }

        public List<CardDeal> getCards() {
            return Collections.unmodifiableList(cards);
        }
    }
}

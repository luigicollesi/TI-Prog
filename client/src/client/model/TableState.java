package client.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TableState {
    private final TableStage stage;
    private final String currentTurnUserId;
    private final boolean dealerRevealed;
    private final List<CardView> dealerCards;
    private final List<PlayerView> players;

    public TableState(TableStage stage, String currentTurnUserId, boolean dealerRevealed,
                      List<CardView> dealerCards, List<PlayerView> players) {
        this.stage = stage;
        this.currentTurnUserId = currentTurnUserId;
        this.dealerRevealed = dealerRevealed;
        this.dealerCards = dealerCards;
        this.players = players;
    }

    public TableStage getStage() {
        return stage;
    }

    public String getCurrentTurnUserId() {
        return currentTurnUserId;
    }

    public boolean isDealerRevealed() {
        return dealerRevealed;
    }

    public List<CardView> getDealerCards() {
        return Collections.unmodifiableList(dealerCards);
    }

    public List<PlayerView> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Optional<PlayerView> findPlayer(String userId) {
        return players.stream()
            .filter(p -> p.getUserId().equals(userId))
            .findFirst();
    }
}

package server.game;

import server.db.DatabaseOperations;
import server.net.ClientHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class Table {
    public enum Stage {
        WAITING,
        BETTING,
        PLAYING,
        RESULTS
    }

    private final String id;
    private final Deck deck = new Deck();
    private final List<Card> dealerHand = new ArrayList<>();
    private final List<PlayerState> players = new ArrayList<>();

    private Stage stage = Stage.WAITING;
    private int currentTurnIndex = -1;
    private boolean dealerRevealed = false;

    public Table(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public synchronized boolean hasSeat() {
        return players.size() < 3 && stage != Stage.PLAYING;
    }

    public synchronized void addPlayer(ClientHandler handler) {
        PlayerState state = new PlayerState(handler);
        players.add(state);
        if (stage == Stage.WAITING) {
            stage = Stage.BETTING;
        }
        broadcastMessage("JOINED;" + id + ";" + handler.getUserId() + ";" + seatIndex(handler));
        sendTableState();
    }

    public synchronized void removePlayer(ClientHandler handler) {
        Iterator<PlayerState> iterator = players.iterator();
        boolean removed = false;
        PlayerState removedState = null;
        while (iterator.hasNext()) {
            PlayerState state = iterator.next();
            if (state.getHandler() == handler) {
                iterator.remove();
                removed = true;
                removedState = state;
                break;
            }
        }

        if (!removed) {
            return;
        }

        if (stage == Stage.BETTING && removedState != null && removedState.getBet() > 0) {
            handler.adjustBalance(removedState.getBet());
        }

        broadcastMessage("LEFT;" + handler.getUserId());

        if (players.isEmpty()) {
            resetTable();
        } else {
            if (stage == Stage.PLAYING && players.stream().noneMatch(p -> !p.isStanding())) {
                finishRound();
            } else {
                sendTableState();
            }
        }
    }

    public synchronized void placeBet(ClientHandler handler, int amount) {
        Optional<PlayerState> opt = findPlayer(handler);
        if (opt.isEmpty()) {
            handler.sendMessage("ERROR;Você não está na mesa.");
            return;
        }

        PlayerState player = opt.get();

        if (stage != Stage.BETTING) {
            handler.sendMessage("ERROR;Aguarde o fim da rodada para apostar novamente.");
            return;
        }

        if (player.getBet() > 0) {
            handler.sendMessage("ERROR;Aposta já registrada.");
            return;
        }

        if (amount <= 0) {
            handler.sendMessage("ERROR;Valor de aposta inválido.");
            return;
        }

        if (handler.getBalance() < amount) {
            handler.sendMessage("ERROR;Saldo insuficiente para aposta.");
            return;
        }

        handler.adjustBalance(-amount);
        player.setBet(amount);

        sendTableState();

        if (allPlayersReadyToStart()) {
            startRound();
        }
    }

    public synchronized void handleAction(ClientHandler handler, String action) {
        if (stage != Stage.PLAYING) {
            handler.sendMessage("ERROR;A rodada ainda não começou.");
            return;
        }

        if (currentTurnIndex < 0 || currentTurnIndex >= players.size()) {
            handler.sendMessage("ERROR;Turno inválido.");
            return;
        }

        PlayerState currentPlayer = players.get(currentTurnIndex);
        if (currentPlayer.getHandler() != handler) {
            handler.sendMessage("ERROR;Aguarde a sua vez.");
            return;
        }

        switch (action) {
            case "HIT":
                currentPlayer.addCard(deck.draw());
                sendTableState();

                if (currentPlayer.isBusted()) {
                    handler.sendMessage("MESSAGE;Você estourou!");
                    advanceTurn();
                }
                break;

            case "STAND":
                currentPlayer.stand();
                advanceTurn();
                break;

            default:
                handler.sendMessage("ERROR;Ação desconhecida.");
        }
    }

    private synchronized void startRound() {
        stage = Stage.PLAYING;
        dealerRevealed = false;
        deck.reset();
        dealerHand.clear();
        dealerHand.add(deck.draw());
        dealerHand.add(deck.draw());

        for (PlayerState player : players) {
            player.clearHand();
            player.addCard(deck.draw());
            player.addCard(deck.draw());
        }

        currentTurnIndex = 0;
        broadcastDealSequence();
        sendTableState();
    }

    private void advanceTurn() {
        do {
            currentTurnIndex++;
        } while (currentTurnIndex < players.size() && players.get(currentTurnIndex).isStanding());

        if (currentTurnIndex >= players.size()) {
            finishRound();
        } else {
            sendTableState();
        }
    }

    private synchronized void finishRound() {
        stage = Stage.RESULTS;
        dealerRevealed = true;
        playDealerHand();

        int dealerTotal = CardUtils.computeTotal(dealerHand);

        sendTableState();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for (PlayerState player : players) {
            resolveOutcome(player, dealerTotal);
        }

        prepareNextRound();
    }

    private void playDealerHand() {
        while (CardUtils.computeTotal(dealerHand) < 17) {
            dealerHand.add(deck.draw());
        }
    }

    private void resolveOutcome(PlayerState player, int dealerTotal) {
        int playerTotal = player.total();
        int bet = player.getBet();
        String playerCardsJson = cardsToJson(player.getHand());
        String dealerCardsJson = cardsToJson(dealerHand);

        Boolean won = null;
        int amountChange = 0;
        String message;

        if (player.isBusted()) {
            won = false;
            message = "Você perdeu. Total: " + playerTotal + ".";
        } else if (dealerTotal > 21 || playerTotal > dealerTotal) {
            won = true;
            boolean blackjack = playerTotal == 21 && player.getHand().size() == 2;
            double multiplier = blackjack ? 2.5 : 2.0;
            int prize = (int) Math.round(bet * multiplier);
            amountChange = prize;
            player.setBalance(player.getBalance() + prize);
            message = blackjack ? "Blackjack! +" + prize : "Você venceu! +" + prize;
        } else if (dealerTotal > playerTotal) {
            won = false;
            message = "Dealer venceu com " + dealerTotal + ".";
        } else {
            amountChange = bet;
            player.setBalance(player.getBalance() + bet);
            message = "Empate. Aposta devolvida.";
        }

        int recordedValue = Boolean.TRUE.equals(won) ? amountChange : bet;

        try {
            DatabaseOperations.insertHistory(
                player.getUserId(),
                recordedValue,
                playerCardsJson,
                dealerCardsJson,
                won
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        player.getHandler().sendMessage("ROUND_RESULT;" + message + ";" + player.getBalance());
    }

    private void broadcastDealSequence() {
        String turnId = players.isEmpty() ? "" : players.get(currentTurnIndex).getUserId();
        String dealerData = formatDealerSequence();
        String playerData = formatPlayerSequence();
        String message = "DEAL_SEQUENCE;" + turnId + ";" + dealerData + ";" + playerData;
        broadcastMessage(message);
    }

    private String formatDealerSequence() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < dealerHand.size(); i++) {
            if (i > 0) builder.append("|");
            Card card = dealerHand.get(i);
            boolean faceUp = (i == 0);
            builder.append(card.rank())
                   .append(":")
                   .append(card.suit())
                   .append(":")
                   .append(faceUp ? "1" : "0");
        }
        return builder.toString();
    }

    private String formatPlayerSequence() {
        if (players.isEmpty()) {
            return "";
        }

        List<PlayerState> ordered = new ArrayList<>(players);
        if (currentTurnIndex > 0) {
            Collections.rotate(ordered, -currentTurnIndex);
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ordered.size(); i++) {
            if (i > 0) builder.append("|");
            PlayerState player = ordered.get(i);
            builder.append(player.getUserId()).append("=");

            List<Card> hand = player.getHand();
            int limit = Math.min(hand.size(), 2);
            for (int j = 0; j < limit; j++) {
                if (j > 0) builder.append(",");
                Card card = hand.get(j);
                boolean faceUp = (j == 0);
                if (j == 1) faceUp = false;
                builder.append(card.rank())
                       .append(":")
                       .append(card.suit())
                       .append(":")
                       .append(faceUp ? "1" : "0");
            }
        }
        return builder.toString();
    }

    private void prepareNextRound() {
        for (PlayerState player : players) {
            player.setBet(0);
            player.clearHand();
        }
        dealerHand.clear();
        deck.reset();
        stage = Stage.BETTING;
        currentTurnIndex = -1;
        dealerRevealed = false;
        sendTableState();
    }

    private boolean allPlayersReadyToStart() {
        return !players.isEmpty() && players.stream().allMatch(p -> p.getBet() > 0);
    }

    private Optional<PlayerState> findPlayer(ClientHandler handler) {
        return players.stream().filter(p -> p.getHandler() == handler).findFirst();
    }

    private int seatIndex(ClientHandler handler) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getHandler() == handler) {
                return i;
            }
        }
        return -1;
    }

    private void broadcastMessage(String message) {
        for (PlayerState player : players) {
            player.getHandler().sendMessage(message);
        }
    }

    private void sendTableState() {
        StringBuilder builder = new StringBuilder();
        builder.append("TABLE_STATE;");
        builder.append(stage.name()).append(";");
        builder.append(currentTurnIndex >= 0 && currentTurnIndex < players.size()
            ? players.get(currentTurnIndex).getUserId()
            : "NONE");
        builder.append(";");
        builder.append(dealerRevealed ? "1" : "0").append(";");
        builder.append(cardsAsString(dealerHand, dealerRevealed)).append(";");
        builder.append(players.size());

        for (PlayerState player : players) {
            builder.append(";");
            builder.append(player.getUserId()).append(",");
            builder.append(player.getUsername()).append(",");
            builder.append(player.getBalance()).append(",");
            builder.append(player.getBet()).append(",");
            builder.append(playerStatus(player)).append(",");
            builder.append(cardsAsString(player.getHand(), shouldRevealPlayer(player)));
        }

        broadcastMessage(builder.toString());
    }

    private String playerStatus(PlayerState player) {
        if (stage == Stage.BETTING && player.getBet() == 0) {
            return "BETTING";
        }
        if (player.isBusted()) {
            return "BUST";
        }
        if (player.isStanding()) {
            return "STAND";
        }
        if (stage == Stage.RESULTS) {
            return "RESULT";
        }
        return "PLAYING";
    }

    private boolean shouldRevealPlayer(PlayerState player) {
        if (stage == Stage.RESULTS) {
            return true;
        }
        if (stage != Stage.PLAYING) {
            return true;
        }
        int index = players.indexOf(player);
        if (index < 0) {
            return true;
        }
        if (player.isStanding() || player.isBusted()) {
            return true;
        }
        return index <= currentTurnIndex;
    }

    private String cardsAsString(List<Card> cards, boolean revealAll) {
        if (cards.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < cards.size(); i++) {
            if (!revealAll && i == 1) {
                sb.append("HIDDEN");
            } else {
                sb.append(cards.get(i).toString());
            }
            if (i < cards.size() - 1) {
                sb.append("|");
            }
        }
        return sb.toString();
    }

    private String cardsToJson(List<Card> cards) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            sb.append("[\"")
              .append(card.rank())
              .append("\",\"")
              .append(card.suit())
              .append("\"]");
            if (i < cards.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private void resetTable() {
        stage = Stage.WAITING;
        currentTurnIndex = -1;
        dealerHand.clear();
        deck.reset();
    }

    public synchronized boolean isEmpty() {
        return players.isEmpty();
    }
}

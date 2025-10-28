package client.net;

import client.model.CardView;
import client.model.DealSequence;
import client.model.DealSequence.CardDeal;
import client.model.DealSequence.PlayerDeal;
import client.model.HistoryEntryModel;
import client.model.PlayerView;
import client.model.TableStage;
import client.model.TableState;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerConnection {
    private final Socket socket;
    private final PrintStream output;
    private final Scanner input;

    private final List<ServerListener> listeners = new CopyOnWriteArrayList<>();

    private String userId;
    private String username;
    private int balance;

    private final List<HistoryEntryModel> historyBuffer = new ArrayList<>();
    private boolean bufferingHistory = false;

    public ServerConnection(String host, int port) throws IOException {
        socket = new Socket(host, port);
        output = new PrintStream(socket.getOutputStream(), true);
        input = new Scanner(socket.getInputStream());
        Thread listenerThread = new Thread(this::listenLoop, "ServerConnection-Listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void addListener(ServerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ServerListener listener) {
        listeners.remove(listener);
    }

    public void login(String username, String password) {
        sendCommand("LOGIN;" + username + ";" + password);
    }

    public void register(String username, String password) {
        sendCommand("REGISTER;" + username + ";" + password);
    }

    public void joinTable() {
        sendCommand("JOIN_TABLE");
    }

    public void placeBet(int amount) {
        sendCommand("BET;" + amount);
    }

    public void sendAction(String action) {
        sendCommand("ACTION;" + action);
    }

    public void requestHistory() {
        sendCommand("HISTORY");
    }

    public void logout() {
        sendCommand("LOGOUT");
    }

    public void deleteAccount() {
        sendCommand("DELETE_ACCOUNT");
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

    private void sendCommand(String command) {
        output.println(command);
    }

    private void listenLoop() {
        try {
            while (input.hasNextLine()) {
                String line = input.nextLine().trim();
                handleLine(line);
            }
        } finally {
            notifyConnectionClosed();
        }
    }

    private void handleLine(String line) {
        if (line.isEmpty()) {
            return;
        }

        String[] parts = line.split(";", -1);
        String command = parts[0];

        switch (command) {
            case "LOGIN_OK":
                handleLoginOk(parts);
                break;
            case "LOGIN_FAIL":
                notifyLoginFailure(parts.length > 1 ? parts[1] : "Falha no login.");
                break;
            case "REGISTER_OK":
                notifyRegisterResult(true, "Conta criada com sucesso.");
                break;
            case "REGISTER_FAIL":
                notifyRegisterResult(false, parts.length > 1 ? parts[1] : "Falha ao criar conta.");
                break;
            case "JOIN_ACK":
                notifyJoinAcknowledged(parts);
                break;
            case "DEAL_SEQUENCE":
                handleDealSequence(parts);
                break;
            case "TABLE_STATE":
                notifyTableState(parts);
                break;
            case "ERROR":
                notifyError(parts.length > 1 ? parts[1] : "Erro desconhecido.");
                break;
            case "MESSAGE":
                notifyInfo(parts.length > 1 ? parts[1] : "");
                break;
            case "JOINED":
            case "LEFT":
                // Atualizações de presença refletem no TABLE_STATE; nenhum tratamento adicional necessário aqui.
                break;
            case "ROUND_RESULT":
                notifyRoundResult(parts);
                break;
            case "BALANCE":
                handleBalanceUpdate(parts);
                break;
            case "HISTORY_BEGIN":
                startHistoryBuffer();
                break;
            case "HISTORY_ENTRY":
                bufferHistoryEntry(parts);
                break;
            case "HISTORY_END":
                flushHistory();
                break;
            case "LOGOUT_OK":
                notifyLogout();
                break;
            case "ACCOUNT_DELETED":
                notifyAccountDeleted();
                break;
            case "ACCOUNT_DELETE_FAIL":
                notifyError(parts.length > 1 ? parts[1] : "Falha ao excluir conta.");
                break;
            default:
                notifyInfo("Recebido: " + line);
        }
    }

    private void handleLoginOk(String[] parts) {
        if (parts.length < 4) {
            notifyLoginFailure("Resposta inválida do servidor.");
            return;
        }
        userId = parts[1];
        username = parts[2];
        balance = parseIntSafe(parts[3]);
        runOnUi(() -> listeners.forEach(l -> l.onLoginSuccess(userId, username, balance)));
    }

    private void notifyLoginFailure(String message) {
        runOnUi(() -> listeners.forEach(l -> l.onLoginFailure(message)));
    }

    private void notifyRegisterResult(boolean success, String message) {
        runOnUi(() -> listeners.forEach(l -> l.onRegisterResult(success, message)));
    }

    private void notifyJoinAcknowledged(String[] parts) {
        if (parts.length < 3) {
            return;
        }
        balance = parseIntSafe(parts[2]);
        runOnUi(() -> listeners.forEach(l -> l.onJoinAcknowledged(parts[1], balance)));
    }

    private void handleDealSequence(String[] parts) {
        DealSequence sequence = parseDealSequence(parts);
        if (sequence == null) {
            return;
        }
        runOnUi(() -> listeners.forEach(l -> l.onDealSequence(sequence)));
    }

    private void notifyTableState(String[] parts) {
        TableState state = parseTableState(parts);
        if (state != null) {
            runOnUi(() -> listeners.forEach(l -> l.onTableState(state)));
        }
    }

    private void notifyError(String message) {
        runOnUi(() -> listeners.forEach(l -> l.onErrorMessage(message)));
    }

    private void notifyInfo(String message) {
        runOnUi(() -> listeners.forEach(l -> l.onInfoMessage(message)));
    }

    private void notifyRoundResult(String[] parts) {
        if (parts.length < 3) {
            return;
        }
        balance = parseIntSafe(parts[2]);
        String message = parts[1];
        runOnUi(() -> listeners.forEach(l -> l.onRoundResult(message, balance)));
    }

    private void handleBalanceUpdate(String[] parts) {
        if (parts.length < 2) {
            return;
        }
        balance = parseIntSafe(parts[1]);
        runOnUi(() -> listeners.forEach(l -> l.onBalanceUpdate(balance)));
    }

    private void startHistoryBuffer() {
        historyBuffer.clear();
        bufferingHistory = true;
    }

    private void bufferHistoryEntry(String[] parts) {
        if (!bufferingHistory || parts.length < 6) {
            return;
        }
        Boolean won = null;
        if ("1".equalsIgnoreCase(parts[1])) {
            won = Boolean.TRUE;
        } else if ("0".equalsIgnoreCase(parts[1])) {
            won = Boolean.FALSE;
        }
        int value = parseIntSafe(parts[2]);
        HistoryEntryModel entry = new HistoryEntryModel(won, value, parts[3], parts[4], parts[5]);
        historyBuffer.add(entry);
    }

    private void flushHistory() {
        if (!bufferingHistory) {
            return;
        }
        List<HistoryEntryModel> copy = Collections.unmodifiableList(new ArrayList<>(historyBuffer));
        bufferingHistory = false;
        runOnUi(() -> listeners.forEach(l -> l.onHistoryLoaded(copy)));
    }

    private void notifyLogout() {
        userId = null;
        username = null;
        balance = 0;
        runOnUi(() -> listeners.forEach(ServerListener::onLogout));
    }

    private void notifyAccountDeleted() {
        userId = null;
        username = null;
        balance = 0;
        historyBuffer.clear();
        bufferingHistory = false;
        runOnUi(() -> listeners.forEach(ServerListener::onAccountDeleted));
    }

    private void notifyConnectionClosed() {
        runOnUi(() -> listeners.forEach(ServerListener::onConnectionClosed));
    }

    private DealSequence parseDealSequence(String[] parts) {
        if (parts.length < 3) {
            return null;
        }

        String turnId = parts[1];
        List<CardDeal> dealer = parseSequenceCards(parts[2]);
        List<PlayerDeal> players = new ArrayList<>();

        if (parts.length >= 4 && parts[3] != null && !parts[3].isBlank()) {
            String[] playerTokens = parts[3].split("\\|");
            for (String token : playerTokens) {
                if (token.isBlank()) continue;
                String[] split = token.split("=", 2);
                if (split.length == 0 || split[0].isBlank()) continue;
                String playerId = split[0];
                List<CardDeal> cards = split.length > 1 ? parseSequenceCards(split[1]) : List.of();
                players.add(new PlayerDeal(playerId, cards));
            }
        }

        return new DealSequence(turnId, dealer, players);
    }

    private List<CardDeal> parseSequenceCards(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        String[] tokens = value.contains(",") ? value.split(",") : value.split("\\|");
        List<CardDeal> cards = new ArrayList<>(tokens.length);
        for (String token : tokens) {
            if (token.isBlank()) continue;
            String[] parts = token.split(":", 3);
            if (parts.length < 3) continue;
            String rank = parts[0];
            String suit = parts[1];
            boolean faceUp = "1".equals(parts[2]);
            cards.add(new CardDeal(rank, suit, faceUp));
        }
        return cards;
    }

    private TableState parseTableState(String[] parts) {
        if (parts.length < 6) {
            return null;
        }
        try {
            TableStage stage = TableStage.valueOf(parts[1]);
            String currentTurn = parts[2];
            if ("NONE".equalsIgnoreCase(currentTurn) || currentTurn.isBlank()) {
                currentTurn = null;
            }
            boolean dealerRevealed = "1".equals(parts[3]);
            List<CardView> dealerCards = parseCards(parts[4]);

            int playerCount = parseIntSafe(parts[5]);
            List<PlayerView> players = new ArrayList<>();

            for (int i = 0; i < playerCount; i++) {
                int index = 6 + i;
                if (index >= parts.length) {
                    break;
                }
                String[] tokens = parts[index].split(",", -1);
                if (tokens.length < 6) {
                    continue;
                }
                String id = tokens[0];
                String name = tokens[1];
                int playerBalance = parseIntSafe(tokens[2]);
                int bet = parseIntSafe(tokens[3]);
                String status = tokens[4];
                List<CardView> cards = parseCards(tokens[5]);
                players.add(new PlayerView(id, name, playerBalance, bet, status, cards));
            }

            return new TableState(stage, currentTurn, dealerRevealed, dealerCards, players);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private List<CardView> parseCards(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        String[] tokens = value.split("\\|");
        List<CardView> cards = new ArrayList<>();
        for (String token : tokens) {
            if ("HIDDEN".equals(token)) {
                cards.add(new CardView("", "", true));
            } else {
                String[] parts = token.split("-", 2);
                if (parts.length == 2) {
                    cards.add(new CardView(parts[0], parts[1], false));
                }
            }
        }
        return cards;
    }

    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void runOnUi(Runnable runnable) {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }
}

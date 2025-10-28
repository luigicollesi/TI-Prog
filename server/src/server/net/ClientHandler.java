package server.net;

import server.db.DatabaseOperations;
import server.db.HistoryEntry;
import server.db.UserData;
import server.game.Table;
import server.game.TableManager;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final BlackjackServer server;
    private final TableManager tableManager;

    private Scanner input;
    private PrintStream output;

    private final AtomicBoolean running = new AtomicBoolean(true);

    private UserData userData;
    private int balance;
    private Table table;

    public ClientHandler(Socket socket, BlackjackServer server, TableManager tableManager) {
        this.socket = socket;
        this.server = server;
        this.tableManager = tableManager;
    }

    @Override
    public void run() {
        try {
            input = new Scanner(socket.getInputStream());
            output = new PrintStream(socket.getOutputStream(), true);

            while (running.get() && input.hasNextLine()) {
                String line = input.nextLine().trim();
                if (!line.isEmpty()) {
                    handleCommand(line);
                }
            }
        } catch (IOException e) {
            // conexão encerrada
        } finally {
            cleanup();
        }
    }

    private void handleCommand(String line) {
        String[] parts = line.split(";", -1);
        String command = parts[0].toUpperCase();

        try {
            switch (command) {
                case "LOGIN":
                    handleLogin(parts);
                    break;
                case "REGISTER":
                    handleRegister(parts);
                    break;
                case "JOIN_TABLE":
                    handleJoinTable();
                    break;
                case "BET":
                    handleBet(parts);
                    break;
                case "ACTION":
                    handleAction(parts);
                    break;
                case "HISTORY":
                    handleHistory();
                    break;
                case "LOGOUT":
                    handleLogout();
                    break;
                case "DELETE_ACCOUNT":
                    handleDeleteAccount();
                    break;
                default:
                    sendMessage("ERROR;Comando desconhecido.");
            }
        } catch (Exception e) {
            sendMessage("ERROR;" + e.getMessage());
        }
    }

    private void handleLogin(String[] parts) throws SQLException {
        if (parts.length < 3) {
            sendMessage("LOGIN_FAIL;Dados insuficientes.");
            return;
        }
        if (userData != null) {
            sendMessage("LOGIN_FAIL;Usuário já autenticado.");
            return;
        }

        String username = parts[1];
        String password = parts[2];

        UserData data = DatabaseOperations.authenticate(username, password);
        if (data == null) {
            sendMessage("LOGIN_FAIL;Credenciais inválidas.");
            return;
        }

        userData = data;
        balance = data.getBalance();
        sendMessage("LOGIN_OK;" + data.getId() + ";" + data.getUsername() + ";" + balance);
    }

    private void handleRegister(String[] parts) throws SQLException {
        if (parts.length < 3) {
            sendMessage("REGISTER_FAIL;Dados insuficientes.");
            return;
        }
        if (userData != null) {
            sendMessage("REGISTER_FAIL;Já autenticado.");
            return;
        }

        String username = parts[1];
        String password = parts[2];

        if (username.isBlank() || username.length() > 15) {
            sendMessage("REGISTER_FAIL;Nome inválido.");
            return;
        }
        if (password.length() < 4 || password.length() > 50) {
            sendMessage("REGISTER_FAIL;Senha inválida.");
            return;
        }

        if (DatabaseOperations.usernameExists(username)) {
            sendMessage("REGISTER_FAIL;Nome já utilizado.");
            return;
        }

        boolean created = DatabaseOperations.createUser(username, password);
        if (!created) {
            sendMessage("REGISTER_FAIL;Não foi possível criar a conta.");
            return;
        }

        sendMessage("REGISTER_OK");
    }

    private void handleJoinTable() {
        if (!isAuthenticated()) {
            sendMessage("ERROR;Faça login primeiro.");
            return;
        }

        if (table != null) {
            sendMessage("ERROR;Você já está em uma mesa.");
            return;
        }

        table = tableManager.assignTable(this);
        sendMessage("JOIN_ACK;" + table.getId() + ";" + balance);
    }

    private void handleBet(String[] parts) {
        if (!isAuthenticated()) {
            sendMessage("ERROR;Autenticação necessária.");
            return;
        }

        if (table == null) {
            sendMessage("ERROR;Entre em uma mesa antes de apostar.");
            return;
        }

        if (parts.length < 2) {
            sendMessage("ERROR;Informe o valor da aposta.");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            sendMessage("ERROR;Valor de aposta inválido.");
            return;
        }

        table.placeBet(this, amount);
    }

    private void handleAction(String[] parts) {
        if (!isAuthenticated()) {
            sendMessage("ERROR;Autenticação necessária.");
            return;
        }

        if (table == null) {
            sendMessage("ERROR;Entre na mesa para jogar.");
            return;
        }

        if (parts.length < 2) {
            sendMessage("ERROR;Ação inválida.");
            return;
        }

        table.handleAction(this, parts[1].toUpperCase());
    }

    private void handleHistory() {
        if (!isAuthenticated()) {
            sendMessage("ERROR;Autenticação necessária.");
            return;
        }

        try {
            List<HistoryEntry> entries = DatabaseOperations.fetchHistory(userData.getId());
            sendMessage("HISTORY_BEGIN;" + entries.size());
            for (HistoryEntry entry : entries) {
                Boolean won = entry.won();
                String flag = won == null ? "NULL" : (won ? "1" : "0");
                sendMessage(String.format(
                    "HISTORY_ENTRY;%s;%d;%s;%s;%s",
                    flag,
                    entry.value(),
                    entry.playerCardsJson(),
                    entry.dealerCardsJson(),
                    entry.createdAt()
                ));
            }
            sendMessage("HISTORY_END");
        } catch (SQLException e) {
            sendMessage("ERROR;Falha ao carregar histórico.");
        }
    }

    private void handleLogout() {
        if (table != null) {
            tableManager.removePlayer(table, this);
            table = null;
        }
        userData = null;
        balance = 0;
        sendMessage("LOGOUT_OK");
    }

    private void handleDeleteAccount() {
        if (!isAuthenticated()) {
            sendMessage("ACCOUNT_DELETE_FAIL;Autenticação necessária.");
            return;
        }

        try {
            String userId = userData.getId();
            if (table != null) {
                tableManager.removePlayer(table, this);
                table = null;
            }
            DatabaseOperations.deleteUser(userId);
            sendMessage("ACCOUNT_DELETED");
            userData = null;
            balance = 0;
        } catch (SQLException e) {
            sendMessage("ACCOUNT_DELETE_FAIL;" + e.getMessage());
        }
    }

    private boolean isAuthenticated() {
        return userData != null;
    }

    public synchronized void sendMessage(String message) {
        if (output != null) {
            output.println(message);
        }
    }

    public synchronized void adjustBalance(int delta) {
        if (!isAuthenticated()) {
            return;
        }
        updateBalance(balance + delta);
    }

    public synchronized void updateBalance(int newBalance) {
        if (!isAuthenticated()) {
            return;
        }
        balance = newBalance;
        try {
            DatabaseOperations.updateBalance(userData.getId(), balance);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sendMessage("BALANCE;" + balance);
    }

    public String getUserId() {
        return userData != null ? userData.getId() : "";
    }

    public String getUsername() {
        return userData != null ? userData.getUsername() : "";
    }

    public int getBalance() {
        return balance;
    }

    private void cleanup() {
        if (!running.getAndSet(false)) {
            return;
        }
        if (table != null) {
            tableManager.removePlayer(table, this);
            table = null;
        }

        server.removeHandler(this);

        try {
            if (input != null) {
                input.close();
            }
        } catch (Exception ignored) {}

        if (output != null) {
            output.close();
        }

        try {
            socket.close();
        } catch (IOException ignored) {}
    }
}

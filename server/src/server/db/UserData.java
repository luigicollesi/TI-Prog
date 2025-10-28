package server.db;

public class UserData {
    private final String id;
    private final String username;
    private final int balance;

    public UserData(String id, String username, int balance) {
        this.id = id;
        this.username = username;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getBalance() {
        return balance;
    }
}

package server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.security.GeneralSecurityException;

import server.security.PasswordCrypto;

public final class DatabaseOperations {

    private DatabaseOperations() {}

    private static PreparedStatement prepare(String sql, String[] params) throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
        }
        return ps;
    }

    public static void executeUpdate(String sql, String[] params) throws SQLException {
        try (PreparedStatement ps = prepare(sql, params)) {
            ps.executeUpdate();
        }
    }

    public static ResultSet executeQuery(String sql, String[] params) throws SQLException {
        PreparedStatement ps = prepare(sql, params);
        return ps.executeQuery();
    }

    public static UserData authenticate(String username, String password) throws SQLException {
        String sql = "SELECT id, user_name, Money AS balance, senha FROM usuarios WHERE user_name = ?";
        try (PreparedStatement ps = prepare(sql, new String[]{username});
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String encryptedInput = PasswordCrypto.encryptPassword(password);
                String storedPassword = rs.getString("senha");
                if (storedPassword != null && storedPassword.equals(encryptedInput)) {
                    return new UserData(
                        rs.getString("id"),
                        rs.getString("user_name"),
                        rs.getInt("balance")
                    );
                }
            }
        } catch (GeneralSecurityException e) {
            throw new SQLException("Erro ao criptografar senha.", e);
        }
        return null;
    }

    public static boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM usuarios WHERE user_name = ?";
        try (PreparedStatement ps = prepare(sql, new String[]{username});
             ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }

    public static boolean createUser(String username, String password) throws SQLException {
        try {
            String encryptedPassword = PasswordCrypto.encryptPassword(password);
            executeUpdate(
                "INSERT INTO usuarios (user_name, senha) VALUES (?, ?)",
                new String[]{username, encryptedPassword}
            );
            return true;
        } catch (GeneralSecurityException e) {
            throw new SQLException("Erro ao criptografar senha.", e);
        }
    }

    public static void updateBalance(String userId, int newBalance) throws SQLException {
        String sql = "UPDATE usuarios SET Money = ? WHERE id = ?";
        executeUpdate(sql, new String[]{String.valueOf(newBalance), userId});
    }

    public static int fetchBalance(String userId) throws SQLException {
        String sql = "SELECT Money FROM usuarios WHERE id = ?";
        try (PreparedStatement ps = prepare(sql, new String[]{userId});
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public static void insertHistory(String userId, int amount, String playerCardsJson, String dealerCardsJson, Boolean won) throws SQLException {
        String sql = "INSERT INTO historico_partidas (user_id, valor, cartas_player, cartas_bot, venceu) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setInt(2, amount);
            ps.setString(3, playerCardsJson);
            ps.setString(4, dealerCardsJson);
            if (won == null) {
                ps.setNull(5, Types.TINYINT);
            } else {
                ps.setInt(5, won ? 1 : 0);
            }
            ps.executeUpdate();
        }
    }

    public static List<HistoryEntry> fetchHistory(String userId) throws SQLException {
        String sql = "SELECT valor, cartas_player, cartas_bot, venceu, created_at FROM historico_partidas WHERE user_id = ? ORDER BY created_at DESC";
        List<HistoryEntry> entries = new ArrayList<>();

        try (PreparedStatement ps = prepare(sql, new String[]{userId});
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object venceuObj = rs.getObject("venceu");
                Boolean won = null;
                if (venceuObj instanceof Boolean boolVal) {
                    won = boolVal;
                } else if (venceuObj instanceof Number numberVal) {
                    won = numberVal.intValue() == 1;
                } else if (venceuObj != null) {
                    won = "1".equalsIgnoreCase(venceuObj.toString());
                }
                entries.add(new HistoryEntry(
                    won,
                    rs.getInt("valor"),
                    rs.getString("cartas_player"),
                    rs.getString("cartas_bot"),
                    rs.getString("created_at")
                ));
            }
        }
        return entries;
    }

    public static void deleteUser(String userId) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        executeUpdate(sql, new String[]{userId});
    }
}

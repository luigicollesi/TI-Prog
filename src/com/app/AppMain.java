package com.app;

import com.app.db.DatabaseConnection;
import com.app.ui.LoginFrame;

import javax.swing.*;
import java.awt.Font;
import java.sql.Connection;
import java.sql.SQLException;

public class AppMain {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.close();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Ajusta fonte das caixas de diálogo para tamanhos mais legíveis
            UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 25));
            UIManager.put("OptionPane.buttonFont",  new Font("Arial", Font.BOLD, 25));
            JOptionPane.showMessageDialog(
                null,
                "Não foi possível conectar ao banco de dados:\n" + ex.getMessage(),
                "Erro de Conexão",
                JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        }
    }
}

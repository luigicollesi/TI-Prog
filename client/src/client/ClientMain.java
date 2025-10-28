package client;

import client.net.ServerConnection;
import client.ui.Auth.LoginFrame;

import javax.swing.*;
import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int port = 5555;
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
            }
        }

        ServerConnection connection;
        try {
            connection = new ServerConnection(host, port);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                null,
                "Não foi possível conectar ao servidor: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        ServerConnection finalConnection = connection;
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame(finalConnection);
            frame.setVisible(true);
        });
    }
}

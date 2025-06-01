package app.ui.utility;

import javax.swing.*;

import java.awt.*;

public class CustomDialog {

    public static void showMessage(Component parent, String message, String title, int messageType) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), title, true);
        dialog.setUndecorated(true);

        // Ajusta tamanho conforme o tipo de mensagem
        Dimension dialogSize = (messageType == JOptionPane.INFORMATION_MESSAGE)
                ? new Dimension(450, 180)
                : new Dimension(500, 250);

        dialog.setSize(dialogSize);
        dialog.setLocationRelativeTo(parent);

        // Estilos
        Color bgColor = new Color(30, 30, 30, 240);
        Color innerBoxColor = new Color(20, 20, 20, 220);
        Color borderColor = null;
        String iconChar = null;

        if (messageType == JOptionPane.ERROR_MESSAGE) {
            borderColor = Color.RED;
            iconChar = "\u2716"; // ✖
        } else if (messageType == JOptionPane.WARNING_MESSAGE) {
            borderColor = Color.YELLOW;
            iconChar = "⚠️";
        }

        // Painel principal
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bgColor);

        // Define a borda apenas para erro/aviso
        if (borderColor != null) {
            panel.setBorder(BorderFactory.createLineBorder(borderColor, 3));
        }

        // Painel da mensagem
        JPanel messageBox = new JPanel();
        messageBox.setLayout(new BoxLayout(messageBox, BoxLayout.Y_AXIS));
        messageBox.setBackground(innerBoxColor);
        messageBox.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        if (iconChar != null) {
            JLabel iconLabel = new JLabel(iconChar, SwingConstants.CENTER);
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            iconLabel.setFont(new Font("SansSerif", Font.BOLD, 42));
            iconLabel.setForeground(borderColor);
            messageBox.add(iconLabel);
        }

        JLabel lblMsg = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>", SwingConstants.CENTER);
        lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMsg.setFont(new Font("Arial", Font.BOLD, 22));
        lblMsg.setForeground(Color.WHITE);
        lblMsg.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        messageBox.add(lblMsg);

        panel.add(messageBox, BorderLayout.CENTER);

        // Botão
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JButton btnOk = CustomInput.createButtom("OK", Color.BLACK);
        btnOk.setPreferredSize(new Dimension(150, 50));
        btnOk.addActionListener(e -> dialog.dispose());
        buttonPanel.add(btnOk);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private CustomDialog() {
        throw new UnsupportedOperationException("Utility class");
    }
}

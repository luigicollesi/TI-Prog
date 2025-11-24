package client.ui.Private;

import client.ui.utility.PanelImage;
import client.ui.utility.RoundButton;
import client.i18n.I18n;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.function.Consumer;

/**
 * Janela simples de chat entre jogadores.
 * Armazena histórico em um TXT baseado no usuário logado.
 */
public class ChatFrame extends JDialog {
    private final JTextPane chatPane = new JTextPane();
    private final JTextField inputField = new JTextField();
    private final RoundButton sendButton = new RoundButton("Enviar", Color.BLACK);

    private final SimpleAttributeSet nameStyle = new SimpleAttributeSet();
    private final SimpleAttributeSet messageStyle = new SimpleAttributeSet();

    private final Path historyFile;
    private final Consumer<String> sender;

    public ChatFrame(JFrame owner, String username, Consumer<String> sender) {
        super(owner, I18n.get("chat.title"), false);
        this.sender = sender;

        StyleConstants.setBold(nameStyle, true);
        StyleConstants.setFontSize(nameStyle, 20);
        StyleConstants.setFontSize(messageStyle, 18);
        StyleConstants.setForeground(nameStyle, Color.WHITE);
        StyleConstants.setForeground(messageStyle, Color.WHITE);

        historyFile = buildHistoryPath(username);
        loadHistory();
        registerShutdownHook();

        setSize(480, 640);
        setLocationRelativeTo(owner);

        PanelImage background = new PanelImage(new ImageIcon("public/Images/GameFundo.png").getImage(), true);
        background.setLayout(new BorderLayout(10, 10));
        background.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        setContentPane(background);

        chatPane.setEditable(false);
        chatPane.setOpaque(false);
        chatPane.setForeground(Color.WHITE);
        chatPane.setFont(new Font("Arial", Font.PLAIN, 18));

        JScrollPane scrollPane = new JScrollPane(chatPane);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1, true));
        background.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
        inputPanel.setOpaque(false);
        inputField.setFont(new Font("Arial", Font.PLAIN, 18));
        inputField.setPreferredSize(new Dimension(100, 48));
        sendButton.setPreferredSize(new Dimension(120, 48));
        sendButton.setText(I18n.get("chat.send"));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        background.add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendUserMessage());
        inputField.addActionListener(e -> sendUserMessage());
    }

    private Path buildHistoryPath(String user) {
        String safe = user == null ? "anon" : user.replaceAll("[^a-zA-Z0-9_-]", "_");
        return Path.of("chat_cache", safe + ".txt");
    }

    private void sendUserMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) {
            return;
        }
        inputField.setText("");
        if (sender != null) {
            sender.accept(text);
        }
    }

    public void addIncomingMessage(String author, String message) {
        appendMessage(author, message, true);
    }

    private void appendMessage(String author, String message, boolean persist) {
        StyledDocument doc = chatPane.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), author + "\n", nameStyle);
            doc.insertString(doc.getLength(), message + "\n\n", messageStyle);
        } catch (BadLocationException ignored) {}

        chatPane.setCaretPosition(doc.getLength());
        if (persist) {
            persistMessage(author, message);
        }
    }

    private void loadHistory() {
        if (!Files.exists(historyFile)) {
            return;
        }
        try {
            List<String> lines = Files.readAllLines(historyFile, StandardCharsets.UTF_8);
            for (int i = 0; i + 1 < lines.size(); i += 3) {
                String author = lines.get(i).trim();
                String msg = lines.get(i + 1);
                appendMessage(author.isEmpty() ? "?" : author, msg, false);
            }
        } catch (IOException ignored) {
        }
    }

    private void persistMessage(String author, String message) {
        try {
            Files.createDirectories(historyFile.getParent());
            String block = author + "\n" + message + "\n\n";
            Files.writeString(
                historyFile,
                block,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        } catch (IOException ignored) {
        }
    }

    public void deleteHistoryFile() {
        try {
            Files.deleteIfExists(historyFile);
        } catch (IOException ignored) {
        }
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::deleteHistoryFile));
    }

    public void applyTranslations() {
        setTitle(I18n.get("chat.title"));
        sendButton.setText(I18n.get("chat.send"));
    }
}

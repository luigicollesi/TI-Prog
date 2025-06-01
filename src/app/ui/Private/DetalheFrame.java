package app.ui.Private;

import app.game.Game;
import app.ui.utility.PanelImage;
import app.ui.utility.RoundButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DetalheFrame extends JDialog {
    private final Image backgroundImage = new ImageIcon("public/Images/GameFundo2.png").getImage();
    private final JPanel fundoDet;
    private final JLabel lblSomaBot;
    private final JPanel painelBot;
    private final JLabel lblSomaPlayer;
    private final JPanel painelPlayer;
    private final JButton btnFechar;

    private final JPanel contentPanel;
    private final JPanel southPanel;

    private final HistoricoFrame historicoFrame;

    public DetalheFrame(HistoricoFrame parent) {
        super(parent, "Detalhes da Partida", true);
        setSize(800, 570);
        setLocationRelativeTo(parent);

        historicoFrame = parent;

        // Painel de fundo com imagem + overlay
        fundoDet = new PanelImage(backgroundImage, true);
        fundoDet.setLayout(new BorderLayout());
        fundoDet.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(fundoDet);

        // --- Content Panel (CENTER) ---
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Label de soma do Bot
        lblSomaBot = new JLabel("", SwingConstants.CENTER);
        lblSomaBot.setFont(new Font("Arial", Font.BOLD, 25));
        lblSomaBot.setForeground(Color.WHITE);
        lblSomaBot.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSomaBot.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        contentPanel.add(lblSomaBot);
        contentPanel.add(Box.createVerticalStrut(10));

        // Painel para cartas do Bot
        painelBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        painelBot.setOpaque(false);
        painelBot.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelBot.setPreferredSize(new Dimension(500, 160));
        painelBot.setMaximumSize(new Dimension(500, 160));
        contentPanel.add(painelBot);
        contentPanel.add(Box.createVerticalStrut(20));

        // Label de soma do Jogador
        lblSomaPlayer = new JLabel("", SwingConstants.CENTER);
        lblSomaPlayer.setFont(new Font("Arial", Font.BOLD, 25));
        lblSomaPlayer.setForeground(Color.WHITE);
        lblSomaPlayer.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSomaPlayer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        contentPanel.add(lblSomaPlayer);
        contentPanel.add(Box.createVerticalStrut(10));

        // Painel para cartas do Jogador
        painelPlayer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        painelPlayer.setOpaque(false);
        painelPlayer.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelPlayer.setPreferredSize(new Dimension(500, 160));
        painelPlayer.setMaximumSize(new Dimension(500, 160));
        contentPanel.add(painelPlayer);
        contentPanel.add(Box.createVerticalGlue());

        fundoDet.add(contentPanel, BorderLayout.CENTER);

        // --- Botão “Fechar” (SOUTH) ---
        southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.setOpaque(false);
        southPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnFechar = new RoundButton("Fechar", Color.BLACK);
        btnFechar.setPreferredSize(new Dimension(250, 60));
        btnFechar.addActionListener(e -> setVisible(false));
        southPanel.add(btnFechar);

        fundoDet.add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * Atualiza os componentes com base nos JSON e exibe o diálogo.
     */
    public void mostrar(String cartasPlayerJSON, String cartasBotJSON) {
        List<String[]> cartasPlayer = parseCartasJson(cartasPlayerJSON);
        List<String[]> cartasBot = parseCartasJson(cartasBotJSON);

        int somaPlayer = Game.somar(cartasPlayer);
        int somaBot = Game.somar(cartasBot);

        lblSomaBot.setText("Bot: " + somaBot);
        lblSomaPlayer.setText("Jogador: " + somaPlayer);

        painelBot.removeAll();
        for (String[] carta : cartasBot) {
            JLabel img = getCartaLabel(carta);
            painelBot.add(img);
        }

        painelPlayer.removeAll();
        for (String[] carta : cartasPlayer) {
            JLabel img = getCartaLabel(carta);
            painelPlayer.add(img);
        }

        painelBot.revalidate();
        painelBot.repaint();
        painelPlayer.revalidate();
        painelPlayer.repaint();

        setLocationRelativeTo(historicoFrame);
        setVisible(true);
    }

    private JLabel getCartaLabel(String[] carta) {
        String path = "public/Cartas/" + carta[1] + "/" + carta[0] + ".png";
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(100, 150, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(img));
    }

    private List<String[]> parseCartasJson(String json) {
        List<String[]> cartas = new ArrayList<>();
        json = json.replaceAll("\\[\\[|\\]\\]", "").trim();
        if (json.isEmpty()) return cartas;
        String[] pares = json.split("\\],\\s*\\[");
        for (String par : pares) {
            String[] valores = par.replaceAll("[\"\\[\\]]", "").split(",");
            cartas.add(new String[]{valores[0].trim(), valores[1].trim()});
        }
        return cartas;
    }
}

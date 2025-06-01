package app.ui.Private;


import app.game.Game;
import app.ui.utility.CustomInput;

import javax.swing.*;
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

    private final HistoricoFrame historicoFrame;

    public DetalheFrame(HistoricoFrame parent) {
        super(parent, "Detalhes da Partida", true);
        setSize(800, 610);
        setLayout(null);

        historicoFrame = parent;

        // Painel de fundo com imagem + overlay
        fundoDet = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        fundoDet.setLayout(null);
        setContentPane(fundoDet);

        // Label de soma do Bot
        lblSomaBot = new JLabel("", SwingConstants.CENTER);
        lblSomaBot.setFont(new Font("Arial", Font.BOLD, 25));
        lblSomaBot.setForeground(Color.WHITE);
        lblSomaBot.setBounds(0, 10, 800, 30);
        fundoDet.add(lblSomaBot);

        // Painel para cartas do Bot
        painelBot = new JPanel();
        painelBot.setBounds(150, 50, 500, 160);
        painelBot.setOpaque(false);
        fundoDet.add(painelBot);

        // Label de soma do Jogador
        lblSomaPlayer = new JLabel("", SwingConstants.CENTER);
        lblSomaPlayer.setFont(new Font("Arial", Font.BOLD, 25));
        lblSomaPlayer.setForeground(Color.WHITE);
        lblSomaPlayer.setBounds(0, 230, 800, 30);
        fundoDet.add(lblSomaPlayer);

        // Painel para cartas do Jogador
        painelPlayer = new JPanel();
        painelPlayer.setBounds(150, 280, 500, 160);
        painelPlayer.setOpaque(false);
        fundoDet.add(painelPlayer);

        // Botão “Fechar”
        btnFechar = CustomInput.createButtom("Fechar", Color.BLACK);
        btnFechar.setBounds(275, 470, 250, 60);
        btnFechar.addActionListener(e -> setVisible(false));
        fundoDet.add(btnFechar);
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

package app.ui;

import app.db.DatabaseOperations;
import app.game.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HistoricoFrame extends JFrame {

    private Image backgroundImage = new ImageIcon("public/Images/GameFundo2.png").getImage();

    public HistoricoFrame(JFrame parent, String userId) {
        setTitle("Histórico de Partidas");
        setSize(800, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Point parentLocation = parent.getLocation();
        int x = parentLocation.x + (parent.getWidth() - getWidth()) / 2;
        int y = parentLocation.y + (parent.getHeight() - getHeight()) / 2 - 70;
        setLocation(x, y);

        JPanel backgroundPanel = new JPanel() {
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
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        try {
            ResultSet rs = DatabaseOperations.executeQuery(
                "SELECT * FROM historico_partidas WHERE user_id = ? ORDER BY created_at DESC;",
                new String[]{userId}
            );

            while (rs != null && rs.next()) {
                boolean venceu = rs.getInt("venceu") == 1;
                int valor = rs.getInt("valor");
                String cartasPlayer = rs.getString("cartas_player");
                String cartasBot = rs.getString("cartas_bot");

                String texto = (venceu ? "Ganhou  +" : "Perdeu  -") + "$" + valor;
                JButton btn = CustomInput.createButtom(texto, venceu ? new Color(0, 220, 0) : new Color(220, 50, 50));
                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                btn.setForeground(venceu ? new Color(0, 220, 0) : new Color(220, 50, 50));
                btn.setFont(new Font("Arial", Font.BOLD, 20));
                btn.setMaximumSize(new Dimension(200, 50));
                btn.addActionListener((ActionEvent e) -> mostrarDetalhes(cartasPlayer, cartasBot));

                centerPanel.add(btn);
                centerPanel.add(Box.createVerticalStrut(15));
            }

            if (rs != null) rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(centerPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        JButton btnVoltar = CustomInput.createButtom("Voltar", Color.BLACK);
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 18));
        btnVoltar.setPreferredSize(new Dimension(150, 45));
        btnVoltar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVoltar.addActionListener(e -> {
            new MenuFrame(this, userId);
            dispose();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnVoltar);
        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(backgroundPanel);
        setVisible(true);
    }

    private void mostrarDetalhes(String cartasPlayerJSON, String cartasBotJSON) {
        List<String[]> cartasPlayer = parseCartasJson(cartasPlayerJSON);
        List<String[]> cartasBot = parseCartasJson(cartasBotJSON);

        JFrame detalhe = new JFrame("Detalhes da Partida");
        detalhe.setSize(800, 450);
        detalhe.setLayout(null);
        detalhe.setLocationRelativeTo(this);

        JPanel backgroundPanel = new JPanel() {
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
        backgroundPanel.setLayout(null);

        int somaPlayer = Game.somar(cartasPlayer);
        int somaBot = Game.somar(cartasBot);

        JLabel lblSomaBot = new JLabel("Bot: " + somaBot, SwingConstants.CENTER);
        lblSomaBot.setFont(new Font("Arial", Font.BOLD, 22));
        lblSomaBot.setBounds(0, 10, 800, 30);
        backgroundPanel.add(lblSomaBot);

        JPanel painelBot = new JPanel();
        painelBot.setBounds(150, 50, 500, 130);
        painelBot.setOpaque(false);
        for (String[] carta : cartasBot) {
            JLabel img = getCartaLabel(carta, 135); // altura aumentada
            painelBot.add(img);
        }
        backgroundPanel.add(painelBot);

        JLabel lblSomaPlayer = new JLabel("Jogador: " + somaPlayer, SwingConstants.CENTER);
        lblSomaPlayer.setFont(new Font("Arial", Font.BOLD, 22));
        lblSomaPlayer.setBounds(0, 190, 800, 30);
        backgroundPanel.add(lblSomaPlayer);

        JPanel painelPlayer = new JPanel();
        painelPlayer.setBounds(150, 230, 500, 130);
        painelPlayer.setOpaque(false);
        for (String[] carta : cartasPlayer) {
            JLabel img = getCartaLabel(carta, 135); // altura aumentada
            painelPlayer.add(img);
        }
        backgroundPanel.add(painelPlayer);

        JButton btnFechar = CustomInput.createButtom("Fechar", Color.BLACK);
        btnFechar.setFont(new Font("Arial", Font.BOLD, 18));
        btnFechar.setBounds(325, 380, 150, 40);
        btnFechar.addActionListener(e -> detalhe.dispose());
        backgroundPanel.add(btnFechar);

        detalhe.setContentPane(backgroundPanel);
        detalhe.setVisible(true);
    }

    private JLabel getCartaLabel(String[] carta, int altura) {
        String path = "public/Cartas/" + carta[1] + "/" + carta[0] + ".png";
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(80, altura, Image.SCALE_SMOOTH);
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

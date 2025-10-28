package client.ui.Private;

import client.ui.utility.PanelImage;
import client.ui.utility.RoundButton;

import javax.swing.*;
import java.awt.*;

import javax.swing.border.EmptyBorder;
import java.util.ArrayList;
import java.util.List;

public class DetalheFrame extends JDialog {
    private final PanelImage fundoDet;
    private final JLabel lblSomaBot;
    private final JPanel painelBot;
    private final JLabel lblSomaPlayer;
    private final JPanel painelPlayer;
    private final RoundButton btnFechar;

    private final JPanel contentPanel;
    private final JPanel southPanel;

    private final HistoricoFrame historicoFrame;

    private final ImageIcon backgroundImage = new ImageIcon("public/Images/GameFundo2.png");

    public DetalheFrame(HistoricoFrame parent) {
        super(parent, "Detalhes da Partida", true);
        setSize(800, 570);
        setLocationRelativeTo(parent);

        historicoFrame = parent;

        fundoDet = new PanelImage(backgroundImage.getImage(), true);
        fundoDet.setLayout(new BorderLayout());
        fundoDet.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(fundoDet);

        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new javax.swing.BoxLayout(contentPanel, javax.swing.BoxLayout.Y_AXIS));

        lblSomaBot = new JLabel("", SwingConstants.CENTER);
        lblSomaBot.setFont(new Font("Arial", Font.BOLD, 25));
        lblSomaBot.setForeground(Color.WHITE);
        lblSomaBot.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSomaBot.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 30));
        contentPanel.add(lblSomaBot);
        contentPanel.add(javax.swing.Box.createVerticalStrut(10));

        painelBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        painelBot.setOpaque(false);
        painelBot.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelBot.setPreferredSize(new java.awt.Dimension(500, 160));
        painelBot.setMaximumSize(new java.awt.Dimension(500, 160));
        contentPanel.add(painelBot);
        contentPanel.add(javax.swing.Box.createVerticalStrut(20));

        lblSomaPlayer = new JLabel("", SwingConstants.CENTER);
        lblSomaPlayer.setFont(new Font("Arial", Font.BOLD, 25));
        lblSomaPlayer.setForeground(Color.WHITE);
        lblSomaPlayer.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSomaPlayer.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 30));
        contentPanel.add(lblSomaPlayer);
        contentPanel.add(javax.swing.Box.createVerticalStrut(10));

        painelPlayer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        painelPlayer.setOpaque(false);
        painelPlayer.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelPlayer.setPreferredSize(new java.awt.Dimension(500, 160));
        painelPlayer.setMaximumSize(new java.awt.Dimension(500, 160));
        contentPanel.add(painelPlayer);
        contentPanel.add(javax.swing.Box.createVerticalGlue());

        fundoDet.add(contentPanel, BorderLayout.CENTER);

        southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.setOpaque(false);
        southPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        btnFechar = new RoundButton("Fechar", Color.BLACK);
        btnFechar.setPreferredSize(new java.awt.Dimension(250, 60));
        btnFechar.addActionListener(e -> setVisible(false));
        southPanel.add(btnFechar);

        fundoDet.add(southPanel, BorderLayout.SOUTH);
    }

    public void mostrar(String cartasPlayerJSON, String cartasDealerJSON) {
        List<String[]> cartasPlayer = parseCartasJson(cartasPlayerJSON);
        List<String[]> cartasDealer = parseCartasJson(cartasDealerJSON);

        int somaPlayer = somar(cartasPlayer);
        int somaDealer = somar(cartasDealer);

        lblSomaBot.setText("Dealer: " + somaDealer);
        lblSomaPlayer.setText("Jogador: " + somaPlayer);

        preencherPainel(painelBot, cartasDealer);
        preencherPainel(painelPlayer, cartasPlayer);

        setLocationRelativeTo(historicoFrame);
        setVisible(true);
    }

    private void preencherPainel(JPanel painel, List<String[]> cartas) {
        painel.removeAll();
        for (String[] carta : cartas) {
            painel.add(getCartaLabel(carta));
        }
        painel.revalidate();
        painel.repaint();
    }

    private JLabel getCartaLabel(String[] carta) {
        String path = "public/Cartas/" + carta[1] + "/" + carta[0] + ".png";
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(100, 150, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(img));
    }

    private List<String[]> parseCartasJson(String json) {
        List<String[]> cartas = new ArrayList<>();
        String cleaned = json.replaceAll("[\\[\\]\"]", "");
        if (cleaned.isBlank()) {
            return cartas;
        }
        String[] tokens = cleaned.split(",");
        for (int i = 0; i + 1 < tokens.length; i += 2) {
            String rank = tokens[i].trim();
            String suit = tokens[i + 1].trim();
            cartas.add(new String[]{rank, suit});
        }
        return cartas;
    }

    private int somar(List<String[]> cartas) {
        int total = 0;
        int ases = 0;
        for (String[] carta : cartas) {
            String valor = carta[0];
            switch (valor) {
                case "J":
                case "Q":
                case "K":
                    total += 10;
                    break;
                case "A":
                    total += 11;
                    ases++;
                    break;
                default:
                    total += Integer.parseInt(valor);
            }
        }
        while (total > 21 && ases > 0) {
            total -= 10;
            ases--;
        }
        return total;
    }
}

package app.ui.Private;

import app.ui.Auth.LoginFrame;
import app.ui.utility.PanelImage;
import app.ui.utility.RoundButton;

import java.awt.*;
import javax.swing.*;

public class MenuFrame extends JFrame {
    private final JButton btnJogar;
    private final JButton btnHistorico;
    private final JButton btnSair;
    private final JButton btnDeslogar;
    private final Image backgroundImage = new ImageIcon("public/Images/Fundo.png").getImage();

    private final JPanel backgroundPanel;
    private final JPanel centerPanel;
    private final JPanel southPanel;

    private final GameFrame gameFrame;
    private final HistoricoFrame historicoFrame;

    public MenuFrame(LoginFrame parent, String userId) {
        gameFrame = new GameFrame(this, userId);
        historicoFrame = new HistoricoFrame(this, userId);

        setTitle("Blackjack - Menu");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Centraliza a janela em relação ao parent
        Point parentLocation = parent.getLocation();
        int x = parentLocation.x + (parent.getWidth() - getWidth()) / 2;
        int y = parentLocation.y + (parent.getHeight() - getHeight()) / 2 - 60;
        setLocation(x, y);

        // Painel de fundo com imagem e overlay sem usar null layout
        backgroundPanel = new PanelImage(backgroundImage, true);
        // Usa BorderLayout para positionar center e south
        backgroundPanel.setLayout(new BorderLayout());

        // --- Centro: botões "Jogar" e "Ver Histórico" empilhados ---
        centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        // BoxLayout Y_AXIS para empilhar verticalmente
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        btnJogar = new RoundButton("Jogar", Color.BLACK);
        btnJogar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnJogar.setFont(new Font("Arial", Font.BOLD, 35));
        btnJogar.setMaximumSize(new Dimension(400, 120));
        btnJogar.addActionListener(e -> {
            gameFrame.open();
            setVisible(false);
        });

        btnHistorico = new RoundButton("Ver Histórico", Color.BLACK);
        btnHistorico.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnHistorico.setFont(new Font("Arial", Font.BOLD, 35));
        btnHistorico.setMaximumSize(new Dimension(400, 120));
        btnHistorico.addActionListener(e -> {
            historicoFrame.open();
            setVisible(false);
        });

        // Espaços verticais entre os botões
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(btnJogar);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(btnHistorico);
        centerPanel.add(Box.createVerticalGlue());

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        // --- Sul: botões "Sair" e "Deslogar" lado a lado, alinhados à direita ---
        southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        southPanel.setOpaque(false);

        btnDeslogar = new RoundButton("Deslogar", Color.BLACK);
        btnDeslogar.setPreferredSize(new Dimension(250, 60));
        btnDeslogar.addActionListener(e -> {
            dispose();
            gameFrame.dispose();
            historicoFrame.dispose();
            parent.open(this);
        });

        btnSair = new RoundButton("Sair", Color.BLACK);
        btnSair.setPreferredSize(new Dimension(250, 60));
        btnSair.addActionListener(e -> System.exit(0));

        southPanel.add(btnDeslogar);
        southPanel.add(btnSair);

        backgroundPanel.add(southPanel, BorderLayout.SOUTH);

        setContentPane(backgroundPanel);
        setVisible(true);
    }

    public void open(JFrame frame) {
        Point parentLocation = frame.getLocation();
        int x = parentLocation.x + (frame.getWidth() - getWidth()) / 2;
        int y = parentLocation.y + (frame.getHeight() - getHeight()) / 2 - 70;
        setLocation(x, y);
        setVisible(true);
    }
}

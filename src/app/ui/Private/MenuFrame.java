package app.ui.Private;


import javax.swing.*;

import app.ui.Auth.LoginFrame;
import app.ui.utility.CustomInput;

import java.awt.*;

public class MenuFrame extends JFrame {
    private final JButton btnJogar;
    private final JButton btnHistorico;
    private final JButton btnSair;
    private final JButton btnDeslogar;
    private final Image backgroundImage = new ImageIcon("public/Images/Fundo.png").getImage();

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
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                // Desenha a imagem de fundo
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                // Desenha uma camada semitransparente preta por cima
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        // Usa BorderLayout para positionar center e south
        backgroundPanel.setLayout(new BorderLayout());

        // --- Centro: botões "Jogar" e "Ver Histórico" empilhados ---
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        // BoxLayout Y_AXIS para empilhar verticalmente
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        btnJogar = CustomInput.createButtom("Jogar", Color.BLACK);
        btnJogar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnJogar.setMaximumSize(new Dimension(300, 80));
        btnJogar.addActionListener(e -> {
            gameFrame.open();
            setVisible(false);
        });

        btnHistorico = CustomInput.createButtom("Ver Histórico", Color.BLACK);
        btnHistorico.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnHistorico.setMaximumSize(new Dimension(300, 80));
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
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        southPanel.setOpaque(false);

        btnDeslogar = CustomInput.createButtom("Deslogar", Color.BLACK);
        btnDeslogar.setPreferredSize(new Dimension(250, 60));
        btnDeslogar.addActionListener(e -> {
            dispose();
            gameFrame.dispose();
            historicoFrame.dispose();
            parent.open(this);
        });

        btnSair = CustomInput.createButtom("Sair", Color.BLACK);
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

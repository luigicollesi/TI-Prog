package com.app.ui;

import javax.swing.*;
import java.awt.*;

public class MenuFrame extends JFrame {
    private JButton btnJogar;
    private JButton btnHistorico;
    private JButton btnSair;
    private Image backgroundImage;

    public MenuFrame() {
        setTitle("Blackjack - Menu");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        backgroundImage = new ImageIcon("public/Images/Fundo.png").getImage();

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(null);

        btnJogar = RoundedInput.createButtom("Jogar");
        btnJogar.setBounds(350, 250, 300, 80);
        btnJogar.addActionListener(e -> {
            CustomDialog.showMessage(this, "Iniciando o jogo...", "Jogar", JOptionPane.INFORMATION_MESSAGE);

            new GameFrame();
            dispose();
        });

        btnHistorico = RoundedInput.createButtom("Ver Histórico");
        btnHistorico.setBounds(350, 370, 300, 80);
        btnHistorico.addActionListener(e -> {
            CustomDialog.showMessage(this, "Exibindo histórico de partidas...", "Histórico", JOptionPane.INFORMATION_MESSAGE);
        });

        btnSair = RoundedInput.createButtom("Sair");
        btnSair.setBounds(800, 580, 150, 60); // canto inferior direito
        btnSair.addActionListener(e -> System.exit(0));

        backgroundPanel.add(btnJogar);
        backgroundPanel.add(btnHistorico);
        backgroundPanel.add(btnSair);

        setContentPane(backgroundPanel);
        setVisible(true);
    }
}

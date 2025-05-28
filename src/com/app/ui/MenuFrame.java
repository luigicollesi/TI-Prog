package com.app.ui;

import javax.swing.*;
import java.awt.*;


public class MenuFrame extends JFrame {
    private JButton btnJogar;
    private JButton btnHistorico;
    private Image backgroundImage;

    public MenuFrame() {
        setTitle("Blackjack - Menu");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Carrega imagem de fundo
        backgroundImage = new ImageIcon("public/Images/Fundo.png").getImage(); // ajuste o caminho conforme seu projeto

        // Painel com fundo customizado
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null);

        // Botão Jogar
        btnJogar = new JButton("Jogar");
        btnJogar.setFont(new Font("Arial", Font.BOLD, 36));
        btnJogar.setBounds(350, 250, 300, 80);
        btnJogar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Iniciando o jogo...", "Jogar", JOptionPane.INFORMATION_MESSAGE);
            // Aqui você pode chamar sua lógica de jogo
        });

        // Botão Histórico
        btnHistorico = new JButton("Ver Histórico");
        btnHistorico.setFont(new Font("Arial", Font.BOLD, 36));
        btnHistorico.setBounds(350, 370, 300, 80);
        btnHistorico.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Exibindo histórico de partidas...", "Histórico", JOptionPane.INFORMATION_MESSAGE);
            // Aqui você pode chamar a lógica de histórico
        });

        backgroundPanel.add(btnJogar);
        backgroundPanel.add(btnHistorico);

        setContentPane(backgroundPanel);
        setVisible(true);
    }
}

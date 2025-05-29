package com.app.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class GameFrame extends JFrame {
    private JPanel backgroundPanel;
    private JLabel lblSaldo;
    private JLabel lblAposta;
    private JPanel painelCartasJogador;
    private JPanel painelCartasMesa;
    private JPanel painelBotoes;
    private JButton btnSair;

    private int saldo = 500;
    private int apostaAtual = 0;

    private Image backgroundImage = new ImageIcon("public/Images/GameFundo.png").getImage();;

    public GameFrame() {
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        backgroundPanel = new JPanel() {
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

        // Label do saldo (topo)
        lblSaldo = new JLabel("Saldo: $" + saldo, SwingConstants.LEFT);
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 32));
        lblSaldo.setForeground(Color.WHITE);
        lblSaldo.setBounds(30, 10, 400, 40);

        // Botão de Sair para o menu
        btnSair = RoundedInput.createButtom("Sair");
        btnSair.setFont(new Font("Arial", Font.BOLD, 20));
        btnSair.setBounds(850, 10, 100, 40);
        btnSair.addActionListener(e -> {
            this.dispose();
            new MenuFrame(); // Volta ao menu
        });

        // Label de aposta (centralizada)
        lblAposta = new JLabel("Apostando: $" + apostaAtual, SwingConstants.CENTER);
        lblAposta.setFont(new Font("Arial", Font.BOLD, 28));
        lblAposta.setForeground(Color.WHITE);
        lblAposta.setBounds(300, 530, 400, 40);

        // Painéis de cartas
        painelCartasMesa = new JPanel();
        painelCartasMesa.setOpaque(false);
        painelCartasMesa.setBounds(150, 100, 700, 150);

        painelCartasJogador = new JPanel();
        painelCartasJogador.setOpaque(false);
        painelCartasJogador.setBounds(150, 350, 700, 150);

        // Painel dos botões (centralizado)
        painelBotoes = new JPanel();
        painelBotoes.setBounds(150, 580, 700, 60);
        painelBotoes.setOpaque(false);
        painelBotoes.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        comecarApostas();        

        setVisible(true);
    }

    private void comecarApostas(){
        setTitle("Blackjack - Fase de Aposta");
        painelBotoes.removeAll();
        int[] valores = {5, 10, 25, 50};
        for (int valor : valores) {
            JButton btn = RoundedInput.createButtom("+" + valor);
            btn.setFont(new Font("Arial", Font.BOLD, 22));
            btn.setPreferredSize(new Dimension(100, 45));
            btn.addActionListener(e -> adicionarAposta(valor));
            painelBotoes.add(btn);
        }

        JButton btnFinalizar = RoundedInput.createButtom("Finalizar Aposta");
        btnFinalizar.setFont(new Font("Arial", Font.BOLD, 22));
        btnFinalizar.setPreferredSize(new Dimension(200, 45));
        btnFinalizar.addActionListener(this::finalizarAposta);
        painelBotoes.add(btnFinalizar);

        painelBotoes.setVisible(true);
        painelBotoes.revalidate();
        adicionarComponentes();
    }

    private void botoesGame(){
         painelBotoes.removeAll();
        JButton btnComprar = RoundedInput.createButtom("Comprar");
        btnComprar.setFont(new Font("Arial", Font.BOLD, 22));
        btnComprar.setPreferredSize(new Dimension(200, 45));
        btnComprar.addActionListener(this::comprarCarta);
        painelBotoes.add(btnComprar);

        JButton btnManter = RoundedInput.createButtom("Manter");
        btnManter.setFont(new Font("Arial", Font.BOLD, 22));
        btnManter.setPreferredSize(new Dimension(200, 45));
        btnManter.addActionListener(this::manterCartas);
        painelBotoes.add(btnManter);

        JButton btnDesistir = RoundedInput.createButtom("Desistir");
        btnDesistir.setFont(new Font("Arial", Font.BOLD, 22));
        btnDesistir.setPreferredSize(new Dimension(200, 45));
        btnDesistir.addActionListener(this::desistirCartas);
        painelBotoes.add(btnDesistir);

        painelBotoes.setVisible(true);
        painelBotoes.revalidate(); 
        adicionarComponentes();
    }

    private void comprarCarta(ActionEvent e){
        CustomDialog.showMessage(this, "Comprando Carta...", "Comprar", JOptionPane.INFORMATION_MESSAGE);
    }

    private void manterCartas(ActionEvent e){
        CustomDialog.showMessage(this, "Manter Cartas", "Manter", JOptionPane.INFORMATION_MESSAGE);
    }

    private void desistirCartas(ActionEvent e){
        CustomDialog.showMessage(this, "Desistindo da Rodada", "Desistindo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void adicionarAposta(int valor) {
        if (saldo >= valor) {
            apostaAtual += valor;
            saldo -= valor;
            atualizarLabels();
        } else {
            CustomDialog.showMessage(this, "Saldo insuficiente.", "Erro", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void finalizarAposta(ActionEvent e) {
        painelBotoes.setVisible(false);
        CustomDialog.showMessage(this, "Aposta de $" + apostaAtual + " finalizada!", "Aposta", JOptionPane.INFORMATION_MESSAGE);
        iniciarFaseDeJogo();
    }

    private void iniciarFaseDeJogo() {
        setTitle("Blackjack - Fase de Jogo");
        botoesGame();
    }

    private void atualizarLabels() {
        lblSaldo.setText("Saldo: $" + saldo);
        lblAposta.setText("Apostando: $" + apostaAtual);
    }

    private void adicionarComponentes(){
        backgroundPanel.add(lblSaldo);
        backgroundPanel.add(btnSair);
        backgroundPanel.add(lblAposta);
        backgroundPanel.add(painelCartasMesa);
        backgroundPanel.add(painelCartasJogador);
        backgroundPanel.add(painelBotoes);
        setContentPane(backgroundPanel);
    }
}

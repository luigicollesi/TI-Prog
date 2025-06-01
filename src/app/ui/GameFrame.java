package app.ui;

import app.game.Game;
import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private final JPanel backgroundPanel;
    private final JLabel lblSaldo;
    private final JLabel lblAposta;
    private final JPanel painelCartasJogador;
    private final JPanel painelCartasMesa;
    private final JPanel painelBotoes;
    private final JButton btnSair;
    private final JLabel lblValorCartasJogador;

    private final MenuFrame menuFrame;;

    private int apostaAtual = 0;

    private final Image backgroundImage = new ImageIcon("public/Images/GameFundo.png").getImage();
    private final Game match;

    public GameFrame(MenuFrame parent, String userId) {
        this.match = new Game(this, userId);

        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        menuFrame = parent;

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
        lblSaldo = new JLabel("Saldo: $" + match.getSaldo(), SwingConstants.LEFT);
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 32));
        lblSaldo.setForeground(Color.WHITE);
        lblSaldo.setBounds(30, 10, 400, 40);

        // Botão de Sair para o menu
        btnSair = CustomInput.createButtom("Sair", Color.BLACK);
        btnSair.setFont(new Font("Arial", Font.BOLD, 20));
        btnSair.setBounds(800, 20, 150, 50);
        btnSair.addActionListener(e -> {
            menuFrame.open(this);
            setVisible(false);
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

        lblValorCartasJogador = new JLabel("", SwingConstants.CENTER);
        lblValorCartasJogador.setFont(new Font("Arial", Font.BOLD, 24));
        lblValorCartasJogador.setForeground(Color.WHITE);
        lblValorCartasJogador.setBounds(0, 300, getWidth(), 40);

        comecarApostas();
        
        backgroundPanel.add(lblSaldo);
        backgroundPanel.add(btnSair);
        backgroundPanel.add(lblAposta);
        backgroundPanel.add(painelCartasMesa);
        backgroundPanel.add(painelCartasJogador);
        backgroundPanel.add(painelBotoes);
        backgroundPanel.add(lblValorCartasJogador);
        setContentPane(backgroundPanel);
    }

    private void comecarApostas(){
        setTitle("Blackjack - Fase de Aposta");
        painelBotoes.removeAll();
        int[] valores = {5, 10, 25, 50};
        for (int valor : valores) {
            JButton btn = CustomInput.createButtom("+" + valor, Color.BLACK);
            btn.setFont(new Font("Arial", Font.BOLD, 22));
            btn.setPreferredSize(new Dimension(100, 45));
            btn.addActionListener(e -> adicionarAposta(valor));
            painelBotoes.add(btn);
        }

        JButton btnFinalizar = CustomInput.createButtom("Finalizar Aposta", Color.BLACK);
        btnFinalizar.setFont(new Font("Arial", Font.BOLD, 22));
        btnFinalizar.setPreferredSize(new Dimension(200, 45));
        btnFinalizar.addActionListener(e -> finalizarAposta());
        painelBotoes.add(btnFinalizar);

        painelBotoes.setVisible(true);
        painelBotoes.revalidate();
        painelBotoes.repaint();
    }

    private void botoesGame(){
        painelBotoes.removeAll();
        JButton btnComprar = CustomInput.createButtom("Comprar",Color.BLACK);
        btnComprar.setFont(new Font("Arial", Font.BOLD, 22));
        btnComprar.setPreferredSize(new Dimension(200, 45));
        btnComprar.addActionListener(e -> comprarCarta());
        painelBotoes.add(btnComprar);

        JButton btnManter = CustomInput.createButtom("Manter", Color.BLACK);
        btnManter.setFont(new Font("Arial", Font.BOLD, 22));
        btnManter.setPreferredSize(new Dimension(200, 45));
        btnManter.addActionListener(e -> manterCartas());
        painelBotoes.add(btnManter);

        painelBotoes.setVisible(true);
        painelBotoes.revalidate();
        painelBotoes.repaint();
    }

    private void comprarCarta(){
        this.match.comprar();
    }

    private void manterCartas(){
        painelCartasMesa.removeAll();
        this.match.manter();
    }

    private void adicionarAposta(int valor) {
        int saldo = match.getSaldo();
        if (saldo >= valor) {
            apostaAtual += valor;
            saldo -= valor;
            atualizarLabels();
        } else {
            CustomDialog.showMessage(this, "Saldo insuficiente.", "Erro", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void finalizarAposta() {
        painelBotoes.setVisible(false);
        CustomDialog.showMessage(this, "Aposta de $" + apostaAtual + " finalizada!", "Aposta", JOptionPane.INFORMATION_MESSAGE);
        iniciarFaseDeJogo();
    }

    private void iniciarFaseDeJogo() {
        setTitle("Blackjack - Fase de Jogo");
        match.Init(apostaAtual);
        atualizarLabels();
    }

    private void atualizarLabels() {
        lblSaldo.setText("Saldo: $" + match.getSaldo());
        lblAposta.setText("Apostando: $" + apostaAtual);
    }

    public void cartasBot(String[] carta, boolean show) {
        String path;
        if (show) {
            // ["K", "Copas"] → public/Cartas/Copas/K.png
            path = "public/Cartas/" + carta[1] + "/" + carta[0] + ".png";
        } else {
            path = "public/Cartas/Fundo.png";
        }

        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(100, 145, Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(img));
        label.setPreferredSize(new Dimension(100, 145));

        painelCartasMesa.add(label);
        painelCartasMesa.revalidate();
        painelCartasMesa.repaint();
    }

    public void cartasPlayer(String[] carta, int saldo) {
        String path = "public/Cartas/" + carta[1] + "/" + carta[0] + ".png";

        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(100, 145, Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(img));
        label.setPreferredSize(new Dimension(100, 145));

        painelCartasJogador.add(label);
        painelCartasJogador.revalidate();
        painelCartasJogador.repaint();

        // Atualiza o valor total das cartas do jogador
        lblValorCartasJogador.setText(String.valueOf(saldo));
    }

    public void game() {
        botoesGame();
    }

    public void restart(){
        this.apostaAtual = 0;

        atualizarLabels();

        painelCartasMesa.removeAll();
        painelCartasMesa.revalidate();
        painelCartasMesa.repaint();

        painelCartasJogador.removeAll();
        painelCartasJogador.revalidate();
        painelCartasJogador.repaint();

        lblValorCartasJogador.setText("");
        lblValorCartasJogador.revalidate();
        lblValorCartasJogador.repaint();
        comecarApostas();
    }

    public void open() {
        Point parentLocation = menuFrame.getLocation();
        int x = parentLocation.x + (menuFrame.getWidth() - getWidth()) / 2;
        int y = parentLocation.y + (menuFrame.getHeight() - getHeight()) / 2 - 70;
        setLocation(x, y);
        setVisible(true);
    }

}

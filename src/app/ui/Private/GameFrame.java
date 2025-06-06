package app.ui.Private;

import app.game.Game;
import app.ui.utility.CustomDialog;
import app.ui.utility.PanelImage;
import app.ui.utility.RoundButton;

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
    private final JPanel northPanel;
    private final JPanel centerPanel;
    private final JPanel southOuter;

    private final JButton btnManter;
    private final JButton btnComprar;
    private final JButton btnReset;
    private final JButton btnAposta5;
    private final JButton btnAposta10;
    private final JButton btnAposta25;
    private final JButton btnAposta50;
    private final JButton btnFinalizarAposta;

    private final MenuFrame menuFrame;;

    private int apostaAtual = 0;

    private final Image backgroundImage = new ImageIcon("public/Images/GameFundo.png").getImage();
    private final Game match;

    public GameFrame(MenuFrame parent, String userId) {
        this.match = new Game(this, userId);
        this.menuFrame = parent;

        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Painel de fundo com imagem + overlay
        backgroundPanel = new PanelImage(backgroundImage, true);
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(backgroundPanel);

        // --- NORTH: saldo e botão Sair ---
        northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);

        lblSaldo = new JLabel("Saldo: $" + match.getSaldo(), SwingConstants.LEFT);
        lblSaldo.setFont(new Font("Arial", Font.BOLD, 32));
        lblSaldo.setForeground(Color.WHITE);
        northPanel.add(lblSaldo, BorderLayout.WEST);

        btnSair = new RoundButton("Sair", Color.BLACK);
        btnSair.setPreferredSize(new Dimension(130, 50));
        btnSair.addActionListener(e -> {
            menuFrame.open(this);
            setVisible(false);
        });
        northPanel.add(btnSair, BorderLayout.EAST);

        backgroundPanel.add(northPanel, BorderLayout.NORTH);

        // --- CENTER: cartas da mesa, valor do jogador e cartas do jogador ---
        centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        // Cartas da mesa
        painelCartasMesa = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        painelCartasMesa.setOpaque(false);
        painelCartasMesa.setMaximumSize(new Dimension(700, 150));
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(painelCartasMesa);
        centerPanel.add(Box.createVerticalStrut(20));

        // Valor das cartas do jogador
        lblValorCartasJogador = new JLabel("", SwingConstants.CENTER);
        lblValorCartasJogador.setFont(new Font("Arial", Font.BOLD, 24));
        lblValorCartasJogador.setForeground(Color.WHITE);
        lblValorCartasJogador.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblValorCartasJogador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        centerPanel.add(lblValorCartasJogador);
        centerPanel.add(Box.createVerticalStrut(20));

        // Cartas do jogador
        painelCartasJogador = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        painelCartasJogador.setOpaque(false);
        painelCartasJogador.setMaximumSize(new Dimension(700, 150));
        centerPanel.add(painelCartasJogador);
        centerPanel.add(Box.createVerticalGlue());

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        // --- SOUTH: aposta e botões ---
        southOuter = new JPanel();
        southOuter.setOpaque(false);
        southOuter.setLayout(new BoxLayout(southOuter, BoxLayout.Y_AXIS));

        lblAposta = new JLabel("Apostando: $" + apostaAtual, SwingConstants.CENTER);
        lblAposta.setFont(new Font("Arial", Font.BOLD, 28));
        lblAposta.setForeground(Color.WHITE);
        lblAposta.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblAposta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        southOuter.add(lblAposta);
        southOuter.add(Box.createVerticalStrut(10));

        painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        painelBotoes.setOpaque(false);
        painelBotoes.setMaximumSize(new Dimension(1000, 60));
        southOuter.add(painelBotoes);
        southOuter.add(Box.createVerticalStrut(20));

        backgroundPanel.add(southOuter, BorderLayout.SOUTH);

        // Botões de jogo (comprar/manter), construídos mas só adicionados depois em botoesGame()
        btnComprar = new RoundButton("Comprar", Color.BLACK);
        btnComprar.setPreferredSize(new Dimension(200, 60));
        btnComprar.addActionListener(e -> comprarCarta());

        btnManter = new RoundButton("Manter", Color.BLACK);
        btnManter.setPreferredSize(new Dimension(200, 60));
        btnManter.addActionListener(e -> manterCartas());

        
        // --- Cria os botões de aposta apenas uma vez ---
        btnReset = new RoundButton("Reset", Color.BLACK);
        btnReset.setPreferredSize(new Dimension(120, 60));
        btnReset.addActionListener(e -> {
            this.apostaAtual = 0;
            atualizarLabels();
        });

        btnAposta5 = new RoundButton("+5", Color.BLACK);
        btnAposta5.setPreferredSize(new Dimension(100, 60));
        btnAposta5.addActionListener(e -> adicionarAposta(5));

        btnAposta10 = new RoundButton("+10", Color.BLACK);
        btnAposta10.setPreferredSize(new Dimension(100, 60));
        btnAposta10.addActionListener(e -> adicionarAposta(10));

        btnAposta25 = new RoundButton("+25", Color.BLACK);
        btnAposta25.setPreferredSize(new Dimension(100, 60));
        btnAposta25.addActionListener(e -> adicionarAposta(25));

        btnAposta50 = new RoundButton("+50", Color.BLACK);
        btnAposta50.setPreferredSize(new Dimension(100, 60));
        btnAposta50.addActionListener(e -> adicionarAposta(50));

        btnFinalizarAposta = new RoundButton("Finalizar Aposta", Color.BLACK);
        btnFinalizarAposta.setPreferredSize(new Dimension(300, 60));
        btnFinalizarAposta.addActionListener(e -> finalizarAposta());

        // Prepare botões de aposta
        comecarApostas();
    }


    private void comecarApostas(){
        setTitle("Blackjack - Fase de Aposta");
        painelBotoes.removeAll();
        // Adiciona cada botão preparado anteriormente
        painelBotoes.add(btnReset);
        painelBotoes.add(btnAposta5);
        painelBotoes.add(btnAposta10);
        painelBotoes.add(btnAposta25);
        painelBotoes.add(btnAposta50);
        painelBotoes.add(btnFinalizarAposta);

        painelBotoes.setVisible(true);
        painelBotoes.revalidate();
        painelBotoes.repaint();
    }

    private void botoesGame(){
        painelBotoes.removeAll();

        painelBotoes.add(btnComprar);
        painelBotoes.add(btnManter);

        painelBotoes.setVisible(true);
        painelBotoes.revalidate();
        painelBotoes.repaint();
    }

    private void comprarCarta(){
        this.match.comprar();
    }

    private void manterCartas(){
        painelBotoes.setVisible(false);
        painelCartasMesa.removeAll();
        this.match.manter();
    }

    private void adicionarAposta(int valor) {
        if (match.getSaldo() >= (apostaAtual + valor)) {
            apostaAtual += valor;
            atualizarLabels();
        } else {
            CustomDialog.showMessage(this, "Saldo insuficiente.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void finalizarAposta() {
        if (apostaAtual == 0) {
            CustomDialog.showMessage(this, "Sem Aposta, sem Jogo!", "Erro", JOptionPane.ERROR_MESSAGE);
        } else {
            painelBotoes.setVisible(false);
            CustomDialog.showMessage(this, "Aposta de $" + apostaAtual + " finalizada!", "Aposta", JOptionPane.INFORMATION_MESSAGE);
            iniciarFaseDeJogo();
        }
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

    public void cartasPlayer(String[] carta, int saldoCartas) {
        String path = "public/Cartas/" + carta[1] + "/" + carta[0] + ".png";

        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(100, 145, Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(img));
        label.setPreferredSize(new Dimension(100, 145));

        painelCartasJogador.add(label);
        painelCartasJogador.revalidate();
        painelCartasJogador.repaint();

        // Atualiza o valor total das cartas do jogador
        lblValorCartasJogador.setText(String.valueOf(saldoCartas));
        if (saldoCartas >= 21) {
            painelBotoes.removeAll();
            painelBotoes.add(btnManter);
            painelBotoes.revalidate();
            painelBotoes.repaint();
        }
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

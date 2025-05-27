import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Classe de GUI para o jogo de Blackjack.
 * Exibe mensagens, cartas e captura ações do usuário via listener.
 */
public class BlackjackUI extends JFrame implements ActionListener {
    // Interface para receber eventos de "Comprar" e "Parar"
    public interface BlackjackUIListener {
        void onHit();   // jogador clicou em "Comprar"
        void onStand(); // jogador clicou em "Parar"
    }

    private final JTextArea logArea = new JTextArea(10, 30);
    private final JPanel   cardPanel = new JPanel(new GridLayout(0, 5, 5, 5));
    private final JButton  hitButton = new JButton("Comprar");
    private final JButton  standButton = new JButton("Parar");

    private BlackjackUIListener listener;

    public BlackjackUI() {
        super("Blackjack");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));
        // Área de log
        logArea.setEditable(false);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        // Painel de cartas (para face up)
        cardPanel.setBorder(BorderFactory.createTitledBorder("Cartas na mesa"));
        add(cardPanel, BorderLayout.NORTH);

        // Botões de ação
        JPanel buttonPane = new JPanel();
        hitButton.addActionListener(this);
        standButton.addActionListener(this);
        buttonPane.add(hitButton);
        buttonPane.add(standButton);
        add(buttonPane, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        enableControls(false);
    }

    /** Registra quem vai ouvir os eventos de hit/stand */
    public void setListener(BlackjackUIListener l) {
        this.listener = l;
    }

    /** Habilita ou desabilita os botões de ação */
    public void enableControls(boolean enable) {
        hitButton.setEnabled(enable);
        standButton.setEnabled(enable);
    }

    /** Exibe texto no log */
    public void displayMessage(String msg) {
        logArea.append(msg + "\n");
    }

    /** Mostra uma carta virada para cima no painel */
    public void displayFaceUpCard(String playerName, Card card) {
        JLabel lbl = new JLabel(playerName + ": " + card);
        lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        cardPanel.add(lbl);
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    /** Informa que o jogador comprou uma carta (virada para baixo) */
    public void displayNewCard(String playerName) {
        displayMessage(playerName + " comprou uma carta virada para baixo.");
    }

    /** Revela a mão completa ao fim */
    public void revealHand(String playerName, List<Card> cards, int score) {
        StringBuilder sb = new StringBuilder(playerName + " finalizou com " + score + " pontos: ");
        for (Card c : cards) sb.append("[").append(c).append("] ");
        displayMessage(sb.toString());
    }

    /** Captura clique em Comprar ou Parar e notifica o listener */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (listener == null) return;
        if (e.getSource() == hitButton) {
            enableControls(false);
            listener.onHit();
        } else if (e.getSource() == standButton) {
            enableControls(false);
            listener.onStand();
        }
    }
}

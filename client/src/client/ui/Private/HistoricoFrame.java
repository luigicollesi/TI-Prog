package client.ui.Private;

import client.model.HistoryEntryModel;
import client.net.ServerConnection;
import client.net.ServerListener;
import client.ui.utility.PanelImage;
import client.ui.utility.RoundButton;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class HistoricoFrame extends JFrame implements ServerListener {
    private final MenuFrame menuFrame;
    private final ServerConnection connection;

    private final PanelImage backgroundPanel;
    private final JPanel centerPanel;
    private final JScrollPane scrollPane;
    private final JButton btnVoltar;

    private final DetalheFrame detalheFrame;

    private final ImageIcon backgroundImage = new ImageIcon("public/Images/GameFundo2.png");

    public HistoricoFrame(MenuFrame parent, ServerConnection connection, String userId) {
        this.menuFrame = parent;
        this.connection = connection;
        this.connection.addListener(this);

        detalheFrame = new DetalheFrame(this);

        setTitle("Histórico de Partidas");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        backgroundPanel = new PanelImage(backgroundImage.getImage(), true);
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(centerPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        bottomPanel.setOpaque(false);

        btnVoltar = new RoundButton("Voltar", Color.BLACK);
        btnVoltar.setPreferredSize(new Dimension(250, 60));
        btnVoltar.addActionListener(e -> {
            detalheFrame.dispose();
            menuFrame.open(this);
            setVisible(false);
        });

        bottomPanel.add(btnVoltar);
        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(backgroundPanel);
    }

    public void open() {
        centerPanel.removeAll();
        centerPanel.add(new javax.swing.JLabel("Carregando histórico..."));
        centerPanel.revalidate();
        centerPanel.repaint();

        connection.requestHistory();

        Point parentLocation = menuFrame.getLocation();
        int x = parentLocation.x + (menuFrame.getWidth() - getWidth()) / 2;
        int y = parentLocation.y + (menuFrame.getHeight() - getHeight()) / 2 - 70;
        setLocation(x, y);
        setVisible(true);
    }

    @Override
    public void onHistoryLoaded(List<HistoryEntryModel> entries) {
        if (!isVisible()) {
            return;
        }

        centerPanel.removeAll();

        if (entries.isEmpty()) {
            javax.swing.JLabel empty = new javax.swing.JLabel("Nenhum histórico encontrado.");
            empty.setForeground(Color.WHITE);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(Box.createVerticalGlue());
            centerPanel.add(empty);
            centerPanel.add(Box.createVerticalGlue());
        } else {
            for (HistoryEntryModel entry : entries) {
                Boolean resultado = entry.getWon();
                int valor = entry.getValue();

                String texto;
                JButton btn;

                if (Boolean.TRUE.equals(resultado)) {
                    texto = "Ganhou  +$" + valor;
                    btn = new RoundButton(texto, new Color(0, 190, 0));
                } else if (Boolean.FALSE.equals(resultado)) {
                    texto = "Perdeu  -$" + valor;
                    btn = new RoundButton(texto, new Color(220, 50, 50));
                } else {
                    texto = "Empate  $" + valor;
                    Color textColor = new Color(215, 215, 215);
                    Color normalBg = new Color(70, 70, 70, 220);
                    Color hoverBg = new Color(98, 98, 98, 240);
                    btn = new RoundButton(texto, textColor, normalBg, hoverBg);
                }

                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                btn.setMaximumSize(new Dimension(300, 60));
                btn.addActionListener(e -> detalheFrame.mostrar(entry.getPlayerCardsJson(), entry.getDealerCardsJson()));

                centerPanel.add(btn);
                centerPanel.add(Box.createVerticalStrut(15));
            }
        }

        centerPanel.revalidate();
        centerPanel.repaint();
    }

    @Override
    public void onConnectionClosed() {
        if (isVisible()) {
            dispose();
        }
    }

    @Override
    public void dispose() {
        connection.removeListener(this);
        super.dispose();
    }
}

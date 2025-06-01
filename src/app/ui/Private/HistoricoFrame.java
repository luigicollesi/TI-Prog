package app.ui.Private;


import app.db.DatabaseOperations;
import app.ui.utility.CustomInput;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HistoricoFrame extends JFrame {

    private final Image backgroundImage = new ImageIcon("public/Images/GameFundo2.png").getImage();
    private final MenuFrame menuFrame;
    private final JPanel backgroundPanel;
    private final JScrollPane scrollPane;
    private final JPanel centerPanel;
    private final JButton btnVoltar;
    private final JPanel bottomPanel;

    private final DetalheFrame detalheFrame;
    private final String userId;

    public HistoricoFrame(MenuFrame parent, String userId) {
        setTitle("Histórico de Partidas");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.userId = userId;

        detalheFrame = new DetalheFrame(this);
        menuFrame = parent;

        backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        try {
            ResultSet rs = DatabaseOperations.executeQuery(
                "SELECT * FROM historico_partidas WHERE user_id = ? ORDER BY created_at DESC;",
                new String[]{userId}
            );

            while (rs != null && rs.next()) {
                boolean venceu = rs.getInt("venceu") == 1;
                int valor = rs.getInt("valor");
                String cartasPlayer = rs.getString("cartas_player");
                String cartasBot = rs.getString("cartas_bot");

                String texto = (venceu ? "Ganhou  +" : "Perdeu  -") + "$" + valor;
                JButton btn = CustomInput.createButtom(texto, venceu ? new Color(0, 190, 0) : new Color(220, 50, 50));
                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                btn.setMaximumSize(new Dimension(250, 60));
                btn.addActionListener(e -> detalheFrame.mostrar(cartasPlayer, cartasBot));

                centerPanel.add(btn);
                centerPanel.add(Box.createVerticalStrut(15));
            }

            if (rs != null) rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        scrollPane = new JScrollPane(centerPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        btnVoltar = CustomInput.createButtom("Voltar", Color.BLACK);
        btnVoltar.setPreferredSize(new Dimension(250, 60));
        btnVoltar.addActionListener(e -> {
            detalheFrame.dispose();
            menuFrame.open(this);
            setVisible(false);
        });

        bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnVoltar);
        backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(backgroundPanel);
    }

    public void open() {
        centerPanel.removeAll();

        try {
            ResultSet rs = DatabaseOperations.executeQuery(
                "SELECT * FROM historico_partidas WHERE user_id = ? ORDER BY created_at DESC;",
                new String[]{userId}
            );

            while (rs != null && rs.next()) {
                boolean venceu = rs.getInt("venceu") == 1;
                int valor = rs.getInt("valor");
                String cartasPlayer = rs.getString("cartas_player");
                String cartasBot = rs.getString("cartas_bot");

                String texto = (venceu ? "Ganhou  +" : "Perdeu  -") + "$" + valor;
                JButton btn = CustomInput.createButtom(texto, venceu ? new Color(0, 190, 0) : new Color(220, 50, 50));
                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                btn.setMaximumSize(new Dimension(250, 60));
                btn.addActionListener(e -> detalheFrame.mostrar(cartasPlayer, cartasBot));

                centerPanel.add(btn);
                centerPanel.add(Box.createVerticalStrut(15));
            }

            if (rs != null) rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Point parentLocation = menuFrame.getLocation();
        int x = parentLocation.x + (menuFrame.getWidth() - getWidth()) / 2;
        int y = parentLocation.y + (menuFrame.getHeight() - getHeight()) / 2 - 70;
        setLocation(x, y);
        setVisible(true);
    }
}

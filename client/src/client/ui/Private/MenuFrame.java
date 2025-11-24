package client.ui.Private;

import client.net.ServerConnection;
import client.net.ServerListener;
import client.ui.Auth.LoginFrame;
import client.i18n.I18n;
import client.i18n.I18n.Lang;
import client.ui.utility.CustomDialog;
import client.ui.utility.PanelImage;
import client.ui.utility.RoundButton;

import javax.swing.*;
import java.awt.*;

public class MenuFrame extends JFrame implements ServerListener {
    private final ServerConnection connection;
    private final LoginFrame loginFrame;

    private final GameFrame gameFrame;
    private final HistoricoFrame historicoFrame;

    private final ImageIcon backgroundImage = new ImageIcon("public/Images/Fundo.png");

    private final RoundButton btnExcluirConta;
    private final RoundButton btnJogar;
    private final RoundButton btnHistorico;
    private final RoundButton btnDeslogar;
    private final RoundButton btnSair;
    private final JLabel lblPerfil;
    private boolean awaitingAccountDeletion = false;
    private boolean accountDeleted = false;
    private final JComboBox<Lang> languageSelector;

    public MenuFrame(LoginFrame parent, ServerConnection connection, String userId, String username, int balance) {
        this.connection = connection;
        this.loginFrame = parent;
        this.connection.addListener(this);

        gameFrame = new GameFrame(this, connection, userId, username, balance);
        historicoFrame = new HistoricoFrame(this, connection, userId);

        languageSelector = new JComboBox<>(Lang.values());
        languageSelector.setFont(new Font("Arial", Font.BOLD, 24));
        languageSelector.setSelectedItem(I18n.getLanguage());
        languageSelector.addActionListener(e -> {
            I18n.setLanguage((Lang) languageSelector.getSelectedItem());
            applyTranslations(username);
            gameFrame.applyTranslations();
            historicoFrame.applyTranslations();
        });

        setTitle(I18n.get("menu.title"));
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Point parentLocation = parent.getLocation();
        int x = parentLocation.x + (parent.getWidth() - getWidth()) / 2;
        int y = parentLocation.y + (parent.getHeight() - getHeight()) / 2 - 60;
        setLocation(x, y);

        PanelImage backgroundPanel = new PanelImage(backgroundImage.getImage(), true);
        backgroundPanel.setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        topBar.setOpaque(false);
        languageSelector.setPreferredSize(new Dimension(220, 60));
        topBar.add(languageSelector);
        backgroundPanel.add(topBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JPanel messageWrapper = new JPanel();
        messageWrapper.setOpaque(false);
        messageWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageWrapper.setLayout(new BoxLayout(messageWrapper, BoxLayout.Y_AXIS));

        JPanel messageBox = new JPanel();
        messageBox.setBackground(new Color(20, 20, 20, 200));
        messageBox.setOpaque(true);
        messageBox.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));
        messageBox.setLayout(new BoxLayout(messageBox, BoxLayout.Y_AXIS));

        lblPerfil = new JLabel(I18n.get("menu.message", username));
        lblPerfil.setFont(new Font("Serif", Font.BOLD, 34));
        lblPerfil.setForeground(new Color(230, 230, 230));
        lblPerfil.setHorizontalAlignment(SwingConstants.CENTER);
        lblPerfil.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageBox.add(lblPerfil);

        messageWrapper.add(messageBox);

        btnJogar = new RoundButton(I18n.get("menu.play"), Color.BLACK);
        btnJogar.setAlignmentX(CENTER_ALIGNMENT);
        btnJogar.setFont(new Font("Arial", Font.BOLD, 35));
        btnJogar.setMaximumSize(new Dimension(400, 120));
        btnJogar.addActionListener(e -> {
            gameFrame.open();
            setVisible(false);
        });

        btnHistorico = new RoundButton(I18n.get("menu.history"), Color.BLACK);
        btnHistorico.setAlignmentX(CENTER_ALIGNMENT);
        btnHistorico.setFont(new Font("Arial", Font.BOLD, 35));
        btnHistorico.setMaximumSize(new Dimension(400, 120));
        btnHistorico.addActionListener(e -> {
            historicoFrame.open();
            setVisible(false);
        });

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(messageWrapper);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 35)));
        centerPanel.add(btnJogar);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        centerPanel.add(btnHistorico);
        centerPanel.add(Box.createVerticalGlue());

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftButtons.setOpaque(false);

        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        rightButtons.setOpaque(false);

        btnExcluirConta = new RoundButton(
            I18n.get("menu.delete"),
            Color.WHITE,
            new Color(180, 40, 40, 230),
            new Color(210, 60, 60, 240)
        );
        btnExcluirConta.setPreferredSize(new Dimension(250, 60));
        btnExcluirConta.addActionListener(e -> attemptAccountDeletion());
        leftButtons.add(btnExcluirConta);

        btnDeslogar = new RoundButton(I18n.get("menu.logout"), Color.BLACK);
        btnDeslogar.setPreferredSize(new Dimension(250, 60));
        btnDeslogar.addActionListener(e -> {
            connection.logout();
            setVisible(false);
            loginFrame.open(this);
            gameFrame.dispose();
            historicoFrame.dispose();
            dispose();
        });

        btnSair = new RoundButton(I18n.get("menu.exit"), Color.BLACK);
        btnSair.setPreferredSize(new Dimension(250, 60));
        btnSair.addActionListener(e -> {
            connection.logout();
            System.exit(0);
        });

        rightButtons.add(btnDeslogar);
        rightButtons.add(btnSair);

        southPanel.add(leftButtons, BorderLayout.WEST);
        southPanel.add(rightButtons, BorderLayout.EAST);
        backgroundPanel.add(southPanel, BorderLayout.SOUTH);

        setContentPane(backgroundPanel);
        applyTranslations(username);
    }

    public void open(JFrame frame) {
        Point parentLocation = frame.getLocation();
        int x = parentLocation.x + (frame.getWidth() - getWidth()) / 2;
        int y = parentLocation.y + (frame.getHeight() - getHeight()) / 2 - 70;
        setLocation(x, y);
        setVisible(true);
    }

    private void attemptAccountDeletion() {
        if (awaitingAccountDeletion) {
            return;
        }
        int option = CustomDialog.showConfirm(
            this,
            I18n.get("menu.confirm.delete.msg"),
            I18n.get("menu.confirm.delete.title")
        );
        if (option == JOptionPane.YES_OPTION) {
            awaitingAccountDeletion = true;
            btnExcluirConta.setEnabled(false);
            connection.deleteAccount();
        }
    }

    @Override
    public void onBalanceUpdate(int balance) {
        gameFrame.updateBalance(balance);
    }

    @Override
    public void onErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, I18n.get("dialog.error"), JOptionPane.ERROR_MESSAGE);
        if (awaitingAccountDeletion) {
            awaitingAccountDeletion = false;
            btnExcluirConta.setEnabled(true);
        }
    }

    @Override
    public void onAccountDeleted() {
        awaitingAccountDeletion = false;
        accountDeleted = true;
        btnExcluirConta.setEnabled(true);
        CustomDialog.showMessage(this, I18n.get("menu.delete.ok"), I18n.get("dialog.success"), JOptionPane.INFORMATION_MESSAGE);
        setVisible(false);
        gameFrame.dispose();
        historicoFrame.dispose();
        loginFrame.open(this);
        dispose();
    }

    @Override
    public void onConnectionClosed() {
        if (accountDeleted) {
            return;
        }
        JOptionPane.showMessageDialog(this, I18n.get("connection.lost"), I18n.get("dialog.error"), JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    @Override
    public void dispose() {
        connection.removeListener(this);
        super.dispose();
    }

    private void applyTranslations(String username) {
        setTitle(I18n.get("menu.title"));
        JPanel centerPanel = (JPanel) ((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);
        JPanel messageWrapper = (JPanel) centerPanel.getComponent(1);
        JPanel messageBox = (JPanel) messageWrapper.getComponent(0);
        lblPerfil.setText(I18n.get("menu.message", username));

        btnJogar.setText(I18n.get("menu.play"));
        btnHistorico.setText(I18n.get("menu.history"));

        btnExcluirConta.setText(I18n.get("menu.delete"));
        btnDeslogar.setText(I18n.get("menu.logout"));
        btnSair.setText(I18n.get("menu.exit"));
    }
}

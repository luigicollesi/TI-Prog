package client.ui.Auth;

import client.net.ServerConnection;
import client.net.ServerListener;
import client.ui.Private.MenuFrame;
import client.i18n.I18n;
import client.i18n.I18n.Lang;
import client.ui.utility.CustomDialog;
import client.ui.utility.OutlineLabel;
import client.ui.utility.PanelImage;
import client.ui.utility.RoundButton;
import client.ui.utility.RoundPassF;
import client.ui.utility.RoundTextF;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;

public class LoginFrame extends JFrame implements ServerListener {
    private static final int MAX_PASSWORD_BYTES = 256;

    private final ServerConnection connection;

    private final JPanel centralPanel;
    private final JPanel panelNorth;
    private final JPanel buttonPanel;
    private final JPanel fieldPanel;

    private final RoundTextF txtUser;
    private final RoundPassF txtPass;

    private final CreateAccountFrame createAccountFrame;

    private final Image backgroundImage = new ImageIcon("public/Images/LoginFundo.png").getImage();
    private final JComboBox<Lang> languageSelector;

    public LoginFrame(ServerConnection connection) {
        this.connection = connection;
        this.connection.addListener(this);

        setTitle(I18n.get("login.title"));
        setSize(1000, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        createAccountFrame = new CreateAccountFrame(this, connection);

        PanelImage backgroundPanel = new PanelImage(backgroundImage, false);
        backgroundPanel.setLayout(new GridBagLayout());

        centralPanel = new JPanel(new BorderLayout(10, 10));
        centralPanel.setOpaque(false);
        centralPanel.setPreferredSize(new Dimension(600, 400));

        JLabel lblTitulo = new OutlineLabel(I18n.get("login.header"), new Font("Arial", Font.BOLD, 36), Color.WHITE);

        JButton btnExit = new RoundButton(I18n.get("login.btn.exit"), Color.black);
        btnExit.setPreferredSize(new Dimension(120, 50));
        btnExit.addActionListener(e -> {
            connection.logout();
            System.exit(0);
        });

        languageSelector = new JComboBox<>(Lang.values());
        languageSelector.setFont(new Font("Arial", Font.BOLD, 24));
        languageSelector.setSelectedItem(I18n.getLanguage());
        languageSelector.addActionListener(e -> {
            I18n.setLanguage((Lang) languageSelector.getSelectedItem());
            applyTranslations();
            createAccountFrame.applyTranslations();
        });

        panelNorth = new JPanel(new BorderLayout());
        panelNorth.setOpaque(false);
        panelNorth.add(lblTitulo, BorderLayout.WEST);
        JPanel northRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        northRight.setOpaque(false);
        languageSelector.setPreferredSize(new Dimension(220, 60));
        northRight.add(languageSelector);
        northRight.add(btnExit);
        panelNorth.add(northRight, BorderLayout.EAST);
        centralPanel.add(panelNorth, BorderLayout.NORTH);

        fieldPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        fieldPanel.setOpaque(false);

        JLabel lblUser = new OutlineLabel(I18n.get("login.user"), new Font("Arial", Font.PLAIN, 30), Color.WHITE);
        txtUser = new RoundTextF();
        txtUser.setFont(new Font("Arial", Font.PLAIN, 28));

        JLabel lblPass = new OutlineLabel(I18n.get("login.pass"), new Font("Arial", Font.PLAIN, 30), Color.WHITE);
        txtPass = new RoundPassF();
        txtPass.setFont(new Font("Arial", Font.PLAIN, 28));

        fieldPanel.add(lblUser);
        fieldPanel.add(txtUser);
        fieldPanel.add(lblPass);
        fieldPanel.add(txtPass);
        centralPanel.add(fieldPanel, BorderLayout.CENTER);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton btnLogin = new RoundButton(I18n.get("login.btn.login"), Color.BLACK);
        btnLogin.setPreferredSize(new Dimension(200, 60));
        btnLogin.addActionListener(e -> attemptLogin());

        JButton btnCreate = new RoundButton(I18n.get("login.btn.create"), Color.BLACK);
        btnCreate.setPreferredSize(new Dimension(250, 60));
        btnCreate.addActionListener(e -> {
            createAccountFrame.setLocationRelativeTo(this);
            createAccountFrame.setVisible(true);
        });

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCreate);
        centralPanel.add(buttonPanel, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(0, 0, 120, 0);

        backgroundPanel.add(centralPanel, gbc);
        setContentPane(backgroundPanel);
        applyTranslations();
    }

    private void attemptLogin() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            CustomDialog.showMessage(this, I18n.get("login.msg.missing"), I18n.get("dialog.warning"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES) {
            CustomDialog.showMessage(this, I18n.get("login.pass.toolong"), I18n.get("dialog.warning"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        connection.login(username, password);
    }

    private void applyTranslations() {
        setTitle(I18n.get("login.title"));
        ((OutlineLabel) panelNorth.getComponent(0)).setText(I18n.get("login.header"));
        JPanel northRight = (JPanel) panelNorth.getComponent(1);
        ((JButton) northRight.getComponent(1)).setText(I18n.get("login.btn.exit"));
        ((JLabel) fieldPanel.getComponent(0)).setText(I18n.get("login.user"));
        ((JLabel) fieldPanel.getComponent(2)).setText(I18n.get("login.pass"));
        ((JButton) buttonPanel.getComponent(0)).setText(I18n.get("login.btn.login"));
        ((JButton) buttonPanel.getComponent(1)).setText(I18n.get("login.btn.create"));
    }

    @Override
    public void onLoginSuccess(String userId, String username, int balance) {
        MenuFrame menuFrame = new MenuFrame(this, connection, userId, username, balance);
        CustomDialog.showMessage(menuFrame, I18n.get("login.success"), I18n.get("dialog.success"), JOptionPane.INFORMATION_MESSAGE);
        setVisible(false);
        menuFrame.setVisible(true);
    }

    @Override
    public void onLoginFailure(String message) {
        CustomDialog.showMessage(this, message, I18n.get("dialog.error"), JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void onConnectionClosed() {
        CustomDialog.showMessage(this, I18n.get("login.connection.closed"), I18n.get("dialog.error"), JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    public void open(JFrame parent) {
        Point parentLocation = parent.getLocation();
        int x = parentLocation.x + (parent.getWidth() - getWidth()) / 2;
        int y = parentLocation.y + (parent.getHeight() - getHeight()) / 2 - 70;
        setLocation(x, y);
        setVisible(true);
    }

    @Override
    public void dispose() {
        connection.removeListener(this);
        super.dispose();
    }
}

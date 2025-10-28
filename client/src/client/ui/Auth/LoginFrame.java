package client.ui.Auth;

import client.net.ServerConnection;
import client.net.ServerListener;
import client.ui.Private.MenuFrame;
import client.ui.utility.CustomDialog;
import client.ui.utility.OutlineLabel;
import client.ui.utility.PanelImage;
import client.ui.utility.RoundButton;
import client.ui.utility.RoundPassF;
import client.ui.utility.RoundTextF;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame implements ServerListener {
    private final ServerConnection connection;

    private final JPanel centralPanel;
    private final JPanel panelNorth;
    private final JPanel buttonPanel;
    private final JPanel fieldPanel;

    private final RoundTextF txtUser;
    private final RoundPassF txtPass;

    private final CreateAccountFrame createAccountFrame;

    private final Image backgroundImage = new ImageIcon("public/Images/LoginFundo.png").getImage();

    public LoginFrame(ServerConnection connection) {
        this.connection = connection;
        this.connection.addListener(this);

        setTitle("Blackjack - Login");
        setSize(1000, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        createAccountFrame = new CreateAccountFrame(this, connection);

        PanelImage backgroundPanel = new PanelImage(backgroundImage, false);
        backgroundPanel.setLayout(new GridBagLayout());

        centralPanel = new JPanel(new BorderLayout(10, 10));
        centralPanel.setOpaque(false);
        centralPanel.setPreferredSize(new Dimension(600, 400));

        JLabel lblTitulo = new OutlineLabel("Faça login", new Font("Arial", Font.BOLD, 36), Color.WHITE);

        JButton btnExit = new RoundButton("Sair", Color.black);
        btnExit.setPreferredSize(new Dimension(120, 50));
        btnExit.addActionListener(e -> {
            connection.logout();
            System.exit(0);
        });

        panelNorth = new JPanel(new BorderLayout());
        panelNorth.setOpaque(false);
        panelNorth.add(lblTitulo, BorderLayout.WEST);
        panelNorth.add(btnExit, BorderLayout.EAST);
        centralPanel.add(panelNorth, BorderLayout.NORTH);

        fieldPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        fieldPanel.setOpaque(false);

        JLabel lblUser = new OutlineLabel("Usuário:", new Font("Arial", Font.PLAIN, 30), Color.WHITE);
        txtUser = new RoundTextF();
        txtUser.setFont(new Font("Arial", Font.PLAIN, 28));

        JLabel lblPass = new OutlineLabel("Senha:", new Font("Arial", Font.PLAIN, 30), Color.WHITE);
        txtPass = new RoundPassF();
        txtPass.setFont(new Font("Arial", Font.PLAIN, 28));

        fieldPanel.add(lblUser);
        fieldPanel.add(txtUser);
        fieldPanel.add(lblPass);
        fieldPanel.add(txtPass);
        centralPanel.add(fieldPanel, BorderLayout.CENTER);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton btnLogin = new RoundButton("Login", Color.BLACK);
        btnLogin.setPreferredSize(new Dimension(200, 60));
        btnLogin.addActionListener(e -> attemptLogin());

        JButton btnCreate = new RoundButton("Criar Conta", Color.BLACK);
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
    }

    private void attemptLogin() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            CustomDialog.showMessage(this, "Informe usuário e senha.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        connection.login(username, password);
    }

    @Override
    public void onLoginSuccess(String userId, String username, int balance) {
        MenuFrame menuFrame = new MenuFrame(this, connection, userId, username, balance);
        CustomDialog.showMessage(menuFrame, "Login bem-sucedido!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        setVisible(false);
        menuFrame.setVisible(true);
    }

    @Override
    public void onLoginFailure(String message) {
        CustomDialog.showMessage(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void onConnectionClosed() {
        CustomDialog.showMessage(this, "Conexão com o servidor encerrada.", "Erro", JOptionPane.ERROR_MESSAGE);
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

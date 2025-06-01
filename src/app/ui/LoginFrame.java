package app.ui;

import javax.swing.*;

import app.db.DatabaseOperations;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private final JPanel centralPanel;
    private final JPanel panelNorth;
    private final JPanel buttonPanel;
    private final JPanel fieldPanel;

    private final JLabel lblTitulo;
    private final JLabel lblUser;
    protected final JTextField txtUser;
    private final JLabel lblPass;
    protected final JPasswordField txtPass;
    private final JButton btnLogin;
    private final JButton btnCreate;
    private final JButton btnExit;
    private final JPanel backgroundPanel;

    private final CreateAccountFrame createAccountFrame;

    private final Image backgroundImage = new ImageIcon("public/Images/LoginFundo.png").getImage();

    public LoginFrame() {
        setTitle("Blackjack - Login");
        setSize(1000, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        createAccountFrame = new CreateAccountFrame(this);

        backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        centralPanel = new JPanel();
        centralPanel.setLayout(new BorderLayout(10, 10));
        centralPanel.setOpaque(false);
        centralPanel.setPreferredSize(new Dimension(600, 400));

        // Cabeçalho
        lblTitulo = CustomInput.createOutlinedLabel(
            "Faça login",
            new Font("Arial", Font.BOLD, 36),
            Color.WHITE
        );

        btnExit = CustomInput.createButtom("Sair", Color.BLACK);
        btnExit.setPreferredSize(new Dimension(120, 50));
        btnExit.addActionListener(e -> System.exit(0));

        panelNorth = new JPanel(new BorderLayout());
        panelNorth.setOpaque(false);
        panelNorth.add(lblTitulo, BorderLayout.WEST);
        panelNorth.add(btnExit, BorderLayout.EAST);
        centralPanel.add(panelNorth, BorderLayout.NORTH);

        // Campos
        fieldPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        fieldPanel.setOpaque(false);

        lblUser = CustomInput.createOutlinedLabel(
            "Usuário:",
            new Font("Arial", Font.PLAIN, 30),
            Color.WHITE
        );
        txtUser = CustomInput.createTextField();
        txtUser.setFont(new Font("Arial", Font.PLAIN, 28));

        lblPass = CustomInput.createOutlinedLabel(
            "Senha:",
            new Font("Arial", Font.PLAIN, 30),
            Color.WHITE
        );
        txtPass = CustomInput.createTextFieldPass();
        txtPass.setFont(new Font("Arial", Font.PLAIN, 28));

        fieldPanel.add(lblUser); 
        fieldPanel.add(txtUser);
        fieldPanel.add(lblPass); 
        fieldPanel.add(txtPass);
        centralPanel.add(fieldPanel, BorderLayout.CENTER);

        // Botões
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        btnLogin = CustomInput.createButtom("Login", Color.BLACK);
        btnLogin.setPreferredSize(new Dimension(200, 60));
        btnLogin.addActionListener(e -> LoginAtempt());

        btnCreate = CustomInput.createButtom("Criar Conta", Color.BLACK);
        btnCreate.setPreferredSize(new Dimension(250, 60));
        btnCreate.addActionListener(e -> {
            // Abre a nova classe CreateAccountFrame
            createAccountFrame.setLocationRelativeTo(this);
            createAccountFrame.setVisible(true);
        });

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCreate);
        centralPanel.add(buttonPanel, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 120, 0);

        backgroundPanel.add(centralPanel, gbc);
        setContentPane(backgroundPanel);
        setVisible(true);
    }

    private void LoginAtempt() {
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());
        ResultSet rs = DatabaseOperations.executeQuery(
            "SELECT id FROM usuarios WHERE user_name = ? AND senha = ?",
            new String[]{ user, pass }
        );
        try {
            if (rs != null && rs.next()) {
                String userId = rs.getString("id");
                rs.getStatement().getConnection().close();

                // Abre MenuFrame e oculta LoginFrame
                MenuFrame menuFrame = new MenuFrame(this, userId);
                setVisible(false);

                CustomDialog.showMessage(
                    menuFrame,
                    "Login bem-sucedido!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                CustomDialog.showMessage(
                    this,
                    "Credenciais inválidas.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void open(JFrame frame) {
        Point parentLocation = frame.getLocation();
        int x = parentLocation.x + (frame.getWidth() - getWidth()) / 2;
        int y = parentLocation.y + (frame.getHeight() - getHeight()) / 2 - 70;
        setLocation(x, y);
        setVisible(true);
    }
}

package app.ui;

import javax.swing.*;

import app.db.DatabaseOperations;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Tela inicial de login e criação de conta com botão Sair e fontes maiores.
 */
public class LoginFrame extends JFrame {
    private JPanel centralPanel;
    private JPanel panelNorth;
    private JPanel buttonPanel;
    private JPanel fieldPanel;

    private JLabel lblTitulo;
    private JLabel lblUser;
    protected JTextField txtUser;
    private JLabel lblPass;
    protected JPasswordField txtPass;
    private JButton btnLogin;
    private JButton btnCreate;
    private JButton btnExit;
    private JPanel backgroundPanel;

    private Image backgroundImage = new ImageIcon("public/Images/LoginFundo.png").getImage();

    public LoginFrame() {
        setTitle("Blackjack - Login");
        setSize(1000, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

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

        fieldPanel.add(lblUser); fieldPanel.add(txtUser);
        fieldPanel.add(lblPass); fieldPanel.add(txtPass);
        centralPanel.add(fieldPanel, BorderLayout.CENTER);

        // Botões
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        btnLogin = CustomInput.createButtom("Login", Color.BLACK);
        btnLogin.setPreferredSize(new Dimension(200, 60));
        btnCreate = CustomInput.createButtom("Criar Conta", Color.BLACK);
        btnCreate.setPreferredSize(new Dimension(250, 60));

        btnLogin.addActionListener(e -> LoginAtempt());
        btnCreate.addActionListener(e -> showCreateAccountDialog());

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCreate);
        centralPanel.add(buttonPanel, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 120, 0);   // top, left, bottom, right

        backgroundPanel.add(centralPanel, gbc);
        setContentPane(backgroundPanel);
        setVisible(true);
    }

    private void LoginAtempt() {
        // Somente login
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());
        ResultSet rs = DatabaseOperations.executeQuery(
            "SELECT id FROM usuarios WHERE user_name=? AND senha=?",
            new String[]{user, pass}
        );
        try {
            if (rs != null && rs.next()) {
                String userId = rs.getString("id");
                rs.getStatement().getConnection().close();
                // Abre MenuFrame e fecha Login
                JFrame f = new MenuFrame(this, userId);
                dispose();
                CustomDialog.showMessage(f, "Login bem-sucedido!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                CustomDialog.showMessage(this, "Credenciais inválidas.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Abre diálogo para criar nova conta com validações.
     */
    private void showCreateAccountDialog() {
        JDialog dialog = new JDialog(this, "Criar Conta", true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new BorderLayout(20, 20));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Criar Nova Conta", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 36));
        header.setForeground(Color.WHITE);
        backgroundPanel.add(header, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridLayout(3, 2, 15, 15));
        fields.setOpaque(false);

        JLabel lblNewUser = new JLabel("Usuário:");
        lblNewUser.setFont(new Font("Arial", Font.PLAIN, 28));
        lblNewUser.setForeground(Color.WHITE);

        JTextField newUser = CustomInput.createTextField();
        newUser.setFont(new Font("Arial", Font.PLAIN, 28));

        JLabel lblNewPass = new JLabel("Senha:");
        lblNewPass.setFont(new Font("Arial", Font.PLAIN, 28));
        lblNewPass.setForeground(Color.WHITE);

        JPasswordField newPass = CustomInput.createTextFieldPass();
        newPass.setFont(new Font("Arial", Font.PLAIN, 28));

        JLabel lblConfirm = new JLabel("Confirmar senha:");
        lblConfirm.setFont(new Font("Arial", Font.PLAIN, 28));
        lblConfirm.setForeground(Color.WHITE);

        JPasswordField confirmPass = CustomInput.createTextFieldPass();
        confirmPass.setFont(new Font("Arial", Font.PLAIN, 28));

        fields.add(lblNewUser); fields.add(newUser);
        fields.add(lblNewPass); fields.add(newPass);
        fields.add(lblConfirm); fields.add(confirmPass);
        backgroundPanel.add(fields, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttons.setOpaque(false);
        
        JButton btnConfirm = CustomInput.createButtom("Confirmar", Color.BLACK);
        JButton btnCancel = CustomInput.createButtom("Cancelar", Color.BLACK);

        btnConfirm.addActionListener(e -> {
            String u = newUser.getText().trim();
            String p = new String(newPass.getPassword());
            String cp = new String(confirmPass.getPassword());

            if (u.isEmpty() || u.length() > 15) {
                CustomDialog.showMessage(dialog,
                    "Nome de usuário deve ter de 1 a 15 caracteres.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (p.length() < 4 || p.length() > 50) {
                CustomDialog.showMessage(dialog,
                    "Senha deve ter entre 4 e 50 caracteres.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!p.equals(cp)) {
                CustomDialog.showMessage(dialog,
                    "Senha e confirmação não conferem.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ResultSet check = DatabaseOperations.executeQuery(
                "SELECT id FROM usuarios WHERE user_name=?", new String[]{u}
            );
            try {
                if (check != null && check.next()) {
                    CustomDialog.showMessage(dialog,
                        "Nome de usuário já existe.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                    check.getStatement().getConnection().close();
                    return;
                }
                if (check != null) check.getStatement().getConnection().close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            DatabaseOperations.executeUpdate(
                "INSERT INTO usuarios (user_name, senha) VALUES (?, ?)",
                new String[]{u, p}
            );
            CustomDialog.showMessage(dialog,
                "Conta criada com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        buttons.add(btnConfirm);
        buttons.add(btnCancel);
        backgroundPanel.add(buttons, BorderLayout.SOUTH);

        dialog.setContentPane(backgroundPanel);
        dialog.setVisible(true);
    }
}

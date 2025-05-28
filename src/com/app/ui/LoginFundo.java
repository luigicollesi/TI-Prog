package com.app.ui;

import com.app.db.DatabaseOperations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Tela inicial de login e criação de conta com botão Sair e fontes maiores.
 */
public class LoginFundo extends JFrame implements ActionListener {
    private JPanel centralPanel;
    private JPanel panelNorth;
    private JPanel buttonPanel;
    private JPanel fieldPanel;

    private JLabel lblTitulo;
    private JLabel lblUser;
    private JTextField txtUser;
    private JLabel lblPass;
    private JPasswordField txtPass;
    private JButton btnLogin;
    private JButton btnCreate;
    private JButton btnExit;
    private JPanel backgroundPanel;

    private GridBagConstraints gbc;

    private Image backgroundImage = new ImageIcon("public/Images/LoginFundo.png").getImage();

    public LoginFundo() {
        super("Blackjack - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);

        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.BOLD, 32));
        UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 28));
        UIManager.put("OptionPane.minimumSize", new Dimension(500, 200));

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
        lblTitulo = new OutlinedLabel("Faça login");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 36));
        lblTitulo.setForeground(Color.WHITE);

        btnExit = RoundedInput.createButtom("Sair");
        btnExit.setPreferredSize(new Dimension(120, 50));
        btnExit.addActionListener(e -> System.exit(0));

        panelNorth = new JPanel(new BorderLayout());
        panelNorth.setOpaque(false);
        panelNorth.add(lblTitulo, BorderLayout.CENTER);
        panelNorth.add(btnExit, BorderLayout.EAST);
        centralPanel.add(panelNorth, BorderLayout.NORTH);

        // Campos
        fieldPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        fieldPanel.setOpaque(false);

        lblUser = new OutlinedLabel("Usuário:");
        lblUser.setFont(new Font("Arial", Font.PLAIN, 30));
        lblUser.setForeground(Color.WHITE);
        txtUser = RoundedInput.createTextField();
        txtUser.setFont(new Font("Arial", Font.PLAIN, 28));
        txtUser.setPreferredSize(new Dimension(0, 10));

        lblPass = new OutlinedLabel("Senha:");
        lblPass.setFont(new Font("Arial", Font.PLAIN, 30));
        lblPass.setForeground(Color.WHITE);
        txtPass = RoundedInput.createTextFieldPass();
        txtPass.setFont(new Font("Arial", Font.PLAIN, 28));
        txtUser.setPreferredSize(new Dimension(0, 10));

        fieldPanel.add(lblUser); fieldPanel.add(txtUser);
        fieldPanel.add(lblPass); fieldPanel.add(txtPass);
        centralPanel.add(fieldPanel, BorderLayout.CENTER);

        // Botões
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        btnLogin = RoundedInput.createButtom("Login");
        btnLogin.setPreferredSize(new Dimension(200, 60));
        btnCreate = RoundedInput.createButtom("Criar Conta");
        btnCreate.setPreferredSize(new Dimension(250, 60));

        btnLogin.addActionListener(this);
        btnCreate.addActionListener(e -> showCreateAccountDialog());

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCreate);
        centralPanel.add(buttonPanel, BorderLayout.SOUTH);


        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 120, 0);   // top, left, bottom, right

        backgroundPanel.add(centralPanel, gbc);
        setContentPane(backgroundPanel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Somente login
        String user = txtUser.getText().trim();
        String pass = new String(txtPass.getPassword());
        ResultSet rs = DatabaseOperations.executeQuery(
            "SELECT id FROM usuarios WHERE user_name=? AND senha=?",
            new String[]{user, pass}
        );
        try {
            if (rs != null && rs.next()) {
                CustomDialog.showMessage(this, "Login bem-sucedido!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                rs.getStatement().getConnection().close();
                // Abre MenuFrame e fecha Login
                new MenuFrame();
                dispose();
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

        Image fundo = new ImageIcon("public/Images/LoginFundo.png").getImage();

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.drawImage(fundo, 0, 0, getWidth(), getHeight(), this);
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
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
        JTextField newUser = RoundedInput.createTextField();
        newUser.setFont(new Font("Arial", Font.PLAIN, 28));

        JLabel lblNewPass = new JLabel("Senha:");
        lblNewPass.setFont(new Font("Arial", Font.PLAIN, 28));
        lblNewPass.setForeground(Color.WHITE);
        JPasswordField newPass = RoundedInput.createTextFieldPass();
        newPass.setFont(new Font("Arial", Font.PLAIN, 28));

        JLabel lblConfirm = new JLabel("Confirmar senha:");
        lblConfirm.setFont(new Font("Arial", Font.PLAIN, 28));
        lblConfirm.setForeground(Color.WHITE);
        JPasswordField confirmPass = RoundedInput.createTextFieldPass();
        confirmPass.setFont(new Font("Arial", Font.PLAIN, 28));

        fields.add(lblNewUser); fields.add(newUser);
        fields.add(lblNewPass); fields.add(newPass);
        fields.add(lblConfirm); fields.add(confirmPass);
        backgroundPanel.add(fields, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttons.setOpaque(false);
        
        JButton btnConfirm = RoundedInput.createButtom("Confirmar");
        JButton btnCancel = RoundedInput.createButtom("Cancelar");
        buttons.add(btnConfirm);
        buttons.add(btnCancel);
        backgroundPanel.add(buttons, BorderLayout.SOUTH);

        dialog.setContentPane(backgroundPanel);

        btnConfirm.addActionListener(ev -> {
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

        btnCancel.addActionListener(ev -> dialog.dispose());

        dialog.setVisible(true);
    }
}

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
public class LoginFrame extends JFrame implements ActionListener {
    private JPanel panelMain;
    private JPanel panelFields;
    private JPanel panelButtons;
    private JPanel panelNorth;
    private JLabel lblTitulo;
    private JLabel lblUser;
    private JTextField txtUser;
    private JLabel lblPass;
    private JPasswordField txtPass;
    private JButton btnLogin;
    private JButton btnCreate;
    private JButton btnExit;

    public LoginFrame() {
        super("Blackjack - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 350);  // janela inicial maior
        setLocationRelativeTo(null);

        // Painel principal
        panelMain = new JPanel(new BorderLayout(10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Cabeçalho: título e botão Sair
        panelNorth = new JPanel(new BorderLayout());
        lblTitulo = new JLabel("Faça login ou crie uma conta", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 40));
        btnExit = new JButton("Sair");
        btnExit.setFont(new Font("Arial", Font.PLAIN, 30));
        btnExit.addActionListener(e -> System.exit(0));
        panelNorth.add(lblTitulo, BorderLayout.CENTER);
        panelNorth.add(btnExit, BorderLayout.EAST);
        panelMain.add(panelNorth, BorderLayout.NORTH);

        // Campos de usuário e senha
        panelFields = new JPanel(new GridLayout(2, 2, 15, 15));
        lblUser = new JLabel("Usuário:");
        lblUser.setFont(new Font("Arial", Font.PLAIN, 30));
        txtUser = new JTextField();
        txtUser.setFont(new Font("Arial", Font.PLAIN, 30));
        lblPass = new JLabel("Senha:");
        lblPass.setFont(new Font("Arial", Font.PLAIN, 30));
        txtPass = new JPasswordField();
        txtPass.setFont(new Font("Arial", Font.PLAIN, 30));
        panelFields.add(lblUser);
        panelFields.add(txtUser);
        panelFields.add(lblPass);
        panelFields.add(txtPass);
        panelMain.add(panelFields, BorderLayout.CENTER);

        // Botões de ação
        panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.PLAIN, 30));
        btnCreate = new JButton("Criar Conta");
        btnCreate.setFont(new Font("Arial", Font.PLAIN, 30));
        panelButtons.add(btnLogin);
        panelButtons.add(btnCreate);
        panelMain.add(panelButtons, BorderLayout.SOUTH);

        add(panelMain);

        // Registra ouvintes
        btnLogin.addActionListener(this);
        btnCreate.addActionListener(e -> showCreateAccountDialog());
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
                JOptionPane.showMessageDialog(this, "Login bem-sucedido!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                rs.getStatement().getConnection().close();
            } else {
                JOptionPane.showMessageDialog(this, "Credenciais inválidas.", "Erro", JOptionPane.ERROR_MESSAGE);
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
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(this);

        JPanel dlgPanel = new JPanel(new BorderLayout(20, 20));
        dlgPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Cabeçalho do diálogo
        JLabel header = new JLabel("Criar Nova Conta", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 36));
        dlgPanel.add(header, BorderLayout.NORTH);

        // Campos de entrada
        JPanel fields = new JPanel(new GridLayout(3, 2, 15, 15));
        JLabel lblNewUser = new JLabel("Usuário:");
        lblNewUser.setFont(new Font("Arial", Font.PLAIN, 30));
        JTextField newUser = new JTextField();
        newUser.setFont(new Font("Arial", Font.PLAIN, 30));
        JLabel lblNewPass = new JLabel("Senha:");
        lblNewPass.setFont(new Font("Arial", Font.PLAIN, 30));
        JPasswordField newPass = new JPasswordField();
        newPass.setFont(new Font("Arial", Font.PLAIN, 30));
        JLabel lblConfirm = new JLabel("Confirmar senha:");
        lblConfirm.setFont(new Font("Arial", Font.PLAIN, 30));
        JPasswordField confirmPass = new JPasswordField();
        confirmPass.setFont(new Font("Arial", Font.PLAIN, 30));
        fields.add(lblNewUser);
        fields.add(newUser);
        fields.add(lblNewPass);
        fields.add(newPass);
        fields.add(lblConfirm);
        fields.add(confirmPass);
        dlgPanel.add(fields, BorderLayout.CENTER);

        // Botões de ação
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnConfirm = new JButton("Confirmar");
        btnConfirm.setFont(new Font("Arial", Font.PLAIN, 30));
        JButton btnCancel = new JButton("Cancelar");
        btnCancel.setFont(new Font("Arial", Font.PLAIN, 30));
        buttons.add(btnConfirm);
        buttons.add(btnCancel);
        dlgPanel.add(buttons, BorderLayout.SOUTH);

        dialog.add(dlgPanel);

        // Ações dos botões
        btnConfirm.addActionListener(ev -> {
            String u = newUser.getText().trim();
            String p = new String(newPass.getPassword());
            String cp = new String(confirmPass.getPassword());

            // Ajusta fonte das caixas de diálogo
            UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 30));
            UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.BOLD, 30));

            // Validações
            if (u.isEmpty() || u.length() > 15) {
                JOptionPane.showMessageDialog(dialog,
                    "Nome de usuário deve ter de 1 a 15 caracteres.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (p.length() < 4 || p.length() > 50) {
                JOptionPane.showMessageDialog(dialog,
                    "Senha deve ter entre 4 e 50 caracteres.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!p.equals(cp)) {
                JOptionPane.showMessageDialog(dialog,
                    "Senha e confirmação não conferem.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Verifica existência de usuário
            ResultSet check = DatabaseOperations.executeQuery(
                "SELECT id FROM usuarios WHERE user_name=?", new String[]{u}
            );
            try {
                if (check != null && check.next()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Nome de usuário já existe.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                    check.getStatement().getConnection().close();
                    return;
                }
                if (check != null) check.getStatement().getConnection().close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            // Insere usuário no banco
            DatabaseOperations.executeUpdate(
                "INSERT INTO usuarios (user_name, senha) VALUES (?, ?)",
                new String[]{u, p}
            );
            JOptionPane.showMessageDialog(dialog,
                "Conta criada com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });


        btnCancel.addActionListener(ev -> dialog.dispose());
        dialog.setVisible(true);
    }
}

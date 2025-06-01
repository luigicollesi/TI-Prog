package app.ui.Auth;

import app.db.DatabaseOperations;
import app.ui.utility.CustomDialog;
import app.ui.utility.PanelImage;
import app.ui.utility.RoundButton;
import app.ui.utility.RoundPassF;
import app.ui.utility.RoundTextF;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateAccountFrame extends JDialog {
    private final Image backgroundImage = new ImageIcon("public/Images/LoginFundo.png").getImage();

    private final JPanel backgroundPanel;
    private final JLabel header;

    private final JPanel fieldsPanel;
    private final JLabel lblNewUser;
    private final JTextField newUser;
    private final JLabel lblNewPass;
    private final JPasswordField newPass;
    private final JLabel lblConfirm;
    private final JPasswordField confirmPass;

    private final JPanel buttonsPanel;
    private final JButton btnConfirm;
    private final JButton btnCancel;

    public CreateAccountFrame(JFrame owner) {
        super(owner, "Criar Conta", true);
        setSize(800, 500);
        setLocationRelativeTo(owner);

        // --- Inicializa backgroundPanel com paintComponent personalizado ---
        backgroundPanel = new PanelImage(backgroundImage, true);
        backgroundPanel.setLayout(new BorderLayout(20, 20));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(backgroundPanel);

        // --- Cabeçalho ---
        header = new JLabel("Criar Nova Conta", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 36));
        header.setForeground(Color.WHITE);
        backgroundPanel.add(header, BorderLayout.NORTH);

        // --- Campos de entrada: usuário, senha e confirmação ---
        fieldsPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        fieldsPanel.setOpaque(false);

        lblNewUser = new JLabel("Usuário:");
        lblNewUser.setFont(new Font("Arial", Font.PLAIN, 28));
        lblNewUser.setForeground(Color.WHITE);
        newUser = new RoundTextF();
        newUser.setFont(new Font("Arial", Font.PLAIN, 28));

        lblNewPass = new JLabel("Senha:");
        lblNewPass.setFont(new Font("Arial", Font.PLAIN, 28));
        lblNewPass.setForeground(Color.WHITE);
        newPass = new RoundPassF();
        newPass.setFont(new Font("Arial", Font.PLAIN, 28));

        lblConfirm = new JLabel("Confirmar senha:");
        lblConfirm.setFont(new Font("Arial", Font.PLAIN, 28));
        lblConfirm.setForeground(Color.WHITE);
        confirmPass = new RoundPassF();
        confirmPass.setFont(new Font("Arial", Font.PLAIN, 28));

        fieldsPanel.add(lblNewUser);
        fieldsPanel.add(newUser);
        fieldsPanel.add(lblNewPass);
        fieldsPanel.add(newPass);
        fieldsPanel.add(lblConfirm);
        fieldsPanel.add(confirmPass);
        backgroundPanel.add(fieldsPanel, BorderLayout.CENTER);

        // --- Painel de botões (Confirmar / Cancelar) ---
        buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setOpaque(false);

        btnConfirm = new RoundButton("Confirmar", Color.BLACK);
        btnCancel = new RoundButton("Cancelar", Color.BLACK);

        // Ação do botão Confirmar
        btnConfirm.addActionListener(e -> {
            String u  = newUser.getText().trim();
            String p  = new String(newPass.getPassword());
            String cp = new String(confirmPass.getPassword());

            // Validações
            if (u.isEmpty() || u.length() > 15) {
                CustomDialog.showMessage(this,
                    "Nome de usuário deve ter de 1 a 15 caracteres.",
                    "Aviso", JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            if (p.length() < 4 || p.length() > 50) {
                CustomDialog.showMessage(this,
                    "Senha deve ter entre 4 e 50 caracteres.",
                    "Aviso", JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            if (!p.equals(cp)) {
                CustomDialog.showMessage(this,
                    "Senha e confirmação não conferem.",
                    "Aviso", JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Verifica duplicação no banco
            ResultSet check = DatabaseOperations.executeQuery(
                "SELECT id FROM usuarios WHERE user_name = ?",
                new String[]{ u }
            );
            try {
                if (check != null && check.next()) {
                    CustomDialog.showMessage(this,
                        "Nome de usuário já existe.",
                        "Erro", JOptionPane.ERROR_MESSAGE
                    );
                    check.getStatement().getConnection().close();
                    return;
                }
                if (check != null) {
                    check.getStatement().getConnection().close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            // Insere novo usuário
            DatabaseOperations.executeUpdate(
                "INSERT INTO usuarios (user_name, senha) VALUES (?, ?)",
                new String[]{ u, p }
            );
            CustomDialog.showMessage(this,
                "Conta criada com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE
            );
            setVisible(false);
        });

        // Ação do botão Cancelar
        btnCancel.addActionListener(e -> setVisible(false));

        buttonsPanel.add(btnConfirm);
        buttonsPanel.add(btnCancel);
        backgroundPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }
}

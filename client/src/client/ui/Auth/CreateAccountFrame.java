package client.ui.Auth;

import client.net.ServerConnection;
import client.net.ServerListener;
import client.ui.utility.CustomDialog;
import client.ui.utility.PanelImage;
import client.ui.utility.RoundButton;
import client.ui.utility.RoundPassF;
import client.ui.utility.RoundTextF;

import javax.swing.*;
import java.awt.*;

public class CreateAccountFrame extends JDialog implements ServerListener {
    private final ServerConnection connection;

    private final RoundTextF newUser;
    private final RoundPassF newPass;
    private final RoundPassF confirmPass;

    private final Image backgroundImage = new javax.swing.ImageIcon("public/Images/LoginFundo.png").getImage();

    public CreateAccountFrame(JFrame owner, ServerConnection connection) {
        super(owner, "Criar Conta", true);
        this.connection = connection;
        connection.addListener(this);

        setSize(800, 500);
        setLocationRelativeTo(owner);

        PanelImage backgroundPanel = new PanelImage(backgroundImage, true);
        backgroundPanel.setLayout(new BorderLayout(20, 20));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(backgroundPanel);

        JLabel header = new JLabel("Criar Nova Conta", javax.swing.SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 36));
        header.setForeground(Color.WHITE);
        backgroundPanel.add(header, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        fieldsPanel.setOpaque(false);

        JLabel lblNewUser = new JLabel("Usuário:");
        lblNewUser.setFont(new Font("Arial", Font.PLAIN, 28));
        lblNewUser.setForeground(Color.WHITE);

        newUser = new RoundTextF();
        newUser.setFont(new Font("Arial", Font.PLAIN, 28));

        JLabel lblNewPass = new JLabel("Senha:");
        lblNewPass.setFont(new Font("Arial", Font.PLAIN, 28));
        lblNewPass.setForeground(Color.WHITE);

        newPass = new RoundPassF();
        newPass.setFont(new Font("Arial", Font.PLAIN, 28));

        JLabel lblConfirm = new JLabel("Confirmar senha:");
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

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setOpaque(false);

        JButton btnConfirm = new RoundButton("Confirmar", Color.BLACK);
        JButton btnCancel = new RoundButton("Cancelar", Color.BLACK);

        btnConfirm.addActionListener(e -> attemptRegister());
        btnCancel.addActionListener(e -> setVisible(false));

        buttonsPanel.add(btnConfirm);
        buttonsPanel.add(btnCancel);

        backgroundPanel.add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void attemptRegister() {
        String username = newUser.getText().trim();
        String password = new String(newPass.getPassword());
        String confirm = new String(confirmPass.getPassword());

        if (username.isEmpty() || username.length() > 15) {
            CustomDialog.showMessage(this, "Nome de usuário deve ter de 1 a 15 caracteres.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (password.length() < 4 || password.length() > 50) {
            CustomDialog.showMessage(this, "Senha deve ter entre 4 e 50 caracteres.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirm)) {
            CustomDialog.showMessage(this, "Senha e confirmação não conferem.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        connection.register(username, password);
    }

    @Override
    public void onRegisterResult(boolean success, String message) {
        if (!isDisplayable()) {
            return;
        }
        CustomDialog.showMessage(this, message, success ? "Sucesso" : "Erro", success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        if (success) {
            newUser.setText("");
            newPass.setText("");
            confirmPass.setText("");
            setVisible(false);
        }
    }

    @Override
    public void dispose() {
        connection.removeListener(this);
        super.dispose();
    }
}

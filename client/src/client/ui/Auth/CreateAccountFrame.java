package client.ui.Auth;

import client.net.ServerConnection;
import client.net.ServerListener;
import client.i18n.I18n;
import client.ui.utility.CustomDialog;
import client.ui.utility.PanelImage;
import client.ui.utility.RoundButton;
import client.ui.utility.RoundPassF;
import client.ui.utility.RoundTextF;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;

public class CreateAccountFrame extends JDialog implements ServerListener {
    private static final int MAX_PASSWORD_BYTES = 256;

    private final ServerConnection connection;

    private final RoundTextF newUser;
    private final RoundPassF newPass;
    private final RoundPassF confirmPass;

    private final Image backgroundImage = new javax.swing.ImageIcon("public/Images/LoginFundo.png").getImage();

    public CreateAccountFrame(JFrame owner, ServerConnection connection) {
        super(owner, I18n.get("create.title"), true);
        this.connection = connection;
        connection.addListener(this);

        setSize(800, 500);
        setLocationRelativeTo(owner);

        PanelImage backgroundPanel = new PanelImage(backgroundImage, true);
        backgroundPanel.setLayout(new BorderLayout(20, 20));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(backgroundPanel);

        JLabel header = new JLabel(I18n.get("create.header"), javax.swing.SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 36));
        header.setForeground(Color.WHITE);
        backgroundPanel.add(header, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        fieldsPanel.setOpaque(false);

        JLabel lblNewUser = new JLabel(I18n.get("create.user"));
        lblNewUser.setFont(new Font("Arial", Font.PLAIN, 28));
        lblNewUser.setForeground(Color.WHITE);

        newUser = new RoundTextF();
        newUser.setFont(new Font("Arial", Font.PLAIN, 28));

        JLabel lblNewPass = new JLabel(I18n.get("create.pass"));
        lblNewPass.setFont(new Font("Arial", Font.PLAIN, 28));
        lblNewPass.setForeground(Color.WHITE);

        newPass = new RoundPassF();
        newPass.setFont(new Font("Arial", Font.PLAIN, 28));

        JLabel lblConfirm = new JLabel(I18n.get("create.confirm"));
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

        JButton btnConfirm = new RoundButton(I18n.get("create.btn.ok"), Color.BLACK);
        JButton btnCancel = new RoundButton(I18n.get("create.btn.cancel"), Color.BLACK);

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
            CustomDialog.showMessage(this, I18n.get("create.warn.user"), I18n.get("dialog.warning"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        int passwordBytes = password.getBytes(StandardCharsets.UTF_8).length;
        if (password.length() < 4 || passwordBytes > MAX_PASSWORD_BYTES) {
            CustomDialog.showMessage(this, I18n.get("create.warn.pass"), I18n.get("dialog.warning"), JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirm)) {
            CustomDialog.showMessage(this, I18n.get("create.warn.match"), I18n.get("dialog.warning"), JOptionPane.WARNING_MESSAGE);
            return;
        }

        connection.register(username, password);
    }

    @Override
    public void onRegisterResult(boolean success, String message) {
        if (!isDisplayable()) {
            return;
        }
        CustomDialog.showMessage(this, message, success ? I18n.get("dialog.success") : I18n.get("dialog.error"), success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
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

    public void applyTranslations() {
        setTitle(I18n.get("create.title"));
        Container content = getContentPane();
        if (content.getComponentCount() < 3) return;
        Component headerComp = content.getComponent(0);
        if (headerComp instanceof JLabel lbl) {
            lbl.setText(I18n.get("create.header"));
        }
        JPanel fieldsPanel = (JPanel) content.getComponent(1);
        ((JLabel) fieldsPanel.getComponent(0)).setText(I18n.get("create.user"));
        ((JLabel) fieldsPanel.getComponent(2)).setText(I18n.get("create.pass"));
        ((JLabel) fieldsPanel.getComponent(4)).setText(I18n.get("create.confirm"));
        JPanel buttons = (JPanel) content.getComponent(2);
        ((JButton) buttons.getComponent(0)).setText(I18n.get("create.btn.ok"));
        ((JButton) buttons.getComponent(1)).setText(I18n.get("create.btn.cancel"));
    }
}

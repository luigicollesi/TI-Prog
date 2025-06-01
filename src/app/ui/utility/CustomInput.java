package app.ui.utility;

import javax.swing.*;
import java.awt.*;

public class CustomInput {

    public static JButton createButtom(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                // --- Fundo arredondado ---
                // Cor muda com hover
                boolean hover = getModel().isRollover();
                Color backgroundColor = hover
                    ? new Color(255, 255, 255, 250) // Hover
                    : new Color(255, 255, 255, 200); // Normal

                g2.setColor(backgroundColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);

                // --- Texto centralizado ---
                g2.setColor(color);
                FontMetrics fm = g2.getFontMetrics(getFont());
                int x = ( getWidth() - fm.stringWidth(getText()) ) / 2;
                int y = ( getHeight() - fm.getHeight() ) / 2 + fm.getAscent();
                g2.drawString(getText(), x, y);

                g2.dispose();
            }

            @Override public boolean isContentAreaFilled() { return false; }
            @Override public boolean isBorderPainted() { return false; }
        };

        button.setFont(new Font("Arial", Font.BOLD, 28));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JTextField createTextField() {
        JTextField input = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setColor(new Color(255, 255, 255, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                super.paintComponent(g);
                g2.dispose();
            }
        };
        input.setOpaque(false);
        input.setFont(new Font("Arial", Font.PLAIN, 26));
        input.setForeground(Color.BLACK);
        input.setCaretColor(Color.BLACK);
        input.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return input;
    }

    public static JPasswordField createTextFieldPass() {
        JPasswordField input = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setColor(new Color(255, 255, 255, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                super.paintComponent(g);
                g2.dispose();
            }
        };
        input.setOpaque(false);
        input.setFont(new Font("Arial", Font.PLAIN, 26));
        input.setForeground(Color.BLACK);
        input.setCaretColor(Color.BLACK);
        input.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return input;
    }

    public static JLabel createOutlinedLabel(String text, Font font, Color textColor) {
        JLabel label = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setFont(getFont());
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                FontMetrics fm = g2.getFontMetrics();
                int x = 0;
                int y = fm.getAscent();

                // contorno preto
                g2.setColor(Color.BLACK);
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx != 0 || dy != 0) {
                            g2.drawString(text, x + dx, y + dy);
                        }
                    }
                }

                // texto branco (ou cor definida) no centro
                g2.setColor(getForeground());
                g2.drawString(text, x, y);
                g2.dispose();
            }
        };
        label.setFont(font);
        label.setForeground(textColor);
        label.setOpaque(false);

        return label;
    }

    private CustomInput() {
        throw new UnsupportedOperationException("Utility class");
    }
}

package com.app.ui;

import javax.swing.*;
import java.awt.*;

public class OutlinedLabel extends JLabel {
    public OutlinedLabel(String text) {
        super(text);
        setFont(new Font("Arial", Font.BOLD, 30));
        setForeground(Color.WHITE);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setFont(getFont());
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String text = getText();
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

        // texto branco no centro
        g2.setColor(getForeground());
        g2.drawString(text, x, y);
        g2.dispose();
    }
}

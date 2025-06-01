package app.ui.utility;

import java.awt.Image;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class PanelImage extends javax.swing.JPanel {
    private final java.awt.Image image;
    private final boolean isDark;

    public PanelImage(Image image, boolean isDark) {
        this.image = image;
        this.isDark = isDark;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            if (isDark) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                g2d.setColor(new java.awt.Color(0, 0, 0, 150));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            } else {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
    
}

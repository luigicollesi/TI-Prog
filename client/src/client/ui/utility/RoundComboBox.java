package client.ui.utility;

import javax.swing.*;
import java.awt.*;

public class RoundComboBox<E> extends JComboBox<E> {
    private final Color normalBackground;
    private final Color hoverBackground;
    private boolean hovering = false;

    public RoundComboBox(E[] items) {
        this(items, new Color(255, 255, 255, 200), new Color(255, 255, 255, 240));
    }

    public RoundComboBox(E[] items, Color normalBackground, Color hoverBackground) {
        super(items);
        this.normalBackground = normalBackground;
        this.hoverBackground = hoverBackground;
        setOpaque(false);
        setFocusable(false);
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        setFont(new Font("Arial", Font.BOLD, 24));
        setForeground(Color.BLACK);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setFont(getFont());
                c.setForeground(Color.BLACK);
                if (c instanceof JComponent jc) {
                    jc.setOpaque(true);
                    jc.setBackground(isSelected ? hoverBackground : normalBackground);
                }
                return c;
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { hovering = true; repaint(); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { hovering = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color bg = hovering ? hoverBackground : normalBackground;
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        g2.dispose();
        super.paintComponent(g);
    }
}

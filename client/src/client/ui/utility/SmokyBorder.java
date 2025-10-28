package client.ui.utility;

import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Borda com brilho difuso que cria um efeito "esfumaçado" ao redor do componente.
 */
public class SmokyBorder extends AbstractBorder {
    private final Color color;
    private final int spread;
    private final int arc;

    public SmokyBorder(Color color, int spread, int arc) {
        this.color = color;
        this.spread = spread;
        this.arc = arc;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        RoundRectangle2D inner = new RoundRectangle2D.Float(x + 1, y + 1, width - 2, height - 2, arc, arc);
        RoundRectangle2D outer = new RoundRectangle2D.Float(x - spread, y - spread, width + spread * 2, height + spread * 2, arc + spread * 2, arc + spread * 2);

        Area halo = new Area(outer);
        halo.subtract(new Area(inner));

        Color base = new Color(color.getRed(), color.getGreen(), color.getBlue(), 160);
        Color transparent = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0);

        RadialGradientPaint paint = new RadialGradientPaint(
            new Point2D.Float(x + width / 2f, y + height / 2f),
            Math.max(width, height),
            new float[]{0.55f, 1f},
            new Color[]{base, transparent}
        );
        g2.setPaint(paint);
        g2.fill(halo);

        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 200));
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(inner);

        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(spread + 6, spread + 6, spread + 6, spread + 6);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        Insets computed = getBorderInsets(c);
        insets.top = computed.top;
        insets.left = computed.left;
        insets.bottom = computed.bottom;
        insets.right = computed.right;
        return insets;
    }
}

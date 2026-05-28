package DB2026Team02.view.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class GreenButton extends JButton {

    private final Color bg;
    private final Color hover;
    protected boolean hovered;

    public GreenButton(String text) {
        this(text, UIConstants.PRIMARY, UIConstants.PRIMARY_HOVER);
    }

    public GreenButton(String text, Color bg, Color hover) {
        super(text);
        this.bg    = bg;
        this.hover = hover;
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(UIConstants.f(Font.BOLD, 14));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(hovered ? hover : bg);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
        super.paintComponent(g);
        g2.dispose();
    }

    public static GreenButton outline(String text) {
        GreenButton b = new GreenButton(text, Color.WHITE, UIConstants.SURFACE) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? UIConstants.SURFACE : UIConstants.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(UIConstants.PRIMARY);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth()-2, getHeight()-2, 8, 8));
                super.paintComponent(g);
                g2.dispose();
            }
        };
        b.setForeground(UIConstants.PRIMARY);
        return b;
    }
}

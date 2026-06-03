package DB2026Team02.view.common;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class StatusBadge extends JLabel {

    private Color bg;
    private Color fg;

    public StatusBadge(String status) {
        super(status);
        applyStyle(status);
        setFont(UIConstants.f(Font.BOLD, 11));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void applyStyle(String status) {
        switch (status == null ? "" : status.toUpperCase()) {
            case "CONFIRMED":
                bg = UIConstants.CONFIRMED_BG; fg = UIConstants.CONFIRMED_FG; break;
            case "PENDING":
                bg = UIConstants.PENDING_BG;   fg = UIConstants.PENDING_FG;   break;
            case "COMPLETED":
                bg = UIConstants.COMPLETED_BG; fg = UIConstants.COMPLETED_FG; break;
            case "CANCELLED":
                bg = UIConstants.CANCELLED_BG; fg = UIConstants.CANCELLED_FG; break;
            case "NO_SHOW":
                bg = UIConstants.DANGER_BG;    fg = UIConstants.DANGER;       break;
            case "예약 가능":
                bg = UIConstants.COMPLETED_BG; fg = UIConstants.COMPLETED_FG; break;
            case "마감":
                bg = UIConstants.CANCELLED_BG; fg = UIConstants.CANCELLED_FG; break;
            default:
                bg = UIConstants.SURFACE;      fg = UIConstants.TEXT_MUTED;   break;
        }
        setForeground(fg);
    }

    public void setStatus(String status) {
        setText(status);
        applyStyle(status);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
        super.paintComponent(g);
        g2.dispose();
    }
}

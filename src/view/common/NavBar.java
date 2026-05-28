package DB2026Team02.view.common;

import DB2026Team02.view.MainFrame;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;


public class NavBar extends JPanel {

    public static final String LANDING = "LANDING";
    public static final String STUDENT = "STUDENT";

    public NavBar(String type) {
        setLayout(new BorderLayout());
        setBackground(UIConstants.WHITE);
        setPreferredSize(new Dimension(UIConstants.FRAME_W, UIConstants.NAVBAR_H));
        setBorder(new MatteBorder(0, 0, 1, 0, UIConstants.BORDER));

        add(buildLeft(), BorderLayout.WEST);
        add(buildRight(type), BorderLayout.EAST);
    }

    private JPanel buildLeft() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 0));

        final int CIRCLE_SIZE = 30;
        final int CIRCLE_Y = (UIConstants.NAVBAR_H - CIRCLE_SIZE) / 2;

        JLabel logo = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(UIConstants.PRIMARY);
                g2.fill(new Ellipse2D.Float(0, CIRCLE_Y, CIRCLE_SIZE, CIRCLE_SIZE));

                g2.setColor(Color.WHITE);
                g2.setFont(UIConstants.f(Font.BOLD, 13));

                FontMetrics fm = g2.getFontMetrics();
                String t = "E";
                int tx = (CIRCLE_SIZE - fm.stringWidth(t)) / 2;
                int ty = CIRCLE_Y + (CIRCLE_SIZE + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(t, tx, ty);

                super.paintComponent(g);
                g2.dispose();
            }
        };

        logo.setPreferredSize(new Dimension(CIRCLE_SIZE, UIConstants.NAVBAR_H));

        JLabel brand = new JLabel("이화여자대학교");
        brand.setFont(UIConstants.f(Font.BOLD, 15));
        brand.setForeground(UIConstants.TEXT_PRIMARY);
        brand.setVerticalAlignment(SwingConstants.CENTER);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        p.add(logo);
        p.add(brand);

        return p;
    }

    private JPanel buildRight(String type) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));

        if (LANDING.equals(type)) {
        } else { // STUDENT
            p.add(navLink("홈", MainFrame.STUDENT_HOME));
            p.add(Box.createHorizontalStrut(64));

            p.add(navLink("학과 검색", MainFrame.DEPT_SEARCH));
            p.add(Box.createHorizontalStrut(64));

            p.add(navLink("내 예약", MainFrame.MY_RESERVATIONS));
            p.add(Box.createHorizontalStrut(72));

            p.add(buildLogoutButton());
        }

        return p;
    }

    private JLabel navLink(String text, String target) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIConstants.f(Font.PLAIN, 14));
        lbl.setForeground(UIConstants.TEXT_SECONDARY);
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbl.setAlignmentY(Component.CENTER_ALIGNMENT);

        if (target != null) {
            lbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    MainFrame.navigate(target);
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    lbl.setForeground(UIConstants.PRIMARY);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    lbl.setForeground(UIConstants.TEXT_SECONDARY);
                }
            });
        }

        return lbl;
    }

    private JLabel buildLogoutButton() {
        JLabel logout = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = 64;
                int h = 32;
                int x = 0;
                int y = (getHeight() - h) / 2;

                g2.setColor(UIConstants.PRIMARY);
                g2.fill(new RoundRectangle2D.Float(x, y, w, h, 16, 16));

                g2.setColor(Color.WHITE);
                g2.setFont(UIConstants.f(Font.BOLD, 12));

                String text = "로그아웃";
                FontMetrics fm = g2.getFontMetrics();
                int tx = x + (w - fm.stringWidth(text)) / 2;
                int ty = y + (h + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(text, tx, ty);

                g2.dispose();
            }
        };

        logout.setPreferredSize(new Dimension(64, UIConstants.NAVBAR_H));
        logout.setMaximumSize(new Dimension(64, UIConstants.NAVBAR_H));
        logout.setMinimumSize(new Dimension(64, UIConstants.NAVBAR_H));
        logout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logout.setAlignmentY(Component.CENTER_ALIGNMENT);

        logout.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                MainFrame.navigate(MainFrame.LANDING);
            }
        });

        return logout;
    }
}
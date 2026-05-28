package DB2026Team02.view.admin;

import DB2026Team02.view.MainFrame;
import DB2026Team02.view.common.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class AdminSideBar extends JPanel {

    private final String active;

    public AdminSideBar(String active) {
        this.active = MainFrame.ADMIN_TIMESLOT.equals(active) ? MainFrame.ADMIN_BOOTH : active;
        setLayout(new BorderLayout());
        setBackground(UIConstants.SIDEBAR_BG);
        setPreferredSize(new Dimension(UIConstants.SIDEBAR_W, 0));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildNav(),    BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 20));
        p.setBackground(UIConstants.SIDEBAR_BG);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        JLabel avatar = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.PRIMARY);
                g2.fill(new Ellipse2D.Float(0, 0, 36, 36));
                g2.setColor(Color.WHITE);
                g2.setFont(UIConstants.f(Font.BOLD, 15));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("A", (36 - fm.stringWidth("A")) / 2,
                        (36 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setOpaque(false);

        JLabel title = new JLabel("관리자 패널");
        title.setFont(UIConstants.f(Font.BOLD, 14));
        title.setForeground(Color.WHITE);

        p.add(avatar);
        p.add(title);
        return p;
    }

    private JPanel buildNav() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UIConstants.SIDEBAR_BG);
        p.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        p.add(navItem("대시보드",     MainFrame.ADMIN_DASHBOARD));
        p.add(navItem("예약 목록",    MainFrame.ADMIN_RESERVATIONS));
        p.add(navItem("체크인 처리",  MainFrame.ADMIN_CHECKIN));
        p.add(navItem("학과 관리",    MainFrame.ADMIN_DEPT));
        p.add(navItem("교수 관리",    MainFrame.ADMIN_PROF));
        p.add(navItem("부스/시간대",  MainFrame.ADMIN_BOOTH));
        p.add(Box.createVerticalGlue());

        return p;
    }

    private JPanel navItem(String text, String target) {
        boolean isActive = target.equals(active);

        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        item.setBackground(isActive ? UIConstants.SIDEBAR_ACTIVE : UIConstants.SIDEBAR_BG);
        item.setMaximumSize(new Dimension(UIConstants.SIDEBAR_W, 44));
        item.setCursor(isActive ? Cursor.getDefaultCursor()
                                : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(text);
        lbl.setFont(UIConstants.f(isActive ? Font.BOLD : Font.PLAIN, 14));
        lbl.setForeground(isActive ? Color.WHITE : UIConstants.SIDEBAR_TEXT);
        item.add(lbl);

        if (!isActive) {
            item.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    item.setBackground(new Color(45, 55, 72));
                }
                @Override public void mouseExited(MouseEvent e) {
                    item.setBackground(UIConstants.SIDEBAR_BG);
                }
                @Override public void mousePressed(MouseEvent e) {
                    MainFrame.navigate(target);
                }
            });
        }

        return item;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        p.setBackground(UIConstants.SIDEBAR_BG);

        JLabel homeBtn = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.SIDEBAR_ACTIVE);
                g2.fill(new Ellipse2D.Float(0, 0, 36, 36));
                g2.setColor(UIConstants.SIDEBAR_TEXT);
                g2.setFont(UIConstants.f(Font.PLAIN, 13));
                FontMetrics fm = g2.getFontMetrics();
                String t = "홈";
                g2.drawString(t, (36 - fm.stringWidth(t)) / 2,
                        (36 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        homeBtn.setPreferredSize(new Dimension(36, 36));
        homeBtn.setOpaque(false);
        homeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        homeBtn.setToolTipText("홈으로");
        homeBtn.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                MainFrame.navigate(MainFrame.LANDING);
            }
        });

        p.add(homeBtn);
        return p;
    }
}

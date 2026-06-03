package DB2026Team02.view.student;

import DB2026Team02.view.MainFrame;
import DB2026Team02.view.common.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class LandingPanel extends JPanel {

    public LandingPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG);

        add(new NavBar(NavBar.LANDING), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(UIConstants.BG);

        body.add(buildHero());
        body.add(buildRoleSection());
        return body;
    }

    private JPanel buildHero() {
        JPanel hero = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, UIConstants.PRIMARY_DARK,
                                                      getWidth(), getHeight(), UIConstants.PRIMARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(255, 255, 255, 15));
                g2.fill(new Ellipse2D.Float(getWidth() - 180, -60, 260, 260));
                g2.fill(new Ellipse2D.Float(-60, getHeight() - 120, 200, 200));
                g2.dispose();
            }
        };
        hero.setPreferredSize(new Dimension(1280, 280));

        JLabel title = new JLabel("대학원 박람회");
        title.setFont(UIConstants.f(Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setBounds(80, 60, 700, 52);
        hero.add(title);

        JLabel title2 = new JLabel("상담 예약 시스템");
        title2.setFont(UIConstants.f(Font.BOLD, 36));
        title2.setForeground(Color.WHITE);
        title2.setBounds(80, 114, 700, 52);
        hero.add(title2);

        JLabel sub = new JLabel("간편하게 상담을 예약하세요.");
        sub.setFont(UIConstants.f(Font.PLAIN, 16));
        sub.setForeground(new Color(255, 255, 255, 200));
        sub.setBounds(80, 190, 600, 28);
        hero.add(sub);

        return hero;
    }

    private JPanel buildRoleSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(UIConstants.BG);
        section.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));

        JLabel title = new JLabel("역할을 선택하세요", SwingConstants.CENTER);
        title.setFont(UIConstants.f(Font.BOLD, 22));
        title.setForeground(UIConstants.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 32, 0));
        section.add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 2, 40, 0));
        cards.setOpaque(false);
        cards.add(buildRoleCard("학생으로 시작 →", MainFrame.STUDENT_REGISTER));
        cards.add(buildRoleCard("관리자로 시작 →", MainFrame.ADMIN_DASHBOARD));
        section.add(cards, BorderLayout.CENTER);

        return section;
    }

    private JPanel buildRoleCard(String btnText, String target) {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(UIConstants.PRIMARY);
                g2.fill(new RoundRectangle2D.Float(0, 0, 5, getHeight(), 4, 4));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(32, 24, 32, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;

        GreenButton btn = new GreenButton(btnText);
        btn.setPreferredSize(new Dimension(200, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(btn, gbc);

        btn.addActionListener(e -> {
            if (MainFrame.ADMIN_DASHBOARD.equals(target)) {
                if (!checkAdminPassword()) return;
            }
            MainFrame.navigate(target);
        });

        return card;
    }

    // 본 프로젝트는 DB 과목 실습 범위상 관리자 로그인 테이블/인증 로직을 별도 구현하지 않으므로,
    // 비밀번호를 코드에 직접 고정(hardcode)하여 간이 인증만 수행합니다.
    private boolean checkAdminPassword() {
        JPasswordField pwField = new JPasswordField(16);
        pwField.setFont(UIConstants.f(Font.PLAIN, 14));

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.add(new JLabel("관리자 비밀번호를 입력하세요:"), BorderLayout.NORTH);
        panel.add(pwField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(MainFrame.getInstance(), panel, "관리자 로그인",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return false;

        String entered = new String(pwField.getPassword());
        if ("ewha1886".equals(entered)) return true;

        JOptionPane.showMessageDialog(MainFrame.getInstance(), "비밀번호가 올바르지 않습니다.",
                "인증 실패", JOptionPane.ERROR_MESSAGE
        );
        return false;
    }
}

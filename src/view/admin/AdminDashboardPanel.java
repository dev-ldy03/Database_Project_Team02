package DB2026Team02.view.admin;

import DB2026Team02.model.ReservationDetail;
import DB2026Team02.service.AdminService;
import DB2026Team02.view.MainFrame;
import DB2026Team02.view.common.StatusBadge;
import DB2026Team02.view.common.UIConstants;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminDashboardPanel extends JPanel {

    private final AdminService adminService = new AdminService();

    private JLabel totalLbl, confirmedLbl, pendingLbl, cancelledLbl, completedLbl;
    private JPanel chartArea;
    private JPanel recentList;
    private JPanel statusCard;

    public AdminDashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG);

        add(new AdminSideBar(MainFrame.ADMIN_DASHBOARD), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(UIConstants.BG);

        c.add(buildTitleBar(), BorderLayout.NORTH);
        c.add(buildBody(),     BorderLayout.CENTER);

        return c;
    }

    private JPanel buildTitleBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIConstants.WHITE);
        p.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UIConstants.BORDER),
                BorderFactory.createEmptyBorder(20, 32, 20, 32)
        ));

        JLabel title = new JLabel("대시보드");
        title.setFont(UIConstants.f(Font.BOLD, 22));
        title.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel dateLabel = new JLabel(
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 M월 d일")));
        dateLabel.setFont(UIConstants.f(Font.PLAIN, 13));
        dateLabel.setForeground(UIConstants.TEXT_MUTED);

        p.add(title,     BorderLayout.WEST);
        p.add(dateLabel, BorderLayout.EAST);
        return p;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 0));
        body.setBackground(UIConstants.BG);
        body.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        body.add(buildStatsRow(), BorderLayout.NORTH);

        JPanel mid = new JPanel(new GridLayout(1, 2, 16, 0));
        mid.setOpaque(false);
        mid.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        chartArea = buildChartCard();
        recentList = buildRecentCard();

        mid.add(chartArea);
        mid.add(recentList);
        body.add(mid, BorderLayout.CENTER);

        statusCard = new JPanel(new BorderLayout());
        statusCard.setBackground(UIConstants.WHITE);
        statusCard.setBorder(new CompoundBorder(
                new LineBorder(UIConstants.BORDER),
                BorderFactory.createEmptyBorder(16, 20, 12, 20)
        ));
        JPanel southWrap = new JPanel(new BorderLayout());
        southWrap.setOpaque(false);
        southWrap.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        southWrap.add(statusCard);
        body.add(southWrap, BorderLayout.SOUTH);

        return body;
    }

    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 5, 12, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 110));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        Color[] borderColors = {
            new Color(100, 116, 139),
            UIConstants.CONFIRMED_FG,
            UIConstants.PENDING_FG,
            UIConstants.DANGER,
            new Color(124, 58, 237)
        };
        String[] subLabels  = {"전체 예약", "CONFIRMED", "PENDING", "취소/노쇼", "COMPLETED"};

        totalLbl     = new JLabel("—", SwingConstants.LEFT);
        confirmedLbl = new JLabel("—", SwingConstants.LEFT);
        pendingLbl   = new JLabel("—", SwingConstants.LEFT);
        cancelledLbl = new JLabel("—", SwingConstants.LEFT);
        completedLbl = new JLabel("—", SwingConstants.LEFT);

        JLabel[] nums = {totalLbl, confirmedLbl, pendingLbl, cancelledLbl, completedLbl};

        for (int i = 0; i < 5; i++) {
            row.add(buildStatCard(nums[i], subLabels[i], borderColors[i]));
        }
        return row;
    }

    private JPanel buildStatCard(JLabel numLbl, String sub, Color accent) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(accent);
                g.fillRect(0, 0, getWidth(), 4);
            }
        };
        card.setBackground(UIConstants.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new CompoundBorder(
                new LineBorder(UIConstants.BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        numLbl.setFont(UIConstants.f(Font.BOLD, 30));
        numLbl.setForeground(UIConstants.TEXT_PRIMARY);
        numLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLbl = new JLabel(sub);
        subLbl.setFont(UIConstants.f(Font.PLAIN, 12));
        subLbl.setForeground(UIConstants.TEXT_MUTED);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(numLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(subLbl);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private JPanel buildChartCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIConstants.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(UIConstants.BORDER),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("학과별 예약 현황");
        title.setFont(UIConstants.f(Font.BOLD, 15));
        title.setForeground(UIConstants.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        card.add(title, BorderLayout.NORTH);
        return card;
    }

    private void refreshChart(List<String[]> deptStats) {
        chartArea.removeAll();

        JLabel title = new JLabel("학과별 예약 현황");
        title.setFont(UIConstants.f(Font.BOLD, 15));
        title.setForeground(UIConstants.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        chartArea.add(title, BorderLayout.NORTH);

        JPanel rows = new JPanel();
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        rows.setOpaque(false);

        int max = deptStats.stream()
                .mapToInt(r -> { try { return Integer.parseInt(r[1]); } catch (Exception e) { return 1; } })
                .max().orElse(1);

        Color[] barColors = {
            new Color(59, 130, 246),
            new Color(16, 185, 129),
            new Color(139, 92, 246),
            new Color(245, 158, 11),
            new Color(99, 102, 241),
        };

        for (int i = 0; i < Math.min(deptStats.size(), 6); i++) {
            String[] row = deptStats.get(i);
            String deptName = row[0];
            int total = 0;
            try { total = Integer.parseInt(row[1]); } catch (Exception ignored) {}

            Color barColor = barColors[i % barColors.length];
            int finalTotal = total;
            int finalMax = max;

            JPanel barRow = new JPanel(new BorderLayout(8, 0));
            barRow.setOpaque(false);
            barRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

            JLabel nameLbl = new JLabel(deptName);
            nameLbl.setFont(UIConstants.f(Font.PLAIN, 13));
            nameLbl.setForeground(UIConstants.TEXT_SECONDARY);
            nameLbl.setPreferredSize(new Dimension(110, 22));

            JPanel barWrap = new JPanel(new BorderLayout());
            barWrap.setOpaque(false);

            JPanel bar = new JPanel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(229, 231, 235));
                    g2.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 6, 6));
                    int bw = (int)(getWidth() * (finalMax > 0 ? (double)finalTotal / finalMax : 0));
                    g2.setColor(barColor);
                    g2.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, bw, getHeight(), 6, 6));
                    g2.dispose();
                }
            };
            bar.setPreferredSize(new Dimension(0, 14));
            bar.setOpaque(false);
            barWrap.add(bar, BorderLayout.CENTER);

            JLabel countLbl = new JLabel(String.valueOf(total));
            countLbl.setFont(UIConstants.f(Font.BOLD, 13));
            countLbl.setForeground(UIConstants.TEXT_PRIMARY);
            countLbl.setPreferredSize(new Dimension(36, 22));
            countLbl.setHorizontalAlignment(SwingConstants.RIGHT);

            barRow.add(nameLbl, BorderLayout.WEST);
            barRow.add(barWrap, BorderLayout.CENTER);
            barRow.add(countLbl, BorderLayout.EAST);

            rows.add(barRow);
            rows.add(Box.createVerticalStrut(12));
        }

        chartArea.add(rows, BorderLayout.CENTER);
        chartArea.revalidate();
        chartArea.repaint();
    }

    private JPanel buildRecentCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIConstants.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(UIConstants.BORDER),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        return card;
    }

    private void refreshRecent(List<ReservationDetail> all) {
        recentList.removeAll();

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel title = new JLabel("최근 예약");
        title.setFont(UIConstants.f(Font.BOLD, 15));
        title.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel viewAll = new JLabel("전체 보기 →");
        viewAll.setFont(UIConstants.f(Font.PLAIN, 13));
        viewAll.setForeground(UIConstants.PRIMARY);
        viewAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        viewAll.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent e) {
                MainFrame.navigate(MainFrame.ADMIN_RESERVATIONS);
            }
        });

        header.add(title, BorderLayout.WEST);
        header.add(viewAll, BorderLayout.EAST);
        recentList.add(header, BorderLayout.NORTH);

        JPanel rows = new JPanel();
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        rows.setOpaque(false);

        List<ReservationDetail> recent = all.stream()
                .sorted((a, b) -> Integer.compare(b.getReservationId(), a.getReservationId()))
                .limit(5)
                .collect(java.util.stream.Collectors.toList());

        for (ReservationDetail r : recent) {
            JPanel row = new JPanel(new BorderLayout(12, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));
            row.setPreferredSize(new Dimension(0, 58));
            row.setBorder(new MatteBorder(0, 0, 1, 0, UIConstants.BORDER));

            String name = r.getStudentName() != null ? r.getStudentName() : "?";

            JLabel av = new JLabel() {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setColor(UIConstants.SURFACE);
                    g2.fill(new java.awt.geom.Ellipse2D.Float(0, 0, 32, 32));

                    g2.setColor(UIConstants.TEXT_SECONDARY);
                    g2.setFont(UIConstants.f(Font.BOLD, 13));

                    FontMetrics fm = g2.getFontMetrics();
                    String ch = name.isEmpty() ? "?" : String.valueOf(name.charAt(0));

                    g2.drawString(
                            ch,
                            (32 - fm.stringWidth(ch)) / 2,
                            (32 + fm.getAscent() - fm.getDescent()) / 2
                    );

                    g2.dispose();
                }
            };
            av.setPreferredSize(new Dimension(32, 32));
            av.setMinimumSize(new Dimension(32, 32));
            av.setMaximumSize(new Dimension(32, 32));
            av.setOpaque(false);

            JPanel avWrap = new JPanel(new GridBagLayout());
            avWrap.setOpaque(false);
            avWrap.setPreferredSize(new Dimension(44, 58));
            avWrap.setMinimumSize(new Dimension(44, 58));
            avWrap.add(av);

            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setOpaque(false);
            info.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel nameLbl = new JLabel(name);
            nameLbl.setFont(UIConstants.f(Font.BOLD, 13));
            nameLbl.setForeground(UIConstants.TEXT_PRIMARY);
            nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel deptLbl = new JLabel(
                    "부스: " + (r.getBoothName() != null ? r.getBoothName() : "")
            );
            deptLbl.setFont(UIConstants.f(Font.PLAIN, 11));
            deptLbl.setForeground(UIConstants.TEXT_MUTED);
            deptLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            info.add(nameLbl);
            info.add(Box.createVerticalStrut(3));
            info.add(deptLbl);

            JPanel infoWrap = new JPanel(new GridBagLayout());
            infoWrap.setOpaque(false);
            infoWrap.setPreferredSize(new Dimension(180, 58));
            infoWrap.setMinimumSize(new Dimension(180, 58));

            GridBagConstraints infoGbc = new GridBagConstraints();
            infoGbc.gridx = 0;
            infoGbc.gridy = 0;
            infoGbc.anchor = GridBagConstraints.WEST;
            infoGbc.fill = GridBagConstraints.NONE;
            infoGbc.weightx = 1.0;

            infoWrap.add(info, infoGbc);

            String status = r.getStatus() != null ? r.getStatus() : "";
            StatusBadge badge = new StatusBadge(status);

            Dimension badgeSize = new Dimension(92, 28);
            badge.setPreferredSize(badgeSize);
            badge.setMinimumSize(badgeSize);
            badge.setMaximumSize(badgeSize);

            JPanel badgeWrap = new JPanel(new GridBagLayout());
            badgeWrap.setOpaque(false);
            badgeWrap.setPreferredSize(new Dimension(104, 58));
            badgeWrap.setMinimumSize(new Dimension(104, 58));

            GridBagConstraints badgeGbc = new GridBagConstraints();
            badgeGbc.gridx = 0;
            badgeGbc.gridy = 0;
            badgeGbc.anchor = GridBagConstraints.CENTER;

            badgeWrap.add(badge, badgeGbc);

            JPanel left = new JPanel(new BorderLayout(8, 0));
            left.setOpaque(false);
            left.add(avWrap, BorderLayout.WEST);
            left.add(infoWrap, BorderLayout.CENTER);

            row.add(left, BorderLayout.CENTER);
            row.add(badgeWrap, BorderLayout.EAST);

            rows.add(row);
        }

        recentList.add(rows, BorderLayout.CENTER);
        recentList.revalidate();
        recentList.repaint();
    }

    private void refreshStatusBar(List<String[]> statusStats) {
        statusCard.removeAll();

        JLabel title = new JLabel("상태별 예약 비율");
        title.setFont(UIConstants.f(Font.BOLD, 13));
        title.setForeground(UIConstants.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        statusCard.add(title, BorderLayout.NORTH);

        int total = 0;
        for (String[] s : statusStats) {
            try { total += Integer.parseInt(s[1]); } catch (Exception ignored) {}
        }
        final int finalTotal = Math.max(total, 1);

        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(229, 231, 235));
                g2.fill(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                int x = 0;
                for (String[] s : statusStats) {
                    int cnt = 0;
                    try { cnt = Integer.parseInt(s[1]); } catch (Exception ignored) {}
                    if (cnt == 0) continue;
                    int w = (int)((double) cnt / finalTotal * getWidth());
                    g2.setColor(statusColor(s[0]));
                    g2.fillRect(x, 0, w, getHeight());
                    x += w;
                }
                g2.dispose();
            }
        };
        bar.setPreferredSize(new Dimension(0, 18));
        bar.setOpaque(false);
        statusCard.add(bar, BorderLayout.CENTER);

        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        legend.setOpaque(false);
        legend.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        for (String[] s : statusStats) {
            int cnt = 0;
            try { cnt = Integer.parseInt(s[1]); } catch (Exception ignored) {}
            int pct = (int)((double) cnt / finalTotal * 100);

            JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
            item.setOpaque(false);

            JLabel dot = new JLabel("●");
            dot.setFont(UIConstants.f(Font.PLAIN, 11));
            dot.setForeground(statusColor(s[0]));

            JLabel lbl = new JLabel("  " + s[0] + "  " + cnt + "건 (" + pct + "%)");
            lbl.setFont(UIConstants.f(Font.PLAIN, 11));
            lbl.setForeground(UIConstants.TEXT_SECONDARY);

            item.add(dot);
            item.add(lbl);
            legend.add(item);
        }

        statusCard.add(legend, BorderLayout.SOUTH);
        statusCard.revalidate();
        statusCard.repaint();
    }

    private Color statusColor(String status) {
        switch (status) {
            case "PENDING":   return UIConstants.PENDING_FG;
            case "CONFIRMED": return UIConstants.CONFIRMED_FG;
            case "COMPLETED": return new Color(124, 58, 237);
            case "CANCELLED": return UIConstants.DANGER;
            default:          return Color.GRAY;
        }
    }

    public void refresh() {
        try {
            int[] stats = adminService.getOverallStats();
            totalLbl.setText(String.valueOf(stats[0]));
            confirmedLbl.setText(String.valueOf(stats[1]));
            pendingLbl.setText(String.valueOf(stats[4]));
            cancelledLbl.setText(String.valueOf(stats[3]));
            completedLbl.setText(String.valueOf(stats[2]));

            List<String[]> deptStats = adminService.getReservationStatsByDepartment();
            refreshChart(deptStats);

            List<ReservationDetail> all = adminService.getAllReservationDetails();
            refreshRecent(all);

            List<String[]> statusStats = adminService.getReservationStatsByStatus();
            refreshStatusBar(statusStats);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    "데이터 로딩 오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}

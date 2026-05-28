package DB2026Team02.view.student;

import DB2026Team02.model.ReservationDetail;
import DB2026Team02.service.StudentService;
import DB2026Team02.view.MainFrame;
import DB2026Team02.view.common.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.SQLException;
import java.util.List;

public class StudentHomePanel extends JPanel {

    private JLabel welcomeLabel;
    private DefaultTableModel tableModel;

    public StudentHomePanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG);

        add(new NavBar(NavBar.STUDENT), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(buildBody());
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UIConstants.BG);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(UIConstants.BG);

        body.add(buildBanner());
        body.add(Box.createVerticalStrut(28));
        body.add(buildQuickMenu());
        body.add(Box.createVerticalStrut(28));
        body.add(buildRecentTable());
        body.add(Box.createVerticalStrut(28));

        return body;
    }

    private JPanel buildBanner() {
        JPanel banner = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                GradientPaint gp = new GradientPaint(
                        0, 0, UIConstants.PRIMARY_DARK,
                        getWidth(), 0, UIConstants.PRIMARY
                );

                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        banner.setPreferredSize(new Dimension(1280, 120));
        banner.setMaximumSize(new Dimension(Short.MAX_VALUE, 120));

        welcomeLabel = new JLabel("안녕하세요, 김이화 님!");
        welcomeLabel.setFont(UIConstants.f(Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(80, 32, 600, 40);
        banner.add(welcomeLabel);

        return banner;
    }

    private JPanel buildQuickMenu() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);
        section.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 80));
        section.setMaximumSize(new Dimension(Short.MAX_VALUE, 160));

        JLabel title = new JLabel("빠른 메뉴");
        title.setFont(UIConstants.f(Font.BOLD, 16));
        title.setForeground(UIConstants.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        section.add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 2, 16, 0));
        cards.setOpaque(false);

        Object[][] menus = {
                {"학과 검색", "키워드로 학과 및 교수 검색", MainFrame.DEPT_SEARCH},
                {"내 예약 조회", "예약 내역 및 상태 확인", MainFrame.MY_RESERVATIONS}
        };

        for (Object[] m : menus) {
            cards.add(buildMenuCard((String) m[0], (String) m[1], (String) m[2]));
        }

        section.add(cards, BorderLayout.CENTER);

        return section;
    }

    private JPanel buildMenuCard(String title, String desc, String target) {
        JPanel card = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(UIConstants.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));

                g2.dispose();
            }
        };

        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel arrow = new JLabel("→");
        arrow.setFont(UIConstants.f(Font.BOLD, 16));
        arrow.setForeground(UIConstants.PRIMARY);
        arrow.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        top.add(arrow, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        info.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel tLabel = new JLabel(title);
        tLabel.setFont(UIConstants.f(Font.BOLD, 14));
        tLabel.setForeground(UIConstants.TEXT_PRIMARY);
        tLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel dLabel = new JLabel(desc);
        dLabel.setFont(UIConstants.f(Font.PLAIN, 12));
        dLabel.setForeground(UIConstants.TEXT_SECONDARY);
        dLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        info.add(tLabel);
        info.add(Box.createVerticalStrut(4));
        info.add(dLabel);

        card.add(info, BorderLayout.CENTER);

        MouseAdapter nav = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                MainFrame.navigate(target);
            }
        };

        card.addMouseListener(nav);
        top.addMouseListener(nav);
        arrow.addMouseListener(nav);
        info.addMouseListener(nav);
        tLabel.addMouseListener(nav);
        dLabel.addMouseListener(nav);

        return card;
    }

    private JPanel buildRecentTable() {
        JPanel section = new JPanel(new BorderLayout(0, 12));
        section.setOpaque(false);
        section.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 80));
        section.setMaximumSize(new Dimension(Short.MAX_VALUE, 400));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("최근 예약 내역");
        title.setFont(UIConstants.f(Font.BOLD, 16));
        title.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel viewAll = new JLabel("전체 보기 →");
        viewAll.setFont(UIConstants.f(Font.PLAIN, 13));
        viewAll.setForeground(UIConstants.PRIMARY);
        viewAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        viewAll.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MainFrame.navigate(MainFrame.MY_RESERVATIONS);
            }
        });

        header.add(title, BorderLayout.WEST);
        header.add(viewAll, BorderLayout.EAST);

        section.add(header, BorderLayout.NORTH);

        String[] cols = {"예약 ID", "학과", "교수", "부스", "날짜 / 시간", "상태", "생성일"};

        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(UIConstants.BORDER));
        scroll.setBackground(UIConstants.WHITE);
        scroll.getViewport().setBackground(UIConstants.WHITE);

        section.add(scroll, BorderLayout.CENTER);

        return section;
    }

    private void styleTable(JTable t) {
        t.setFont(UIConstants.f(Font.PLAIN, 13));
        t.setRowHeight(44);
        t.setShowVerticalLines(false);
        t.setGridColor(UIConstants.BORDER);
        t.setBackground(UIConstants.WHITE);

        t.setSelectionBackground(UIConstants.WHITE);
        t.setSelectionForeground(UIConstants.TEXT_PRIMARY);
        t.setFocusable(false);

        JTableHeader header = t.getTableHeader();
        header.setFont(UIConstants.f(Font.BOLD, 12));
        header.setBackground(UIConstants.SURFACE);
        header.setForeground(UIConstants.TEXT_SECONDARY);
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table,
                        value,
                        false,
                        false,
                        row,
                        column
                );

                lbl.setFont(UIConstants.f(Font.PLAIN, 13));
                lbl.setForeground(UIConstants.TEXT_PRIMARY);
                lbl.setBackground(UIConstants.WHITE);
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

                return lbl;
            }
        };

        for (int i = 0; i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(defaultRenderer);
        }

        // 예약 ID column
        t.getColumnModel().getColumn(0).setCellRenderer((tbl, val, sel, foc, row, col) -> {
            JLabel lbl = new JLabel(val == null ? "" : val.toString());
            lbl.setFont(UIConstants.f(Font.BOLD, 13));
            lbl.setForeground(UIConstants.PRIMARY);
            lbl.setBackground(UIConstants.WHITE);
            lbl.setOpaque(true);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            return lbl;
        });

        // 상태 column
        t.getColumnModel().getColumn(5).setCellRenderer((tbl, val, sel, foc, row, col) -> {
            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setBackground(UIConstants.WHITE);
            wrapper.setOpaque(true);

            StatusBadge badge = new StatusBadge(val == null ? "" : val.toString());
            badge.setOpaque(false);

            wrapper.add(badge);

            return wrapper;
        });

        int[] widths = {80, 140, 110, 150, 170, 120, 110};

        for (int i = 0; i < widths.length; i++) {
            TableColumn col = t.getColumnModel().getColumn(i);
            col.setPreferredWidth(widths[i]);
            col.setMinWidth(Math.max(60, widths[i] - 30));
        }

        // row 클릭하면 예약 상세 페이지로 이동
        t.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = t.rowAtPoint(e.getPoint());

                if (row < 0) return;

                int id = parseId(tableModel.getValueAt(row, 0).toString());

                ReservationDetail selected = null;

                try {
                    int studentId = MainFrame.getStudentId();

                    if (studentId == -1) return;

                    List<ReservationDetail> list = new StudentService().getMyReservations(studentId);

                    selected = list.stream()
                            .filter(r -> r.getReservationId() == id)
                            .findFirst()
                            .orElse(null);

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(
                            StudentHomePanel.this,
                            "데이터 로딩 오류: " + ex.getMessage(),
                            "오류",
                            JOptionPane.ERROR_MESSAGE
                    );
                }

                if (selected != null) {
                    MainFrame.setSelectedReservation(selected);
                    MainFrame.navigate(MainFrame.RESERVATION_DETAIL);
                }
            }
        });
    }

    private int parseId(String s) {
        try {
            return Integer.parseInt(s.replace("#", "").trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void refresh() {
        String name = MainFrame.getStudentName();

        if (welcomeLabel != null) {
            welcomeLabel.setText("안녕하세요, " + (name.isEmpty() ? "학생" : name) + " 님!");
        }

        tableModel.setRowCount(0);

        int studentId = MainFrame.getStudentId();

        if (studentId == -1) {
            return;
        }

        try {
            StudentService svc = new StudentService();
            List<ReservationDetail> list = svc.getMyReservations(studentId);

            int limit = Math.min(list.size(), 5);

            for (int i = 0; i < limit; i++) {
                ReservationDetail r = list.get(i);

                tableModel.addRow(new Object[]{
                        "#" + r.getReservationId(),
                        r.getDepartmentName(),
                        formatProfessorName(r.getProfessorName()),
                        r.getBoothName(),
                        r.getSlotDate() + "  " + formatTime(r),
                        r.getStatus(),
                        r.getCreatedAt() != null ? r.getCreatedAt().toLocalDate().toString() : ""
                });
            }

        } catch (SQLException ex) {
            // DB unavailable — show empty table silently
        }
    }

    private String formatProfessorName(String name) {
        if (name == null || name.isBlank()) return "";
        if (name.endsWith("교수")) return name;
        return name + " 교수";
    }

    private String formatTime(ReservationDetail r) {
        if (r.getStartTime() == null) return "";

        String start = r.getStartTime().toString();

        if (start.length() >= 5) {
            start = start.substring(0, 5);
        }

        return start;
    }
}
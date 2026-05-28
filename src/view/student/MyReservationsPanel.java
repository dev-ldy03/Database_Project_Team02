package DB2026Team02.view.student;

import DB2026Team02.model.ReservationDetail;
import DB2026Team02.service.ReservationService;
import DB2026Team02.service.StudentService;
import DB2026Team02.view.MainFrame;
import DB2026Team02.view.common.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyReservationsPanel extends JPanel {

    private DefaultTableModel tableModel;
    private JTable reservationTable;

    private List<ReservationDetail> allReservations = new ArrayList<>();
    private String activeTab = "전체";
    private JPanel tabBar;
    private JLabel subTitleLabel;

    private static final String[] TABS = {
            "전체", "CONFIRMED", "PENDING", "COMPLETED", "CANCELLED"
    };

    private static final String[] COLS = {
            "예약 ID", "학과", "교수", "부스", "날짜", "시간", "상태", "메모", "작업"
    };

    private static final int[] WIDTHS = {
            90, 150, 130, 160, 120, 150, 140, 150, 210
    };

    public MyReservationsPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG);

        add(new NavBar(NavBar.STUDENT), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(UIConstants.BG);

        body.add(buildPageHeader(), BorderLayout.NORTH);
        body.add(buildTableArea(), BorderLayout.CENTER);

        return body;
    }

    private JPanel buildPageHeader() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(UIConstants.WHITE);
        wrap.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UIConstants.BORDER),
                BorderFactory.createEmptyBorder(18, 80, 0, 80)
        ));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);

        JLabel title = new JLabel("내 예약 내역");
        title.setFont(UIConstants.f(Font.BOLD, 24));
        title.setForeground(UIConstants.TEXT_PRIMARY);

        subTitleLabel = new JLabel("");
        subTitleLabel.setFont(UIConstants.f(Font.PLAIN, 14));
        subTitleLabel.setForeground(UIConstants.TEXT_SECONDARY);

        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(6));
        titleBox.add(subTitleLabel);

        topRow.add(titleBox, BorderLayout.WEST);

        GreenButton newBtn = new GreenButton("+ 새 예약");
        newBtn.setPreferredSize(new Dimension(96, 30));
        newBtn.setFont(UIConstants.f(Font.BOLD, 14));
        newBtn.addActionListener(e -> MainFrame.navigate(MainFrame.DEPT_SEARCH));
        topRow.add(newBtn, BorderLayout.EAST);

        wrap.add(topRow, BorderLayout.NORTH);

        tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabBar.setOpaque(false);
        wrap.add(tabBar, BorderLayout.SOUTH);

        return wrap;
    }

    private void buildTabs() {
        tabBar.removeAll();

        for (String tab : TABS) {
            long count = allReservations.stream()
                    .filter(r -> "전체".equals(tab) || tab.equals(r.getStatus()))
                    .count();

            tabBar.add(buildTabButton(tab, (int) count));
        }

        tabBar.revalidate();
        tabBar.repaint();
    }

    private JPanel buildTabButton(String label, int count) {
        boolean active = label.equals(activeTab);

        JPanel tab = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (active) {
                    g.setColor(UIConstants.PRIMARY);
                    g.fillRect(0, getHeight() - 3, getWidth(), 3);
                }
            }
        };

        tab.setBackground(UIConstants.WHITE);
        tab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        int tabWidth = getTabWidth(label);
        tab.setPreferredSize(new Dimension(tabWidth, 52));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.f(active ? Font.BOLD : Font.PLAIN, 14));
        lbl.setForeground(active ? UIConstants.PRIMARY : UIConstants.TEXT_SECONDARY);
        lbl.setBounds(12, 16, tabWidth - 54, 22);
        tab.add(lbl);

        JLabel badge = new JLabel(String.valueOf(count), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(active ? UIConstants.PRIMARY : UIConstants.BORDER);
                g2.fill(new Ellipse2D.Float(0, 0, getWidth(), getHeight()));

                g2.dispose();
                super.paintComponent(g);
            }
        };

        badge.setFont(UIConstants.f(Font.BOLD, 11));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(false);
        badge.setBounds(tabWidth - 36, 16, 22, 20);
        tab.add(badge);

        tab.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                activeTab = label;
                buildTabs();
                applyFilter();
            }
        });

        return tab;
    }

    private int getTabWidth(String label) {
        if ("전체".equals(label)) return 92;
        if ("CONFIRMED".equals(label)) return 150;
        if ("PENDING".equals(label)) return 130;
        if ("COMPLETED".equals(label)) return 150;
        if ("CANCELLED".equals(label)) return 150;
        return 130;
    }

    private JPanel buildTableArea() {
        JPanel area = new JPanel(new BorderLayout());
        area.setBackground(UIConstants.BG);
        area.setBorder(BorderFactory.createEmptyBorder(24, 80, 24, 80));

        tableModel = new DefaultTableModel(COLS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        reservationTable = new JTable(tableModel);
        styleTable(reservationTable);

        JScrollPane scroll = new JScrollPane(reservationTable);
        scroll.setBorder(new LineBorder(UIConstants.BORDER));
        scroll.getViewport().setBackground(UIConstants.WHITE);

        area.add(scroll, BorderLayout.CENTER);

        return area;
    }

    private void styleTable(JTable t) {
        t.setFont(UIConstants.f(Font.PLAIN, 14));
        t.setRowHeight(56);
        t.setShowVerticalLines(false);
        t.setGridColor(UIConstants.BORDER);
        t.setBackground(UIConstants.WHITE);
        t.setSelectionBackground(UIConstants.WHITE);
        t.setSelectionForeground(UIConstants.TEXT_PRIMARY);
        t.setFocusable(false);

        JTableHeader header = t.getTableHeader();
        header.setFont(UIConstants.f(Font.BOLD, 13));
        header.setBackground(UIConstants.SURFACE);
        header.setForeground(UIConstants.TEXT_SECONDARY);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));

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
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table,
                        value,
                        false,
                        false,
                        row,
                        column
                );

                label.setFont(UIConstants.f(Font.PLAIN, 14));
                label.setForeground(UIConstants.TEXT_PRIMARY);
                label.setBackground(UIConstants.WHITE);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                return label;
            }
        };

        for (int i = 0; i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(defaultRenderer);
        }

        t.getColumnModel().getColumn(0).setCellRenderer((tbl, val, sel, foc, r, c) -> {
            JLabel l = new JLabel(val == null ? "" : val.toString());
            l.setFont(UIConstants.f(Font.BOLD, 14));
            l.setForeground(UIConstants.PRIMARY);
            l.setBackground(UIConstants.WHITE);
            l.setOpaque(true);
            l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            return l;
        });

        t.getColumnModel().getColumn(6).setCellRenderer((tbl, val, sel, foc, r, c) -> {
            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setBackground(UIConstants.WHITE);

            StatusBadge badge = new StatusBadge(val == null ? "" : val.toString());
            badge.setOpaque(false);

            wrapper.add(badge);
            return wrapper;
        });

        t.getColumnModel().getColumn(8).setCellRenderer((tbl, val, sel, foc, row, col) -> {
            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setBackground(UIConstants.WHITE);

            JPanel buttonBox = new JPanel(new GridLayout(1, 2, 14, 0));
            buttonBox.setOpaque(false);

            String status = tableModel.getValueAt(row, 6) == null
                    ? ""
                    : tableModel.getValueAt(row, 6).toString();

            JButton detailBtn = smallBtn("상세", UIConstants.PRIMARY);
            buttonBox.add(detailBtn);

            if ("CONFIRMED".equals(status) || "PENDING".equals(status)) {
                JButton cancelBtn = smallBtn("취소", UIConstants.DANGER);
                buttonBox.add(cancelBtn);
            } else {
                JPanel empty = new JPanel();
                empty.setOpaque(false);
                buttonBox.add(empty);
            }

            wrapper.add(buttonBox);

            return wrapper;
        });

        t.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = t.rowAtPoint(e.getPoint());
                int col = t.columnAtPoint(e.getPoint());

                if (row < 0) return;

                if (col == 8) {
                    String status = tableModel.getValueAt(row, 6) == null
                            ? ""
                            : tableModel.getValueAt(row, 6).toString();

                    Rectangle cellRect = t.getCellRect(row, col, false);
                    int relX = e.getX() - cellRect.x;

                    boolean canCancel = "CONFIRMED".equals(status) || "PENDING".equals(status);

                    if (canCancel && relX > cellRect.width / 2) {
                        onCancel(row);
                    } else {
                        onDetail(row);
                    }

                    return;
                }

                onDetail(row);
            }
        });

        for (int i = 0; i < WIDTHS.length; i++) {
            TableColumn col = t.getColumnModel().getColumn(i);
            col.setPreferredWidth(WIDTHS[i]);
            col.setMinWidth(Math.max(70, WIDTHS[i] - 30));
        }

        t.getColumnModel().getColumn(8).setPreferredWidth(210);
        t.getColumnModel().getColumn(8).setMinWidth(200);
        t.getColumnModel().getColumn(8).setMaxWidth(240);
    }

    private JButton smallBtn(String text, Color color) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(new Color(
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue(),
                        25
                ));

                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));

                g2.dispose();
                super.paintComponent(g);
            }
        };

        b.setFont(UIConstants.f(Font.BOLD, 12));
        b.setForeground(color);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(72, 30));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return b;
    }

    private void onDetail(int row) {
        int id = parseId(tableModel.getValueAt(row, 0).toString());

        ReservationDetail rd = allReservations.stream()
                .filter(r -> r.getReservationId() == id)
                .findFirst()
                .orElse(null);

        if (rd != null) {
            MainFrame.setSelectedReservation(rd);
            MainFrame.navigate(MainFrame.RESERVATION_DETAIL);
        }
    }

    private void onCancel(int row) {
        int id = parseId(tableModel.getValueAt(row, 0).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "예약 #" + id + "를 취소하시겠습니까?",
                "예약 취소",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = new ReservationService().cancelReservation(id);

            if (ok) {
                JOptionPane.showMessageDialog(
                        this,
                        "예약이 취소되었습니다.",
                        "완료",
                        JOptionPane.INFORMATION_MESSAGE
                );

                refresh();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "취소할 수 없는 예약입니다.",
                        "오류",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "오류: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private int parseId(String s) {
        try {
            return Integer.parseInt(s.replace("#", "").trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void applyFilter() {
        tableModel.setRowCount(0);

        for (ReservationDetail r : allReservations) {
            if ("전체".equals(activeTab) || activeTab.equals(r.getStatus())) {
                tableModel.addRow(new Object[]{
                        "#" + r.getReservationId(),
                        nullToBlank(r.getDepartmentName()),
                        formatProfessorName(r.getProfessorName()),
                        nullToBlank(r.getBoothName()),
                        r.getSlotDate() != null ? r.getSlotDate().toString() : "",
                        formatTimeRange(r),
                        nullToBlank(r.getStatus()),
                        extractNotes(r),
                        ""
                });
            }
        }
    }

    private String formatProfessorName(String name) {
        if (name == null || name.isBlank()) return "";
        if (name.endsWith("교수")) return name;
        return name + " 교수";
    }

    private String formatTimeRange(ReservationDetail r) {
        if (r.getStartTime() == null) return "";

        String start = r.getStartTime().toString();
        String end = r.getEndTime() != null ? r.getEndTime().toString() : "";

        if (start.length() >= 5) start = start.substring(0, 5);
        if (end.length() >= 5) end = end.substring(0, 5);

        return end.isBlank() ? start : start + " - " + end;
    }

    private String extractNotes(ReservationDetail r) {
        String[] methodNames = {"getNotes", "getMemo", "getRequestNotes"};

        for (String methodName : methodNames) {
            try {
                Method m = r.getClass().getMethod(methodName);
                Object value = m.invoke(r);

                if (value != null && !value.toString().isBlank()) {
                    return value.toString();
                }

            } catch (Exception ignored) {
            }
        }

        return "-";
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }

    public void refresh() {
        allReservations.clear();

        int studentId = MainFrame.getStudentId();

        if (subTitleLabel != null) {
            String studentName = MainFrame.getStudentName();
            subTitleLabel.setText(
                    "학생 " + (studentName == null || studentName.isBlank() ? "" : studentName + " ") + "님의 전체 예약 내역입니다."
            );
        }

        if (studentId == -1) {
            buildTabs();
            applyFilter();
            return;
        }

        try {
            allReservations = new StudentService().getMyReservations(studentId);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "데이터 로딩 오류: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        buildTabs();
        applyFilter();
    }
}
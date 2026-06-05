package DB2026Team02.view.admin;

import DB2026Team02.model.OnlineLink;
import DB2026Team02.model.ReservationDetail;
import DB2026Team02.service.AdminService;
import DB2026Team02.service.ReservationService;
import DB2026Team02.view.MainFrame;
import DB2026Team02.view.common.GreenButton;
import DB2026Team02.view.common.StatusBadge;
import DB2026Team02.view.common.UIConstants;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservationListPanel extends JPanel {

    private static final int COL_ID     = 0;
    private static final int COL_NAME   = 1;
    private static final int COL_DEPT   = 2;
    private static final int COL_BOOTH  = 3;
    private static final int COL_PROF   = 4;
    private static final int COL_DATE   = 5;
    private static final int COL_TIME   = 6;
    private static final int COL_STATUS = 7;
    private static final int COL_ACTION = 8;

    private static final String[] COLS = {
            "ID", "학생명", "학생 학과", "부스", "교수", "날짜", "시간", "상태", "작업"
    };

    private static final int[] WIDTHS = {
            55, 80, 115, 120, 80, 95, 105, 95, 168
    };

    private static final int ACTION_BTN_W = 46;
    private static final int ACTION_BTN_GAP = 4;

    private final ActionCellRenderer actionCellRenderer = new ActionCellRenderer();

    private final AdminService adminService = new AdminService();
    private final ReservationService resvService = new ReservationService();

    private DefaultTableModel tableModel;
    private JTable table;
    private List<ReservationDetail> allData = new ArrayList<>();

    private JLabel totalCountLbl;

    // 상태별 / 부스별 예약 조회용 필터
    private JComboBox<String> statusFilter;
    private JComboBox<String> boothFilter;
    private JTextField searchField;
    private boolean updatingFilter = false;

    public ReservationListPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG);

        add(new AdminSideBar(MainFrame.ADMIN_RESERVATIONS), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(UIConstants.BG);
        c.add(buildTitleBar(), BorderLayout.NORTH);
        c.add(buildTableArea(), BorderLayout.CENTER);
        return c;
    }

    private JPanel buildTitleBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIConstants.WHITE);
        p.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UIConstants.BORDER),
                BorderFactory.createEmptyBorder(16, 32, 16, 32)
        ));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel title = new JLabel("예약 목록");
        title.setFont(UIConstants.f(Font.BOLD, 22));
        title.setForeground(UIConstants.TEXT_PRIMARY);

        totalCountLbl = new JLabel("0");
        totalCountLbl.setFont(UIConstants.f(Font.BOLD, 13));
        totalCountLbl.setForeground(Color.WHITE);
        totalCountLbl.setOpaque(false);
        totalCountLbl.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        JPanel badge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIConstants.PRIMARY);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setOpaque(false);
        badge.setLayout(new BorderLayout());
        badge.add(totalCountLbl);

        left.add(title);
        left.add(badge);

        // 예약 상태 / 부스 기준 필터
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filterPanel.setOpaque(false);

        searchField = new JTextField();
        searchField.setFont(UIConstants.f(Font.PLAIN, 12));
        searchField.setPreferredSize(new Dimension(220, 32));
        searchField.setToolTipText("학생명 / 학과 / 부스 검색");
        searchField.setBorder(new CompoundBorder(
                new LineBorder(UIConstants.BORDER, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        searchField.addActionListener(e -> applyFilters());
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                applyFilters();
            }
        });

        statusFilter = new JComboBox<>(new String[]{
                "상태 전체", "PENDING", "CONFIRMED", "COMPLETED", "CANCELLED"
        });
        statusFilter.setFont(UIConstants.f(Font.PLAIN, 12));
        statusFilter.setPreferredSize(new Dimension(125, 32));

        boothFilter = new JComboBox<>();
        boothFilter.setFont(UIConstants.f(Font.PLAIN, 12));
        boothFilter.setPreferredSize(new Dimension(180, 32));

        statusFilter.addActionListener(e -> {
            if (!updatingFilter) applyFilters();
        });

        boothFilter.addActionListener(e -> {
            if (!updatingFilter) applyFilters();
        });

        JLabel searchLabel = new JLabel("학생 / 학과 / 부스 검색");
        searchLabel.setFont(UIConstants.f(Font.PLAIN, 12));
        searchLabel.setForeground(UIConstants.TEXT_SECONDARY);


        filterPanel.add(searchField);
        filterPanel.add(statusFilter);
        filterPanel.add(boothFilter);

        left.add(filterPanel);

        GreenButton exportBtn = new GreenButton("내보내기", new Color(71, 85, 105), new Color(51, 65, 85));
        exportBtn.setFont(UIConstants.f(Font.BOLD, 12));
        exportBtn.setPreferredSize(new Dimension(96, 32));
        exportBtn.addActionListener(e -> exportCsv());

        p.add(left, BorderLayout.WEST);
        p.add(exportBtn, BorderLayout.EAST);

        return p;
    }

    private JPanel buildTableArea() {
        JPanel area = new JPanel(new BorderLayout());
        area.setBackground(UIConstants.BG);
        area.setBorder(BorderFactory.createEmptyBorder(20, 32, 20, 32));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIConstants.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(UIConstants.BORDER),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        tableModel = new DefaultTableModel(COLS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFillsViewportHeight(false);
        styleTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UIConstants.WHITE);

        card.add(scroll, BorderLayout.CENTER);
        area.add(card, BorderLayout.CENTER);

        return area;
    }

    private void styleTable() {
        table.setFont(UIConstants.f(Font.PLAIN, 12));
        table.setRowHeight(50);
        table.setShowVerticalLines(false);
        table.setGridColor(UIConstants.BORDER);
        table.setBackground(UIConstants.WHITE);
        table.setSelectionBackground(new Color(248, 250, 252));
        table.setSelectionForeground(UIConstants.TEXT_PRIMARY);
        table.setFocusable(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader hdr = table.getTableHeader();
        hdr.setFont(UIConstants.f(Font.BOLD, 11));
        hdr.setBackground(UIConstants.SURFACE);
        hdr.setForeground(UIConstants.TEXT_SECONDARY);
        hdr.setReorderingAllowed(false);
        hdr.setPreferredSize(new Dimension(hdr.getPreferredSize().width, 36));

        DefaultTableCellRenderer base = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col
            ) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, false, false, row, col);
                l.setFont(UIConstants.f(Font.PLAIN, 12));
                l.setForeground(UIConstants.TEXT_PRIMARY);
                l.setBackground(UIConstants.WHITE);
                l.setOpaque(true);
                l.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                l.setToolTipText(v == null ? "" : v.toString());
                return l;
            }
        };

        for (int i = 0; i < COLS.length; i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setPreferredWidth(WIDTHS[i]);
            col.setCellRenderer(base);
        }

        table.getColumnModel().getColumn(COL_ID).setCellRenderer((t, v, sel, foc, row, col) -> {
            JLabel l = new JLabel(v == null ? "" : v.toString());
            l.setFont(UIConstants.f(Font.BOLD, 12));
            l.setForeground(UIConstants.PRIMARY);
            l.setBackground(UIConstants.WHITE);
            l.setOpaque(true);
            l.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            l.setToolTipText(v == null ? "" : v.toString());
            return l;
        });

        table.getColumnModel().getColumn(COL_STATUS).setCellRenderer((t, v, sel, foc, row, col) -> {
            String status = v == null ? "" : v.toString();

            JPanel wrap = new JPanel(new GridBagLayout());
            wrap.setBackground(UIConstants.WHITE);

            StatusBadge badge = new StatusBadge(status);
            Dimension badgeSize = new Dimension(88, 24);
            badge.setPreferredSize(badgeSize);
            badge.setMinimumSize(badgeSize);
            badge.setMaximumSize(badgeSize);

            wrap.add(badge);
            return wrap;
        });

        table.getColumnModel().getColumn(COL_ACTION).setCellRenderer(actionCellRenderer);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int viewRow = table.rowAtPoint(e.getPoint());
                int viewCol = table.columnAtPoint(e.getPoint());
                if (viewRow < 0 || viewCol != COL_ACTION) return;

                Rectangle cellRect = table.getCellRect(viewRow, viewCol, false);
                int relX = e.getX() - cellRect.x;
                handleActionClick(viewRow, relX);
            }
        });
    }

    private void handleActionClick(int viewRow, int relX) {
        relX -= ACTION_BTN_GAP;
        int step = ACTION_BTN_W + ACTION_BTN_GAP;

        if (relX < ACTION_BTN_W) {
            doCancel(viewRow);
            return;
        }
        relX -= step;

        if (relX < ACTION_BTN_W) {
            doDelete(viewRow);
            return;
        }
        relX -= step;

        if (isOnlineOrHybridRow(viewRow) && relX < ACTION_BTN_W) {
            doRegisterOnlineLink(viewRow);
        }
    }

    private boolean isOnlineOrHybridRow(int row) {
        String boothType = boothTypeForRow(row);
        return "ONLINE".equals(boothType) || "HYBRID".equals(boothType);
    }

    private String boothTypeForRow(int row) {
        int id = parseId(tableModel.getValueAt(row, COL_ID).toString());
        return allData.stream()
                .filter(r -> r.getReservationId() == id)
                .map(ReservationDetail::getBoothType)
                .findFirst()
                .orElse("");
    }

    private static JLabel actionLabel(String text, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 18));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        label.setFont(UIConstants.f(Font.BOLD, 10));
        label.setForeground(color);
        label.setOpaque(false);
        Dimension size = new Dimension(ACTION_BTN_W, 26);
        label.setPreferredSize(size);
        label.setMinimumSize(size);
        label.setMaximumSize(size);
        return label;
    }

    private final class ActionCellRenderer implements TableCellRenderer {
        private final JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, ACTION_BTN_GAP, 10));
        private final JLabel cancelLabel = actionLabel("취소", UIConstants.WARNING_FG);
        private final JLabel deleteLabel = actionLabel("삭제", UIConstants.DANGER);
        private final JLabel linkLabel = actionLabel("링크", UIConstants.PRIMARY);

        ActionCellRenderer() {
            wrap.setBackground(UIConstants.WHITE);
            makeNonInteractive(wrap);
            makeNonInteractive(cancelLabel);
            makeNonInteractive(deleteLabel);
            makeNonInteractive(linkLabel);
        }

        private void makeNonInteractive(JComponent c) {
            c.setEnabled(false);
            c.setFocusable(false);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column
        ) {
            wrap.removeAll();
            wrap.add(cancelLabel);
            wrap.add(deleteLabel);
            if (isOnlineOrHybridRow(row)) {
                wrap.add(linkLabel);
            }
            return wrap;
        }
    }

    private void doCancel(int row) {
        int id = parseId(tableModel.getValueAt(row, COL_ID).toString());
        String status = tableModel.getValueAt(row, COL_STATUS).toString();

        if ("CANCELLED".equals(status)) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    "이미 취소된 예약입니다.",
                    "안내",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        if ("COMPLETED".equals(status)) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    "이미 완료된 예약은 취소할 수 없습니다.",
                    "안내",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(MainFrame.getInstance(),
                "예약 #" + id + "을 취소하시겠습니까?",
                "예약 취소",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = resvService.cancelReservation(id);

            if (ok) {
                JOptionPane.showMessageDialog(MainFrame.getInstance(),
                        "예약 #" + id + " 취소 완료.",
                        "완료",
                        JOptionPane.INFORMATION_MESSAGE
                );
                SwingUtilities.invokeLater(this::refresh);
            } else {
                JOptionPane.showMessageDialog(MainFrame.getInstance(),
                        "예약 취소 실패.",
                        "오류",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    "오류: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void doDelete(int row) {
        int id = parseId(tableModel.getValueAt(row, COL_ID).toString());

        int confirm = JOptionPane.showConfirmDialog(MainFrame.getInstance(),
                "정말 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.",
                "예약 삭제",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = resvService.deleteReservation(id);

            if (ok) {
                JOptionPane.showMessageDialog(MainFrame.getInstance(),
                        "예약 #" + id + "이(가) 삭제되었습니다.",
                        "완료",
                        JOptionPane.INFORMATION_MESSAGE
                );
                SwingUtilities.invokeLater(this::refresh);
            } else {
                JOptionPane.showMessageDialog(MainFrame.getInstance(),
                        "예약 삭제에 실패했습니다.",
                        "오류",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    "오류: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void doRegisterOnlineLink(int row) {
        int id = parseId(tableModel.getValueAt(row, COL_ID).toString());

        if (!isOnlineOrHybridRow(row)) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    "온라인/하이브리드 부스 예약만 링크를 등록할 수 있습니다.",
                    "안내",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        try {
            OnlineLink existing = adminService.getOnlineLinkByReservationId(id);
            if (existing != null) {
                JOptionPane.showMessageDialog(MainFrame.getInstance(),
                        "예약 #" + id + "에 이미 온라인 링크가 등록되어 있습니다.",
                        "안내",
                        JOptionPane.INFORMATION_MESSAGE
                );
                return;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    "오류: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        showOnlineLinkDialog(id);
    }

    private void showOnlineLinkDialog(int reservationId) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                "온라인 상담 링크 등록 — 예약 #" + reservationId,
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(460, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("온라인 상담 링크 등록");
        title.setFont(UIConstants.f(Font.BOLD, 18));
        title.setForeground(UIConstants.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("예약 #" + reservationId + " · 회의 URL은 필수입니다.");
        sub.setFont(UIConstants.f(Font.PLAIN, 12));
        sub.setForeground(UIConstants.TEXT_SECONDARY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(sub);
        panel.add(Box.createVerticalStrut(18));

        JTextField urlField = linkFormField("", "https://zoom.us/j/123456789");
        JTextField pwField = linkFormField("", "1234 (선택)");
        JTextField expiresField = linkFormField("", "2026-05-20 11:00:00 (선택)");

        panel.add(labeledLinkField("회의 URL *", urlField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(labeledLinkField("비밀번호", pwField));
        panel.add(Box.createVerticalStrut(12));
        panel.add(labeledLinkField("만료 시간", expiresField));
        panel.add(Box.createVerticalStrut(20));

        GreenButton saveBtn = new GreenButton("등록하기");
        saveBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            String url = urlField.getText().trim();
            if (url.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "회의 URL은 필수입니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int linkId = adminService.registerOnlineLink(
                        reservationId,
                        url,
                        pwField.getText().trim(),
                        expiresField.getText().trim()
                );

                if (linkId != -1) {
                    JOptionPane.showMessageDialog(dialog,
                            "온라인 링크가 등록되었습니다.",
                            "완료",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    dialog.dispose();
                    SwingUtilities.invokeLater(this::refresh);
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "온라인 링크 등록에 실패했습니다. (중복·부스 유형·입력 형식 확인)",
                            "오류",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "오류: " + ex.getMessage(),
                        "오류",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        panel.add(saveBtn);
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private JTextField linkFormField(String value, String placeholder) {
        JTextField f = new JTextField(value);
        f.setFont(UIConstants.f(Font.PLAIN, 13));
        f.setBorder(new CompoundBorder(
                new LineBorder(UIConstants.BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return f;
    }

    private JPanel labeledLinkField(String label, JTextField field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.f(Font.BOLD, 13));
        lbl.setForeground(UIConstants.TEXT_PRIMARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(field);
        return p;
    }

    private void loadAllReservations() {
        updateBoothFilterItems();
        applyFilters();
    }

    // 상태별 / 부스별 예약 목록 필터링
    private void applyFilters() {
        if (tableModel == null) return;

        tableModel.setRowCount(0);

        String selectedStatus = statusFilter == null || statusFilter.getSelectedItem() == null
                ? "상태 전체"
                : statusFilter.getSelectedItem().toString();

        String selectedBooth = boothFilter == null || boothFilter.getSelectedItem() == null
                ? "부스 전체"
                : boothFilter.getSelectedItem().toString();

        String keyword = searchField == null ? "" : searchField.getText().trim().toLowerCase();

        int count = 0;

        for (ReservationDetail r : allData) {
            String status = r.getStatus() != null ? r.getStatus() : "";
            String boothName = r.getBoothName() != null ? r.getBoothName() : "";
            String studentName = r.getStudentName() != null ? r.getStudentName() : "";
            String studentMajor = r.getStudentMajor() != null ? r.getStudentMajor() : "";

            if (!"상태 전체".equals(selectedStatus) && !selectedStatus.equals(status)) {
                continue;
            }

            if (!"부스 전체".equals(selectedBooth) && !selectedBooth.equals(boothName)) {
                continue;
            }

            if (!keyword.isBlank()) {
                String searchable = (
                        studentName + " " +
                                studentMajor + " " +
                                boothName
                ).toLowerCase();

                if (!searchable.contains(keyword)) {
                    continue;
                }
            }

            tableModel.addRow(new Object[]{
                    "#" + r.getReservationId(),
                    r.getStudentName() != null ? r.getStudentName() : "",
                    r.getStudentMajor() != null ? r.getStudentMajor() : "",
                    boothName,
                    r.getProfessorName() != null ? r.getProfessorName() : "",
                    r.getSlotDate() != null ? r.getSlotDate().toString() : "",
                    timeRange(r),
                    status,
                    ""
            });

            count++;
        }

        if (totalCountLbl != null) {
            totalCountLbl.setText(String.valueOf(count));
        }
    }

    private void updateBoothFilterItems() {
        if (boothFilter == null) return;

        updatingFilter = true;

        String selected = boothFilter.getSelectedItem() == null
                ? "부스 전체"
                : boothFilter.getSelectedItem().toString();

        boothFilter.removeAllItems();
        boothFilter.addItem("부스 전체");

        List<String> boothNames = new ArrayList<>();

        for (ReservationDetail r : allData) {
            String boothName = r.getBoothName();

            if (boothName != null && !boothName.isBlank() && !boothNames.contains(boothName)) {
                boothNames.add(boothName);
                boothFilter.addItem(boothName);
            }
        }

        if (boothNames.contains(selected)) {
            boothFilter.setSelectedItem(selected);
        } else {
            boothFilter.setSelectedItem("부스 전체");
        }

        updatingFilter = false;
    }

    private String timeRange(ReservationDetail r) {
        if (r.getStartTime() == null) return "";

        String s = r.getStartTime().toString();
        String e = r.getEndTime() != null ? r.getEndTime().toString() : "";

        if (s.length() >= 5) s = s.substring(0, 5);
        if (e.length() >= 5) e = e.substring(0, 5);

        return e.isBlank() ? s : s + " - " + e;
    }

    private int parseId(String s) {
        try {
            return Integer.parseInt(s.replace("#", "").trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void exportCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("reservations.csv"));

        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (PrintWriter pw = new PrintWriter(
                new FileWriter(fc.getSelectedFile(), java.nio.charset.StandardCharsets.UTF_8)
        )) {
            pw.println(String.join(",", COLS));

            for (int r = 0; r < tableModel.getRowCount(); r++) {
                StringBuilder line = new StringBuilder();

                for (int c = 0; c < COL_ACTION; c++) {
                    if (c > 0) line.append(",");

                    Object val = tableModel.getValueAt(r, c);
                    line.append(val != null ? val.toString().replace(",", " ") : "");
                }

                pw.println(line);
            }

            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    "내보내기 완료!",
                    "완료",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    "내보내기 실패: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void refresh() {
        allData.clear();

        try {
            allData = adminService.getAllReservationDetails();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    "데이터 로딩 오류: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        loadAllReservations();
    }
}
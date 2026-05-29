package DB2026Team02.view.admin;

import DB2026Team02.model.ReservationDetail;
import DB2026Team02.service.AdminService;
import DB2026Team02.service.ReservationService;
import DB2026Team02.view.MainFrame;
import DB2026Team02.view.common.StatusBadge;
import DB2026Team02.view.common.UIConstants;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CheckInPanel extends JPanel {

    private static final int COL_ID     = 0;
    private static final int COL_NAME   = 1;
    private static final int COL_DEPT   = 2;
    private static final int COL_PROF   = 3;
    private static final int COL_DATE   = 4;
    private static final int COL_TIME   = 5;
    private static final int COL_BOOTH  = 6;
    private static final int COL_STATUS = 7;
    private static final int COL_ACTION = 8;

    private static final String[] COLS = {
            "예약 ID", "학생명", "예약학과", "교수", "날짜", "시간", "부스", "현재 상태", "작업"
    };

    private static final int[] WIDTHS = {
            60,   // 예약 ID
            75,   // 학생명
            105,  // 예약학과
            75,   // 교수
            90,   // 날짜
            95,   // 시간
            105,  // 부스
            95,   // 현재 상태
            170   // 작업
    };

    private final AdminService adminService = new AdminService();
    private final ReservationService resvService = new ReservationService();

    private static final int TABLE_PENDING    = 0;
    private static final int TABLE_CONFIRMED  = 1;
    private static final int TABLE_CHECKINGIN = 2;
    private static final int TABLE_NOSHOW     = 3;

    private DefaultTableModel pendingModel;
    private DefaultTableModel confirmedModel;
    private DefaultTableModel checkingInModel;
    private DefaultTableModel noShowModel;

    private JTable pendingTable;
    private JTable confirmedTable;
    private JTable checkingInTable;
    private JTable noShowTable;

    private JLabel pendingCountLbl;
    private JLabel confirmedCountLbl;
    private JLabel checkingInCountLbl;
    private JLabel noShowCountLbl;

    public CheckInPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG);

        add(new AdminSideBar(MainFrame.ADMIN_CHECKIN), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(UIConstants.BG);

        c.add(buildTitleBar(), BorderLayout.NORTH);
        c.add(buildBody(), BorderLayout.CENTER);

        return c;
    }

    private JPanel buildTitleBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIConstants.WHITE);
        p.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UIConstants.BORDER),
                BorderFactory.createEmptyBorder(20, 32, 20, 32)
        ));

        JLabel title = new JLabel("체크인 처리");
        title.setFont(UIConstants.f(Font.BOLD, 22));
        title.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel desc = new JLabel("예약 승인 및 체크인 관리");
        desc.setFont(UIConstants.f(Font.PLAIN, 13));
        desc.setForeground(UIConstants.TEXT_MUTED);

        p.add(title, BorderLayout.WEST);
        p.add(desc, BorderLayout.EAST);

        return p;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(UIConstants.BG);
        body.setBorder(BorderFactory.createEmptyBorder(20, 32, 20, 32));

        body.add(buildPendingCard());
        body.add(Box.createVerticalStrut(16));
        body.add(buildConfirmedCard());
        body.add(Box.createVerticalStrut(16));
        body.add(buildCheckingInCard());
        body.add(Box.createVerticalStrut(16));
        body.add(buildNoShowCard());

        return body;
    }

    private JPanel buildPendingCard() {
        JPanel card = buildBaseCard();
        card.add(buildSectionHeader("예약 승인 대기", "PENDING", TABLE_PENDING), BorderLayout.NORTH);

        pendingModel = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        pendingTable = new JTable(pendingModel);
        styleTable(pendingTable, TABLE_PENDING);

        JScrollPane scroll = new JScrollPane(pendingTable);
        scroll.setBorder(new LineBorder(UIConstants.BORDER));
        scroll.getViewport().setBackground(UIConstants.WHITE);
        scroll.setPreferredSize(new Dimension(0, 230));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildConfirmedCard() {
        JPanel card = buildBaseCard();
        card.add(buildSectionHeader("체크인 대상", "CONFIRMED", TABLE_CONFIRMED), BorderLayout.NORTH);

        confirmedModel = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        confirmedTable = new JTable(confirmedModel);
        styleTable(confirmedTable, TABLE_CONFIRMED);

        JScrollPane scroll = new JScrollPane(confirmedTable);
        scroll.setBorder(new LineBorder(UIConstants.BORDER));
        scroll.getViewport().setBackground(UIConstants.WHITE);
        scroll.setPreferredSize(new Dimension(0, 230));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildCheckingInCard() {
        JPanel card = buildBaseCard();
        card.add(buildSectionHeader("상담 중 (체크아웃 대기)", "CONFIRMED", TABLE_CHECKINGIN), BorderLayout.NORTH);

        checkingInModel = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        checkingInTable = new JTable(checkingInModel);
        styleTable(checkingInTable, TABLE_CHECKINGIN);

        JScrollPane scroll = new JScrollPane(checkingInTable);
        scroll.setBorder(new LineBorder(UIConstants.BORDER));
        scroll.getViewport().setBackground(UIConstants.WHITE);
        scroll.setPreferredSize(new Dimension(0, 230));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildNoShowCard() {
        JPanel card = buildBaseCard();

        JPanel header = buildSectionHeader("노쇼 대상", "PENDING / CONFIRMED", TABLE_NOSHOW);

        JButton batchBtn = new JButton("일괄 노쇼 처리");
        batchBtn.setFont(UIConstants.f(Font.BOLD, 11));
        batchBtn.setForeground(UIConstants.DANGER);
        batchBtn.setContentAreaFilled(false);
        batchBtn.setBorderPainted(false);
        batchBtn.setFocusPainted(false);
        batchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        batchBtn.addActionListener(e -> doAllNoShow());
        header.add(batchBtn, BorderLayout.EAST);

        card.add(header, BorderLayout.NORTH);

        noShowModel = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        noShowTable = new JTable(noShowModel);
        styleTable(noShowTable, TABLE_NOSHOW);

        JScrollPane scroll = new JScrollPane(noShowTable);
        scroll.setBorder(new LineBorder(UIConstants.BORDER));
        scroll.getViewport().setBackground(UIConstants.WHITE);
        scroll.setPreferredSize(new Dimension(0, 230));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildBaseCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIConstants.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(UIConstants.BORDER),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 330));

        return card;
    }

    private JPanel buildSectionHeader(String titleText, String statusText, int tableType) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);

        JLabel title = new JLabel(titleText);
        title.setFont(UIConstants.f(Font.BOLD, 15));
        title.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel status = new JLabel(statusText);
        status.setFont(UIConstants.f(Font.BOLD, 11));
        status.setForeground(UIConstants.TEXT_MUTED);

        JLabel count = new JLabel("0");
        count.setFont(UIConstants.f(Font.BOLD, 11));
        count.setForeground(Color.WHITE);
        count.setOpaque(false);
        count.setBorder(BorderFactory.createEmptyBorder(2, 7, 2, 7));

        JPanel badge = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(tableType == TABLE_PENDING    ? UIConstants.PENDING_FG
                        : tableType == TABLE_CHECKINGIN ? new Color(14, 116, 144)
                        : tableType == TABLE_NOSHOW     ? UIConstants.DANGER
                        : UIConstants.PRIMARY);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setOpaque(false);
        badge.setLayout(new BorderLayout());
        badge.add(count);

        if (tableType == TABLE_PENDING) {
            pendingCountLbl = count;
        } else if (tableType == TABLE_CONFIRMED) {
            confirmedCountLbl = count;
        } else if (tableType == TABLE_CHECKINGIN) {
            checkingInCountLbl = count;
        } else {
            noShowCountLbl = count;
        }

        left.add(title);
        left.add(status);
        left.add(badge);

        header.add(left, BorderLayout.WEST);

        return header;
    }

    private void styleTable(JTable table, int tableType) {
        table.setFont(UIConstants.f(Font.PLAIN, 12));
        table.setRowHeight(48);
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
        hdr.setPreferredSize(new Dimension(hdr.getPreferredSize().width, 34));

        DefaultTableCellRenderer base = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
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

        table.getColumnModel().getColumn(COL_ACTION).setCellRenderer((t, v, sel, foc, row, col) -> {
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 10));
            wrap.setBackground(UIConstants.WHITE);
            if (tableType == TABLE_PENDING) {
                wrap.add(actionBtn("확정", UIConstants.PRIMARY));
                wrap.add(actionBtn("취소", UIConstants.DANGER));
            } else if (tableType == TABLE_CONFIRMED) {
                wrap.add(actionBtn("체크인", UIConstants.PRIMARY));
                wrap.add(actionBtn("취소", UIConstants.DANGER));
            } else if (tableType == TABLE_CHECKINGIN) {
                wrap.add(actionBtn("체크아웃", new Color(14, 116, 144)));
            } else {
                wrap.add(actionBtn("노쇼 처리", UIConstants.DANGER));
            }
            return wrap;
        });

        table.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col != COL_ACTION) return;

                if (tableType == TABLE_CHECKINGIN) {
                    doCheckOut(row);
                    return;
                }
                if (tableType == TABLE_NOSHOW) {
                    doNoShow(row);
                    return;
                }

                Rectangle cellRect = table.getCellRect(row, COL_ACTION, false);
                boolean leftButton = e.getX() - cellRect.x < cellRect.width / 2;

                if (tableType == TABLE_PENDING) {
                    if (leftButton) doConfirm(row); else doCancel(row, true);
                } else {
                    if (leftButton) doCheckIn(row); else doCancel(row, false);
                }
            }
        });
    }

    private JButton actionBtn(String text, Color color) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 18));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));

                g2.dispose();
                super.paintComponent(g);
            }
        };

        b.setFont(UIConstants.f(Font.BOLD, 10));
        b.setForeground(color);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setMargin(new Insets(0, 0, 0, 0));

        int width = ("체크아웃".equals(text) || "노쇼 처리".equals(text)) ? 86 : 70;

        Dimension size = new Dimension(width, 28);
        b.setPreferredSize(size);
        b.setMinimumSize(size);
        b.setMaximumSize(size);

        return b;
    }

    private void doConfirm(int row) {
        int id = parseId(pendingModel.getValueAt(row, COL_ID).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "예약 #" + id + "을 확정하시겠습니까?",
                "예약 확정",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = adminService.confirmReservation(id);

            if (ok) {
                JOptionPane.showMessageDialog(
                        this,
                        "예약 #" + id + " 확정 완료.",
                        "완료",
                        JOptionPane.INFORMATION_MESSAGE
                );
                refresh();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "예약 확정 실패.",
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

    private void doCheckIn(int row) {
        int id = parseId(confirmedModel.getValueAt(row, COL_ID).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "예약 #" + id + "을 체크인 처리하시겠습니까?\n처리 후 상담 중 목록으로 이동됩니다.",
                "체크인",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = adminService.processCheckIn(id);

            if (ok) {
                JOptionPane.showMessageDialog(
                        this,
                        "예약 #" + id + " 체크인 완료.",
                        "체크인 완료",
                        JOptionPane.INFORMATION_MESSAGE
                );
                refresh();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "체크인 처리 실패.\nCONFIRMED 상태인지 확인해주세요.",
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

    private void doCheckOut(int row) {
        int id = parseId(checkingInModel.getValueAt(row, COL_ID).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "예약 #" + id + "을 체크아웃 처리하시겠습니까?\n처리 후 COMPLETED 상태가 됩니다.",
                "체크아웃",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = adminService.processCheckOut(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "예약 #" + id + " 체크아웃 완료.", "완료", JOptionPane.INFORMATION_MESSAGE);
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "체크아웃 처리 실패.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doNoShow(int row) {
        int id = parseId(noShowModel.getValueAt(row, COL_ID).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "예약 #" + id + "을 노쇼 처리하시겠습니까?\n상태가 CANCELLED로 변경됩니다.",
                "노쇼 처리",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = adminService.processNoShow(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "예약 #" + id + " 노쇼 처리 완료.", "완료", JOptionPane.INFORMATION_MESSAGE);
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "노쇼 처리 실패.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doAllNoShow() {
        int count = noShowModel.getRowCount();
        if (count == 0) {
            JOptionPane.showMessageDialog(this, "처리할 노쇼 대상이 없습니다.", "안내", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "노쇼 대상 " + count + "건을 일괄 처리하시겠습니까?",
                "일괄 노쇼 처리",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int processed = adminService.processAllNoShows();
            JOptionPane.showMessageDialog(this, processed + "건 노쇼 처리 완료.", "완료", JOptionPane.INFORMATION_MESSAGE);
            refresh();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doCancel(int row, boolean fromPendingTable) {
        DefaultTableModel model = fromPendingTable ? pendingModel : confirmedModel;
        int id = parseId(model.getValueAt(row, COL_ID).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "예약 #" + id + "을 취소하시겠습니까?",
                "예약 취소",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = resvService.cancelReservation(id);

            if (ok) {
                JOptionPane.showMessageDialog(
                        this,
                        "예약 #" + id + " 취소 완료.",
                        "완료",
                        JOptionPane.INFORMATION_MESSAGE
                );
                refresh();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "예약 취소 실패.",
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

    public void refresh() {
        pendingModel.setRowCount(0);
        confirmedModel.setRowCount(0);
        checkingInModel.setRowCount(0);
        noShowModel.setRowCount(0);

        try {
            List<ReservationDetail> pending = adminService.getReservationDetailsByStatus("PENDING");
            for (ReservationDetail r : pending) pendingModel.addRow(toRow(r));
            if (pendingCountLbl != null) pendingCountLbl.setText(String.valueOf(pending.size()));

            List<ReservationDetail> confirmed = adminService.getConfirmedNotCheckedIn();
            for (ReservationDetail r : confirmed) confirmedModel.addRow(toRow(r));
            if (confirmedCountLbl != null) confirmedCountLbl.setText(String.valueOf(confirmed.size()));

            List<ReservationDetail> checkingIn = adminService.getCheckedInReservations();
            for (ReservationDetail r : checkingIn) checkingInModel.addRow(toRow(r));
            if (checkingInCountLbl != null) checkingInCountLbl.setText(String.valueOf(checkingIn.size()));

            List<ReservationDetail> noShow = adminService.getNoShowCandidates();
            for (ReservationDetail r : noShow) noShowModel.addRow(toRow(r));
            if (noShowCountLbl != null) noShowCountLbl.setText(String.valueOf(noShow.size()));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "데이터 로딩 오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Object[] toRow(ReservationDetail r) {
        return new Object[]{
                "#" + r.getReservationId(),
                r.getStudentName() != null ? r.getStudentName() : "",
                r.getDepartmentName() != null ? r.getDepartmentName() : "",
                r.getProfessorName() != null ? r.getProfessorName() : "",
                r.getSlotDate() != null ? r.getSlotDate().toString() : "",
                timeRange(r),
                r.getBoothName() != null ? r.getBoothName() : "",
                r.getStatus() != null ? r.getStatus() : "",
                ""
        };
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
}
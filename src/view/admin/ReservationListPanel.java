package DB2026Team02.view.admin;

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
    private static final int COL_PROF   = 3;
    private static final int COL_DATE   = 4;
    private static final int COL_TIME   = 5;
    private static final int COL_BOOTH  = 6;
    private static final int COL_STATUS = 7;
    private static final int COL_ACTION = 8;

    private static final String[] COLS = {
            "ID", "학생명", "예약학과", "교수", "날짜", "시간", "부스", "상태", "작업"
    };

    private static final int[] WIDTHS = {
            55, 80, 115, 80, 95, 105, 120, 95, 150
    };

    private final AdminService adminService = new AdminService();
    private final ReservationService resvService = new ReservationService();

    private DefaultTableModel tableModel;
    private JTable table;
    private List<ReservationDetail> allData = new ArrayList<>();

    private JLabel totalCountLbl;

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
            @Override protected void paintComponent(Graphics g) {
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

        GreenButton exportBtn = new GreenButton("↓ 내보내기", new Color(71, 85, 105), new Color(51, 65, 85));
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
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(tableModel);
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

            wrap.add(actionBtn("취소", UIConstants.DANGER));
            wrap.add(actionBtn("삭제", new Color(127, 29, 29)));

            return wrap;
        });

        table.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row < 0 || col != COL_ACTION) return;

                Rectangle cellRect = table.getCellRect(row, COL_ACTION, false);
                int relativeX = e.getX() - cellRect.x;
                boolean leftButton = relativeX < cellRect.width / 2;

                if (leftButton) {
                    doCancel(row);
                } else {
                    doDelete(row);
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

        Dimension size = new Dimension(58, 26);
        b.setPreferredSize(size);
        b.setMinimumSize(size);
        b.setMaximumSize(size);

        return b;
    }

    private void doCancel(int row) {
        int id = parseId(tableModel.getValueAt(row, COL_ID).toString());
        String status = tableModel.getValueAt(row, COL_STATUS).toString();

        if ("CANCELLED".equals(status)) {
            JOptionPane.showMessageDialog(
                    this,
                    "이미 취소된 예약입니다.",
                    "안내",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        if ("COMPLETED".equals(status)) {
            JOptionPane.showMessageDialog(
                    this,
                    "이미 완료된 예약은 취소할 수 없습니다.",
                    "안내",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

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

    private void doDelete(int row) {
        int id = parseId(tableModel.getValueAt(row, COL_ID).toString());

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "예약 #" + id + "을(를) 완전히 삭제하시겠습니까?\n" +
                        "삭제 후에는 복구할 수 없습니다.",
                "예약 삭제",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = resvService.deleteReservation(id);

            if (ok) {
                JOptionPane.showMessageDialog(
                        this,
                        "예약 #" + id + " 삭제 완료.",
                        "완료",
                        JOptionPane.INFORMATION_MESSAGE
                );
                refresh();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "예약 삭제 실패.",
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

    private void loadAllReservations() {
        tableModel.setRowCount(0);

        for (ReservationDetail r : allData) {
            String status = r.getStatus() != null ? r.getStatus() : "";

            tableModel.addRow(new Object[]{
                    "#" + r.getReservationId(),
                    r.getStudentName() != null ? r.getStudentName() : "",
                    r.getDepartmentName() != null ? r.getDepartmentName() : "",
                    r.getProfessorName() != null ? r.getProfessorName() : "",
                    r.getSlotDate() != null ? r.getSlotDate().toString() : "",
                    timeRange(r),
                    r.getBoothName() != null ? r.getBoothName() : "",
                    status,
                    ""
            });
        }

        if (totalCountLbl != null) {
            totalCountLbl.setText(String.valueOf(allData.size()));
        }
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

            JOptionPane.showMessageDialog(
                    this,
                    "내보내기 완료!",
                    "완료",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
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
            JOptionPane.showMessageDialog(
                    this,
                    "데이터 로딩 오류: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        loadAllReservations();
    }
}
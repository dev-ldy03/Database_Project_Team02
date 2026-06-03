package DB2026Team02.view.student;

import DB2026Team02.model.ConsultationBooth;
import DB2026Team02.model.Professor;
import DB2026Team02.model.TimeSlot;
import DB2026Team02.service.StudentService;
import DB2026Team02.service.ReservationService;
import DB2026Team02.view.MainFrame;
import DB2026Team02.view.common.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class DepartmentDetailPanel extends JPanel {

    private JPanel profListPanel;
    private JPanel boothListPanel;
    private DefaultTableModel slotModel;
    private JTable slotTable;

    private JLabel deptTitleLabel;
    private JLabel metaLabel;
    private JLabel headerTypeBadge;

    private List<Professor> currentProfessors = new ArrayList<>();
    private List<ConsultationBooth> currentBooths = new ArrayList<>();

    public DepartmentDetailPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG);

        add(new NavBar(NavBar.STUDENT), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(UIConstants.BG);

        body.add(buildHeader(), BorderLayout.NORTH);
        body.add(buildContent(), BorderLayout.CENTER);

        return body;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(null) {
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

        header.setPreferredSize(new Dimension(1280, 110));

        deptTitleLabel = new JLabel("학과 상세");
        deptTitleLabel.setFont(UIConstants.f(Font.BOLD, 26));
        deptTitleLabel.setForeground(Color.WHITE);
        deptTitleLabel.setBounds(80, 22, 700, 38);
        header.add(deptTitleLabel);

        metaLabel = new JLabel("");
        metaLabel.setFont(UIConstants.f(Font.PLAIN, 13));
        metaLabel.setForeground(new Color(255, 255, 255, 210));
        metaLabel.setBounds(80, 65, 900, 22);
        header.add(metaLabel);

        headerTypeBadge = new JLabel("OFFLINE", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(new Color(255, 255, 255, 210));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));

                g2.dispose();
                super.paintComponent(g);
            }
        };

        headerTypeBadge.setOpaque(false);
        headerTypeBadge.setFont(UIConstants.f(Font.BOLD, 12));
        headerTypeBadge.setForeground(UIConstants.TEXT_SECONDARY);
        headerTypeBadge.setBounds(1140, 42, 90, 26);
        header.add(headerTypeBadge);

        return header;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(20, 0));
        content.setBackground(UIConstants.BG);
        content.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(300, 0));

        profListPanel = new JPanel();
        profListPanel.setLayout(new BoxLayout(profListPanel, BoxLayout.Y_AXIS));
        profListPanel.setOpaque(false);

        boothListPanel = new JPanel();
        boothListPanel.setLayout(new BoxLayout(boothListPanel, BoxLayout.Y_AXIS));
        boothListPanel.setOpaque(false);

        left.add(buildSection("교수진", profListPanel));
        left.add(Box.createVerticalStrut(16));
        left.add(buildSection("상담 부스", boothListPanel));

        content.add(left, BorderLayout.WEST);
        content.add(buildSlotPanel(), BorderLayout.CENTER);

        return content;
    }

    private JPanel buildSection(String title, JPanel inner) {
        JPanel card = new JPanel(new BorderLayout()) {
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
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel lbl = new JLabel(title);
        lbl.setFont(UIConstants.f(Font.BOLD, 16));
        lbl.setForeground(UIConstants.TEXT_PRIMARY);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        card.add(lbl, BorderLayout.NORTH);
        card.add(inner, BorderLayout.CENTER);

        return card;
    }

    private JPanel buildSlotPanel() {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
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
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel("예약 가능 시간대");
        title.setFont(UIConstants.f(Font.BOLD, 16));
        title.setForeground(UIConstants.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        card.add(title, BorderLayout.NORTH);

        String[] cols = {"날짜", "시간", "부스", "정원", "상태", "예약"};

        slotModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        slotTable = new JTable(slotModel);
        styleSlotTable(slotTable);

        JScrollPane scroll = new JScrollPane(slotTable);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UIConstants.WHITE);

        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private void styleSlotTable(JTable t) {
        t.setFont(UIConstants.f(Font.PLAIN, 12));
        t.setRowHeight(46);
        t.setShowVerticalLines(false);
        t.setGridColor(UIConstants.BORDER);
        t.setBackground(UIConstants.WHITE);

        t.setSelectionBackground(UIConstants.WHITE);
        t.setSelectionForeground(UIConstants.TEXT_PRIMARY);
        t.setFocusable(false);
        t.setRowSelectionAllowed(false);
        t.setCellSelectionEnabled(false);

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
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table,
                        value,
                        false,
                        false,
                        row,
                        column
                );

                label.setFont(UIConstants.f(Font.PLAIN, 13));
                label.setForeground(UIConstants.TEXT_PRIMARY);
                label.setBackground(UIConstants.WHITE);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

                return label;
            }
        };

        for (int i = 0; i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(defaultRenderer);
        }

        t.getColumnModel().getColumn(4).setCellRenderer((tbl, val, sel, foc, r, c) -> {
            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setBackground(UIConstants.WHITE);

            StatusBadge badge = new StatusBadge(val == null ? "" : val.toString());
            badge.setOpaque(false);

            wrapper.add(badge);
            return wrapper;
        });

        t.getColumnModel().getColumn(5).setCellRenderer((tbl, val, sel, foc, row, col) -> {
            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setBackground(UIConstants.WHITE);

            if ("예약 가능".equals(slotModel.getValueAt(row, 4))) {
                GreenButton btn = new GreenButton("예약");
                btn.setFont(UIConstants.f(Font.BOLD, 12));
                btn.setPreferredSize(new Dimension(66, 30));
                wrapper.add(btn);
            }

            return wrapper;
        });

        t.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = t.rowAtPoint(e.getPoint());
                int col = t.columnAtPoint(e.getPoint());

                if (row < 0 || col != 5) return;
                if (!"예약 가능".equals(slotModel.getValueAt(row, 4))) return;

                Object slotIdObj = t.getClientProperty("slotId_" + row);
                Object boothIdObj = t.getClientProperty("boothId_" + row);
                Object boothNameObj = t.getClientProperty("boothName_" + row);

                if (slotIdObj == null || boothIdObj == null || boothNameObj == null) return;

                Professor selectedProfessor = chooseProfessorForReservation();
                if (selectedProfessor == null) return;

                String dateText = slotModel.getValueAt(row, 0).toString();
                String timeText = slotModel.getValueAt(row, 1).toString();

                MainFrame.setSelectedSlot(
                        (int) slotIdObj,
                        selectedProfessor.getProfessorId(),
                        (int) boothIdObj,
                        boothNameObj.toString(),
                        dateText,
                        timeText
                );

                MainFrame.navigate(MainFrame.MAKE_RESERVATION);
            }
        });

        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        int[] widths = {95, 120, 210, 55, 90, 70};

        for (int i = 0; i < widths.length; i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        t.getColumnModel().getColumn(5).setMinWidth(64);
        t.getColumnModel().getColumn(5).setMaxWidth(74);
    }

    private Professor chooseProfessorForReservation() {
        if (currentProfessors == null || currentProfessors.isEmpty()) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(), "선택 가능한 교수가 없습니다.");
            return null;
        }

        String[] options = new String[currentProfessors.size()];

        for (int i = 0; i < currentProfessors.size(); i++) {
            Professor p = currentProfessors.get(i);
            options[i] = p.getProfessorName();
        }

        String selected = (String) JOptionPane.showInputDialog(MainFrame.getInstance(),
                "상담할 교수를 선택해주세요.",
                "교수 선택",
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );

        if (selected == null) return null;

        for (Professor p : currentProfessors) {
            if (selected.equals(p.getProfessorName())) {
                return p;
            }
        }

        return null;
    }

    private void loadProfessors(int deptId, StudentService svc) {
        profListPanel.removeAll();
        currentProfessors = new ArrayList<>();

        try {
            currentProfessors = svc.getProfessorsByDepartment(deptId);

            if (currentProfessors == null || currentProfessors.isEmpty()) {
                profListPanel.add(emptyLabel("등록된 교수가 없습니다."));
            } else {
                for (Professor p : currentProfessors) {
                    profListPanel.add(buildProfRow(p));
                    profListPanel.add(Box.createVerticalStrut(8));
                }
            }
        } catch (SQLException ex) {
            profListPanel.add(emptyLabel("오류: " + ex.getMessage()));
        }

        profListPanel.revalidate();
        profListPanel.repaint();
    }

    private JPanel buildProfRow(Professor p) {
        JPanel row = new JPanel(new BorderLayout(16, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 54));

        JLabel initial = new JLabel(getInitial(p.getProfessorName()), SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(UIConstants.PRIMARY_LIGHT_BG);
                g2.fill(new Ellipse2D.Float(0, 0, 34, 34));

                g2.setColor(UIConstants.PRIMARY);
                g2.setFont(UIConstants.f(Font.BOLD, 13));

                FontMetrics fm = g2.getFontMetrics();
                String text = getText();
                int x = (34 - fm.stringWidth(text)) / 2;
                int y = (34 + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(text, x, y);

                g2.dispose();
            }
        };

        initial.setPreferredSize(new Dimension(36, 36));
        initial.setOpaque(false);

        JLabel nameLabel = new JLabel(p.getProfessorName());
        nameLabel.setFont(UIConstants.f(Font.BOLD, 13));
        nameLabel.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel emailLabel = new JLabel(p.getEmail() != null ? p.getEmail() : "");
        emailLabel.setFont(UIConstants.f(Font.PLAIN, 12));
        emailLabel.setForeground(UIConstants.PRIMARY);

        row.add(initial, BorderLayout.WEST);
        row.add(nameLabel, BorderLayout.CENTER);
        row.add(emailLabel, BorderLayout.EAST);

        return row;
    }

    private String getInitial(String name) {
        if (name == null || name.isBlank()) return "";
        return String.valueOf(name.charAt(0));
    }

    private void loadBooths(int deptId, StudentService svc) {
        boothListPanel.removeAll();
        currentBooths = new ArrayList<>();

        try {
            currentBooths = svc.getBoothsByDepartment(deptId);

            if (currentBooths == null || currentBooths.isEmpty()) {
                boothListPanel.add(emptyLabel("등록된 상담 부스가 없습니다."));
            } else {
                for (ConsultationBooth b : currentBooths) {
                    boothListPanel.add(buildBoothRow(b));
                    boothListPanel.add(Box.createVerticalStrut(8));
                }
            }
        } catch (SQLException ex) {
            boothListPanel.add(emptyLabel("오류: " + ex.getMessage()));
        }

        boothListPanel.revalidate();
        boothListPanel.repaint();
    }

    private JPanel buildBoothRow(ConsultationBooth b) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);

        row.setBorder(new CompoundBorder(
                new LineBorder(UIConstants.BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 74));

        JLabel name = new JLabel(b.getBoothName());
        name.setFont(UIConstants.f(Font.BOLD, 13));
        name.setForeground(UIConstants.TEXT_PRIMARY);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        bottom.setOpaque(false);
        bottom.setAlignmentX(Component.LEFT_ALIGNMENT);

        StatusBadge typeBadge = new StatusBadge(b.getBoothType());

        JLabel cap = new JLabel("정원 " + b.getCapacity() + "명");
        cap.setFont(UIConstants.f(Font.PLAIN, 12));
        cap.setForeground(UIConstants.TEXT_MUTED);

        bottom.add(typeBadge);
        bottom.add(cap);

        row.add(name);
        row.add(Box.createVerticalStrut(8));
        row.add(bottom);

        return row;
    }

    private void loadSlotsFromBackend(StudentService svc) {
        slotModel.setRowCount(0);

        if (currentBooths == null || currentBooths.isEmpty()) {
            return;
        }

        try {
            Map<Integer, ConsultationBooth> boothMap = new HashMap<>();

            for (ConsultationBooth b : currentBooths) {
                boothMap.put(b.getBoothId(), b);
            }

            List<TimeSlot> allSlots = new ArrayList<>();

            for (ConsultationBooth booth : currentBooths) {
                List<TimeSlot> slots = svc.getAllSlotsByBooth(booth.getBoothId());
                if (slots != null) allSlots.addAll(slots);
            }

            allSlots.sort((a, b) -> {
                int dateCompare = String.valueOf(a.getSlotDate()).compareTo(String.valueOf(b.getSlotDate()));
                if (dateCompare != 0) return dateCompare;
                return String.valueOf(a.getStartTime()).compareTo(String.valueOf(b.getStartTime()));
            });

            ReservationService reservationService = new ReservationService();

            for (TimeSlot s : allSlots) {
                ConsultationBooth booth = boothMap.get(s.getBoothId());

                String boothName = booth != null ? booth.getBoothName() : "부스 " + s.getBoothId();
                String date = s.getSlotDate() != null ? s.getSlotDate().toString() : "";
                String time = formatTime(s);
                String capacity = s.getMaxReservations() + "명";

                boolean available = reservationService.isSlotAvailable(s.getSlotId());
                String status = available ? "예약 가능" : "마감";

                slotModel.addRow(new Object[]{
                        date,
                        time,
                        boothName,
                        capacity,
                        status,
                        ""
                });

                int row = slotModel.getRowCount() - 1;

                slotTable.putClientProperty("slotId_" + row, s.getSlotId());
                slotTable.putClientProperty("boothId_" + row, s.getBoothId());
                slotTable.putClientProperty("boothName_" + row, boothName);
            }

        } catch (SQLException ex) {
            slotModel.addRow(new Object[]{
                    "오류",
                    ex.getMessage(),
                    "",
                    "",
                    "",
                    ""
            });
        }
    }

    private String formatTime(TimeSlot s) {
        String start = s.getStartTime() == null ? "" : s.getStartTime().toString();
        String end = s.getEndTime() == null ? "" : s.getEndTime().toString();

        if (start.length() >= 5) start = start.substring(0, 5);
        if (end.length() >= 5) end = end.substring(0, 5);

        return start + " - " + end;
    }

    private JLabel emptyLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIConstants.f(Font.PLAIN, 13));
        lbl.setForeground(UIConstants.TEXT_MUTED);
        lbl.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        return lbl;
    }

    private void updateHeader(String deptName) {
        int professorCount = currentProfessors == null ? 0 : currentProfessors.size();
        int boothCount = currentBooths == null ? 0 : currentBooths.size();

        String type = getConsultationTypeFromBooths(currentBooths);

        if (deptTitleLabel != null) {
            deptTitleLabel.setText(
                    deptName == null || deptName.isBlank() ? "학과 상세" : deptName
            );
        }

        if (metaLabel != null) {
            metaLabel.setText(
                    type + " 상담"
                            + "  ·  교수 " + professorCount + "명"
                            + "  ·  부스 " + boothCount + "개"
            );
        }

        if (headerTypeBadge != null) {
            headerTypeBadge.setText(type);
        }
    }

    private String getConsultationTypeFromBooths(List<ConsultationBooth> booths) {
        if (booths == null || booths.isEmpty()) {
            return "OFFLINE";
        }

        boolean hasOnline = false;
        boolean hasOffline = false;
        boolean hasHybrid = false;

        for (ConsultationBooth booth : booths) {
            if (booth == null || booth.getBoothType() == null) continue;

            String type = booth.getBoothType().trim().toUpperCase();

            if ("ONLINE".equals(type)) {
                hasOnline = true;
            } else if ("OFFLINE".equals(type)) {
                hasOffline = true;
            } else if ("HYBRID".equals(type)) {
                hasHybrid = true;
            }
        }

        if (hasHybrid) return "HYBRID";
        if (hasOnline && hasOffline) return "HYBRID";
        if (hasOnline) return "ONLINE";

        return "OFFLINE";
    }

    public void refresh() {
        int deptId = MainFrame.getSelectedDeptId();
        String deptName = MainFrame.getSelectedDeptName();

        StudentService svc = new StudentService();

        loadProfessors(deptId, svc);
        loadBooths(deptId, svc);
        updateHeader(deptName);
        loadSlotsFromBackend(svc);
    }
}
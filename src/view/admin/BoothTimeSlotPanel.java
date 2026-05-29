package DB2026Team02.view.admin;

import DB2026Team02.model.ConsultationBooth;
import DB2026Team02.model.Department;
import DB2026Team02.model.ReservationDetail;
import DB2026Team02.model.TimeSlot;
import DB2026Team02.service.AdminService;
import DB2026Team02.view.MainFrame;
import DB2026Team02.view.common.GreenButton;
import DB2026Team02.view.common.UIConstants;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

public class BoothTimeSlotPanel extends JPanel {

    private static final String TAB_BOOTH = "BOOTH";
    private static final String TAB_SLOT  = "SLOT";

    private final AdminService adminService = new AdminService();

    private String activeTab = TAB_BOOTH;
    private JPanel tabBar;
    private JLabel addBtn;

    private final CardLayout contentLayout = new CardLayout();
    private final JPanel contentCard = new JPanel(contentLayout);

    private static final String[] BOOTH_COLS = {
            "ID", "부스명", "소속 학과", "유형", "정원", "", ""
    };

    private static final int[] BOOTH_WIDTHS = {
            55, 170, 180, 110, 90, 70, 70
    };

    private static final String[] BOOTH_TYPES = {
            "OFFLINE", "ONLINE", "HYBRID"
    };

    private DefaultTableModel boothModel;
    private JTable boothTable;
    private List<ConsultationBooth> allBooths;
    private List<Department> allDepts;
    private List<ReservationDetail> allReservations;
    private JTextField boothSearch;

    private static final String[] SLOT_COLS = {
            "ID", "부스", "날짜", "시작 시간", "종료 시간", "예약 현황", "", ""
    };

    private static final int[] SLOT_WIDTHS = {
            55, 180, 120, 100, 100, 90, 70, 70
    };

    private DefaultTableModel slotModel;
    private JTable slotTable;
    private List<TimeSlot> allSlots;
    private List<String[]> allBoothStats;
    private JTextField slotSearch;

    public BoothTimeSlotPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG);

        add(new AdminSideBar(MainFrame.ADMIN_BOOTH), BorderLayout.WEST);
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
                BorderFactory.createEmptyBorder(16, 32, 0, 32)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(BorderFactory.createEmptyBorder(4, 0, 12, 0));

        JLabel title = new JLabel("부스 / 시간대 관리");
        title.setFont(UIConstants.f(Font.BOLD, 22));
        title.setForeground(UIConstants.TEXT_PRIMARY);

        addBtn = buildAddBtn();

        top.add(title, BorderLayout.WEST);
        top.add(addBtn, BorderLayout.EAST);

        tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabBar.setOpaque(false);

        p.add(top, BorderLayout.NORTH);
        p.add(tabBar, BorderLayout.SOUTH);

        buildTabBar();

        return p;
    }

    private JLabel buildAddBtn() {
        JLabel btn = new JLabel(addButtonText()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(UIConstants.PRIMARY);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(UIConstants.f(Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (TAB_BOOTH.equals(activeTab)) {
                    showBoothForm(null);
                } else {
                    showSlotForm(null);
                }
            }
        });

        return btn;
    }

    private String addButtonText() {
        return TAB_BOOTH.equals(activeTab) ? "+ 부스 추가" : "+ 시간대 추가";
    }

    private void buildTabBar() {
        if (tabBar == null) return;

        tabBar.removeAll();
        tabBar.add(buildTab("부스 관리", TAB_BOOTH));
        tabBar.add(buildTab("시간대 관리", TAB_SLOT));
        tabBar.revalidate();
        tabBar.repaint();

        if (addBtn != null) {
            addBtn.setText(addButtonText());
            addBtn.revalidate();
            addBtn.repaint();
        }
    }

    private JPanel buildTab(String label, String tabId) {
        boolean active = tabId.equals(activeTab);

        JPanel tab = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (active) {
                    g.setColor(UIConstants.PRIMARY);
                    g.fillRect(0, getHeight() - 3, getWidth(), 3);
                }
            }
        };

        tab.setBackground(UIConstants.WHITE);
        tab.setPreferredSize(new Dimension(120, 44));
        tab.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.f(active ? Font.BOLD : Font.PLAIN, 14));
        lbl.setForeground(active ? UIConstants.PRIMARY : UIConstants.TEXT_SECONDARY);
        lbl.setBounds(16, 12, 100, 20);
        tab.add(lbl);

        if (!active) {
            tab.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) {
                    activeTab = tabId;
                    buildTabBar();
                    contentLayout.show(contentCard, tabId);

                    if (TAB_BOOTH.equals(activeTab)) {
                        refreshBooths();
                    } else {
                        refreshSlots();
                    }
                }
            });
        }

        return tab;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(UIConstants.BG);
        body.setBorder(BorderFactory.createEmptyBorder(20, 32, 20, 32));

        contentCard.setOpaque(false);
        contentCard.add(buildBoothCard(), TAB_BOOTH);
        contentCard.add(buildSlotCard(), TAB_SLOT);

        body.add(contentCard, BorderLayout.CENTER);
        return body;
    }

    private JPanel buildBoothCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIConstants.BG);

        boothSearch = searchField(() -> applyBoothFilter());
        JPanel searchWrap = searchWrap(boothSearch);

        boothModel = new DefaultTableModel(BOOTH_COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        boothTable = new JTable(boothModel);
        styleBoothTable();

        JScrollPane scroll = new JScrollPane(boothTable);
        scroll.setBorder(new LineBorder(UIConstants.BORDER));
        scroll.getViewport().setBackground(UIConstants.WHITE);

        card.add(searchWrap, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private void styleBoothTable() {
        JTable t = boothTable;

        t.setFont(UIConstants.f(Font.PLAIN, 13));
        t.setRowHeight(46);
        t.setShowVerticalLines(false);
        t.setGridColor(UIConstants.BORDER);
        t.setBackground(UIConstants.WHITE);
        t.setSelectionBackground(new Color(248, 250, 252));
        t.setFocusable(false);

        JTableHeader hdr = t.getTableHeader();
        hdr.setFont(UIConstants.f(Font.BOLD, 12));
        hdr.setBackground(UIConstants.SURFACE);
        hdr.setForeground(UIConstants.TEXT_SECONDARY);
        hdr.setReorderingAllowed(false);
        hdr.setPreferredSize(new Dimension(hdr.getPreferredSize().width, 34));

        DefaultTableCellRenderer base = baseRenderer();

        for (int i = 0; i < BOOTH_COLS.length; i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(BOOTH_WIDTHS[i]);
            t.getColumnModel().getColumn(i).setCellRenderer(base);
        }

        t.getColumnModel().getColumn(3).setCellRenderer((tbl, v, sel, foc, row, col) -> {
            String type = v == null ? "" : v.toString();

            Color bg = "ONLINE".equals(type) ? UIConstants.CONFIRMED_BG
                    : "HYBRID".equals(type) ? UIConstants.PENDING_BG
                    : UIConstants.SURFACE;

            Color fg = "ONLINE".equals(type) ? UIConstants.CONFIRMED_FG
                    : "HYBRID".equals(type) ? UIConstants.PENDING_FG
                    : UIConstants.TEXT_SECONDARY;

            JPanel wrap = new JPanel(new GridBagLayout());
            wrap.setBackground(UIConstants.WHITE);

            JLabel badge = new JLabel(type, SwingConstants.CENTER) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setColor(bg);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));

                    g2.dispose();
                    super.paintComponent(g);
                }
            };

            badge.setFont(UIConstants.f(Font.BOLD, 11));
            badge.setForeground(fg);
            badge.setOpaque(false);
            badge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

            Dimension badgeSize = new Dimension(68, 24);
            badge.setPreferredSize(badgeSize);
            badge.setMinimumSize(badgeSize);
            badge.setMaximumSize(badgeSize);

            wrap.add(badge);
            return wrap;
        });

        t.getColumnModel().getColumn(4).setCellRenderer((tbl, v, sel, foc, row, col) -> {
            JLabel l = new JLabel(v == null ? "" : v.toString(), SwingConstants.CENTER);
            l.setFont(UIConstants.f(Font.BOLD, 13));
            l.setForeground(UIConstants.TEXT_PRIMARY);
            l.setBackground(UIConstants.WHITE);
            l.setOpaque(true);
            l.setToolTipText(v == null ? "" : v.toString());
            return l;
        });

        t.getColumnModel().getColumn(5).setCellRenderer(linkRenderer("수정", UIConstants.CONFIRMED_FG));
        t.getColumnModel().getColumn(6).setCellRenderer(linkRenderer("삭제", UIConstants.DANGER));

        t.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                int row = t.rowAtPoint(e.getPoint());
                int col = t.columnAtPoint(e.getPoint());

                if (row < 0) return;

                if (col == 5) {
                    int id = (int) boothModel.getValueAt(row, 0);
                    ConsultationBooth booth = allBooths.stream()
                            .filter(b -> b.getBoothId() == id)
                            .findFirst()
                            .orElse(null);

                    if (booth != null) showBoothForm(booth);

                } else if (col == 6) {
                    deleteBoothRow(row);
                }
            }
        });
    }

    private void showBoothForm(ConsultationBooth booth) {
        boolean isEdit = booth != null;

        JDialog dialog = dialog(isEdit ? "부스 수정" : "+ 부스 추가", 440, 440);

        JPanel panel = formPanel();

        panel.add(formTitle(isEdit ? "부스 수정" : "+ 부스 추가"));
        panel.add(vgap(20));

        JTextField nameField = inputField(booth != null ? booth.getBoothName() : "");
        JComboBox<String> deptCombo = deptCombo(booth != null ? booth.getDepartmentId() : -1);
        JComboBox<String> typeCombo = new JComboBox<>(BOOTH_TYPES);
        JTextField capField = inputField(booth != null ? String.valueOf(booth.getCapacity()) : "");

        typeCombo.setFont(UIConstants.f(Font.PLAIN, 13));
        typeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        if (booth != null && booth.getBoothType() != null) {
            typeCombo.setSelectedItem(booth.getBoothType());
        }

        panel.add(labeledInput("부스명 *", nameField));
        panel.add(vgap(12));
        panel.add(labeledCombo("소속 학과 *", deptCombo));
        panel.add(vgap(12));
        panel.add(labeledCombo("유형 *", typeCombo));
        panel.add(vgap(12));
        panel.add(labeledInput("정원", capField));
        panel.add(vgap(22));

        GreenButton saveBtn = saveBtn();

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                warn(dialog, "부스명은 필수입니다.");
                return;
            }

            if (deptCombo.getSelectedIndex() == 0) {
                warn(dialog, "소속 학과를 선택하세요.");
                return;
            }

            int deptId = allDepts.get(deptCombo.getSelectedIndex() - 1).getDepartmentId();
            String type = (String) typeCombo.getSelectedItem();

            int capacity = 0;
            try {
                capacity = Integer.parseInt(capField.getText().trim());
            } catch (NumberFormatException ignored) {}

            try {
                if (isEdit) {
                    adminService.updateBooth(booth.getBoothId(), deptId, name, type, capacity);
                    JOptionPane.showMessageDialog(dialog, "부스 정보가 수정되었습니다.", "완료", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    adminService.addBooth(deptId, name, type, capacity);
                    JOptionPane.showMessageDialog(dialog, "부스가 추가되었습니다.", "완료", JOptionPane.INFORMATION_MESSAGE);
                }

                dialog.dispose();
                refreshBooths();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(saveBtn);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void deleteBoothRow(int row) {
        int id = (int) boothModel.getValueAt(row, 0);
        String name = boothModel.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "부스 [" + name + "]을(를) 삭제하시겠습니까?\n관련 시간대, 예약, 교수 데이터도 함께 삭제됩니다.",
                "부스 삭제",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        if (!checkAdminPassword()) return;

        try {
            boolean ok = adminService.deleteBoothCascade(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "부스가 삭제되었습니다.", "완료", JOptionPane.INFORMATION_MESSAGE);
                refreshBooths();
            } else {
                JOptionPane.showMessageDialog(this, "삭제 실패.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean checkAdminPassword() {
        JPasswordField pwField = new JPasswordField(16);
        pwField.setFont(UIConstants.f(Font.PLAIN, 14));
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.add(new JLabel("관리자 비밀번호를 입력하세요:"), BorderLayout.NORTH);
        panel.add(pwField, BorderLayout.CENTER);
        int result = JOptionPane.showConfirmDialog(
                this, panel, "관리자 인증",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) return false;
        if ("ewha1886".equals(new String(pwField.getPassword()))) return true;
        JOptionPane.showMessageDialog(this, "비밀번호가 올바르지 않습니다.", "인증 실패", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    private void applyBoothFilter() {
        if (allBooths == null) return;

        String q = boothSearch.getText().trim().toLowerCase();
        boothModel.setRowCount(0);

        for (ConsultationBooth b : allBooths) {
            String deptName = deptName(b.getDepartmentId());
            String boothType = b.getBoothType() != null ? b.getBoothType() : "";

            boolean match = q.isEmpty()
                    || b.getBoothName().toLowerCase().contains(q)
                    || deptName.toLowerCase().contains(q)
                    || boothType.toLowerCase().contains(q);

            if (match) {
                String capacityText = String.valueOf(b.getCapacity());

                boothModel.addRow(new Object[]{
                        b.getBoothId(),
                        b.getBoothName(),
                        deptName,
                        boothType,
                        capacityText,
                        "",
                        ""
                });
            }
        }
    }

    private int reservationCountByBoothName(String boothName) {
        if (allReservations == null || boothName == null) return 0;

        int count = 0;

        for (ReservationDetail r : allReservations) {
            String rBoothName = r.getBoothName();
            String status = r.getStatus() != null ? r.getStatus() : "";

            if (rBoothName == null) continue;

            if ("CANCELLED".equals(status)) continue;

            if (boothName.equals(rBoothName)) {
                count++;
            }
        }

        return count;
    }

    private void refreshBooths() {
        try {
            allDepts = adminService.getAllDepartments();
            allBooths = adminService.getAllBooths();
            allReservations = adminService.getAllReservationDetails();

            applyBoothFilter();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "데이터 로딩 오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }


    private JPanel buildSlotCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(UIConstants.BG);

        slotSearch = searchField(() -> applySlotFilter());
        JPanel searchWrap = searchWrap(slotSearch);

        slotModel = new DefaultTableModel(SLOT_COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        slotTable = new JTable(slotModel);
        styleSlotTable();

        JScrollPane scroll = new JScrollPane(slotTable);
        scroll.setBorder(new LineBorder(UIConstants.BORDER));
        scroll.getViewport().setBackground(UIConstants.WHITE);

        card.add(searchWrap, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private void styleSlotTable() {
        JTable t = slotTable;

        t.setFont(UIConstants.f(Font.PLAIN, 13));
        t.setRowHeight(46);
        t.setShowVerticalLines(false);
        t.setGridColor(UIConstants.BORDER);
        t.setBackground(UIConstants.WHITE);
        t.setSelectionBackground(new Color(248, 250, 252));
        t.setFocusable(false);

        JTableHeader hdr = t.getTableHeader();
        hdr.setFont(UIConstants.f(Font.BOLD, 12));
        hdr.setBackground(UIConstants.SURFACE);
        hdr.setForeground(UIConstants.TEXT_SECONDARY);
        hdr.setReorderingAllowed(false);
        hdr.setPreferredSize(new Dimension(hdr.getPreferredSize().width, 34));

        DefaultTableCellRenderer base = baseRenderer();

        for (int i = 0; i < SLOT_COLS.length; i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(SLOT_WIDTHS[i]);
            t.getColumnModel().getColumn(i).setCellRenderer(base);
        }

        t.getColumnModel().getColumn(6).setCellRenderer(linkRenderer("수정", UIConstants.CONFIRMED_FG));
        t.getColumnModel().getColumn(7).setCellRenderer(linkRenderer("삭제", UIConstants.DANGER));

        t.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                int row = t.rowAtPoint(e.getPoint());
                int col = t.columnAtPoint(e.getPoint());

                if (row < 0) return;

                if (col == 6) {
                    int id = (int) slotModel.getValueAt(row, 0);
                    TimeSlot slot = allSlots.stream()
                            .filter(s -> s.getSlotId() == id)
                            .findFirst()
                            .orElse(null);

                    if (slot != null) showSlotForm(slot);

                } else if (col == 7) {
                    deleteSlotRow(row);
                }
            }
        });
    }

    private void showSlotForm(TimeSlot slot) {
        boolean isEdit = slot != null;

        JDialog dialog = dialog(isEdit ? "시간대 수정" : "+ 시간대 추가", 440, 530);

        JPanel panel = formPanel();

        panel.add(formTitle(isEdit ? "시간대 수정" : "+ 시간대 추가"));
        panel.add(vgap(20));

        JComboBox<String> boothCombo = boothCombo(slot != null ? slot.getBoothId() : -1);

        JTextField dateField = inputField(
                slot != null && slot.getSlotDate() != null
                        ? slot.getSlotDate().toString()
                        : ""
        );

        JTextField startField = inputField(
                slot != null && slot.getStartTime() != null
                        ? slot.getStartTime().toString().substring(0, 5)
                        : ""
        );

        JTextField endField = inputField(
                slot != null && slot.getEndTime() != null
                        ? slot.getEndTime().toString().substring(0, 5)
                        : ""
        );

        JTextField maxField = inputField(
                slot != null ? String.valueOf(slot.getMaxReservations()) : "1"
        );

        dateField.setToolTipText("YYYY-MM-DD");
        startField.setToolTipText("HH:MM");
        endField.setToolTipText("HH:MM");

        JLabel capHint = new JLabel(" ");
        capHint.setFont(UIConstants.f(Font.PLAIN, 11));
        capHint.setForeground(UIConstants.TEXT_SECONDARY);
        capHint.setAlignmentX(Component.LEFT_ALIGNMENT);

        Runnable updateCapHint = () -> {
            int idx = boothCombo.getSelectedIndex();
            if (idx > 0 && allBooths != null && idx - 1 < allBooths.size()) {
                int cap = allBooths.get(idx - 1).getCapacity();
                capHint.setText("부스 정원: " + cap + "명  (최대 예약 수는 정원 이하여야 합니다)");
            } else {
                capHint.setText(" ");
            }
        };

        boothCombo.addActionListener(e -> updateCapHint.run());
        updateCapHint.run();

        panel.add(labeledCombo("부스 *", boothCombo));
        panel.add(vgap(4));
        panel.add(capHint);
        panel.add(vgap(8));
        panel.add(labeledInput("날짜 *", dateField));
        panel.add(vgap(12));
        panel.add(labeledInput("시작 시간 *", startField));
        panel.add(vgap(12));
        panel.add(labeledInput("종료 시간 *", endField));
        panel.add(vgap(12));
        panel.add(labeledInput("최대 예약 수", maxField));
        panel.add(vgap(22));

        GreenButton saveBtn = saveBtn();

        saveBtn.addActionListener(e -> {
            if (boothCombo.getSelectedIndex() == 0) {
                warn(dialog, "부스를 선택하세요.");
                return;
            }

            String dateStr = dateField.getText().trim();
            String startStr = startField.getText().trim();
            String endStr = endField.getText().trim();

            if (dateStr.isEmpty() || startStr.isEmpty() || endStr.isEmpty()) {
                warn(dialog, "날짜, 시작 시간, 종료 시간은 필수입니다.");
                return;
            }

            int maxReservations = 1;
            try {
                maxReservations = Integer.parseInt(maxField.getText().trim());
            } catch (NumberFormatException ignored) {}

            int selectedIdx = boothCombo.getSelectedIndex() - 1;
            int boothCapacity = allBooths.get(selectedIdx).getCapacity();
            if (maxReservations > boothCapacity) {
                warn(dialog, "최대 예약 수(" + maxReservations + ")가 부스 정원(" + boothCapacity + "명)을 초과합니다.");
                return;
            }

            try {
                Date date = Date.valueOf(dateStr);
                Time startTime = Time.valueOf(startStr.length() == 5 ? startStr + ":00" : startStr);
                Time endTime = Time.valueOf(endStr.length() == 5 ? endStr + ":00" : endStr);

                int boothId = allBooths.get(selectedIdx).getBoothId();

                if (isEdit) {
                    boolean ok = adminService.updateTimeSlot(slot.getSlotId(), boothId, date, startTime, endTime, maxReservations);
                    if (!ok) { warn(dialog, "시간대 수정에 실패했습니다."); return; }
                    JOptionPane.showMessageDialog(dialog, "시간대가 수정되었습니다.", "완료", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    int newId = adminService.addTimeSlot(boothId, date, startTime, endTime, maxReservations);
                    if (newId == -1) { warn(dialog, "시간대 추가에 실패했습니다."); return; }
                    JOptionPane.showMessageDialog(dialog, "시간대가 추가되었습니다.", "완료", JOptionPane.INFORMATION_MESSAGE);
                }

                dialog.dispose();
                refreshSlots();

            } catch (IllegalArgumentException iae) {
                String msg = iae.getMessage() != null ? iae.getMessage()
                        : "날짜/시간 형식이 올바르지 않습니다.\n날짜: YYYY-MM-DD, 시간: HH:MM";
                warn(dialog, msg);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(saveBtn);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void deleteSlotRow(int row) {
        int id = (int) slotModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "시간대 #" + id + "를 삭제하시겠습니까?",
                "시간대 삭제",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = adminService.deleteTimeSlot(id);

            if (ok) {
                JOptionPane.showMessageDialog(this, "시간대가 삭제되었습니다.", "완료", JOptionPane.INFORMATION_MESSAGE);
                refreshSlots();
            } else {
                JOptionPane.showMessageDialog(this, "삭제 실패.", "오류", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "";

            if (msg.contains("foreign key constraint fails")
                    || msg.contains("slot_id")
                    || msg.contains("fk_reservation_slot")) {

                JOptionPane.showMessageDialog(
                        this,
                        "해당 시간대에 연결된 예약 데이터가 있어 삭제할 수 없습니다.\n" +
                                "먼저 관련 예약 정보를 삭제하거나 다른 시간대로 이동해주세요.",
                        "삭제 불가",
                        JOptionPane.WARNING_MESSAGE
                );

            } else {
                JOptionPane.showMessageDialog(this, "오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void applySlotFilter() {
        if (allSlots == null) return;

        String q = slotSearch.getText().trim().toLowerCase();
        slotModel.setRowCount(0);

        for (TimeSlot s : allSlots) {
            String boothName = boothName(s.getBoothId());
            String date = s.getSlotDate() != null ? s.getSlotDate().toString() : "";

            boolean match = q.isEmpty()
                    || boothName.toLowerCase().contains(q)
                    || date.contains(q);

            if (match) {
                int current = findCurrentReservations(boothName, date, s.getStartTime());
                slotModel.addRow(new Object[]{
                        s.getSlotId(),
                        boothName,
                        date,
                        s.getStartTime() != null ? s.getStartTime().toString().substring(0, 5) : "",
                        s.getEndTime() != null ? s.getEndTime().toString().substring(0, 5) : "",
                        current + "/" + s.getMaxReservations(),
                        "",
                        ""
                });
            }
        }
    }

    private void refreshSlots() {
        try {
            allBooths = adminService.getAllBooths();
            allSlots = adminService.getAllTimeSlots();
            allBoothStats = adminService.getBoothScheduleStats();
            applySlotFilter();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "데이터 로딩 오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int findCurrentReservations(String boothName, String date, java.sql.Time startTime) {
        if (allBoothStats == null || startTime == null) return 0;
        String startStr = startTime.toString().substring(0, 5);
        for (String[] stat : allBoothStats) {
            if (stat[0].equals(boothName) && stat[2].equals(date)
                    && stat[3].length() >= 5 && stat[3].substring(0, 5).equals(startStr)) {
                try { return Integer.parseInt(stat[6]); } catch (Exception ignored) { return 0; }
            }
        }
        return 0;
    }

    public void showBooth() {
        activeTab = TAB_BOOTH;
        buildTabBar();
        contentLayout.show(contentCard, TAB_BOOTH);
        refreshBooths();
    }

    public void showTimeSlot() {
        activeTab = TAB_SLOT;
        buildTabBar();
        contentLayout.show(contentCard, TAB_SLOT);
        refreshSlots();
    }

    public void refresh() {
        if (TAB_BOOTH.equals(activeTab)) {
            refreshBooths();
        } else {
            refreshSlots();
        }
    }

    private JTextField searchField(Runnable onChange) {
        JTextField f = new JTextField();
        f.setFont(UIConstants.f(Font.PLAIN, 13));
        f.setBorder(new CompoundBorder(
                new LineBorder(UIConstants.BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        f.setPreferredSize(new Dimension(260, 36));

        f.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                onChange.run();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                onChange.run();
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                onChange.run();
            }
        });

        return f;
    }

    private JPanel searchWrap(JTextField f) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        p.add(f);
        return p;
    }

    private DefaultTableCellRenderer baseRenderer() {
        return new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t,
                    Object v,
                    boolean sel,
                    boolean foc,
                    int r,
                    int c
            ) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, false, false, r, c);

                l.setFont(UIConstants.f(Font.PLAIN, 13));
                l.setForeground(UIConstants.TEXT_PRIMARY);
                l.setBackground(UIConstants.WHITE);
                l.setOpaque(true);
                l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                l.setToolTipText(v == null ? "" : v.toString());

                return l;
            }
        };
    }

    private TableCellRenderer linkRenderer(String text, Color color) {
        return (t, v, sel, foc, row, col) -> {
            JPanel wrap = new JPanel(new GridBagLayout());
            wrap.setBackground(UIConstants.WHITE);

            JLabel l = new JLabel(text, SwingConstants.CENTER);
            l.setFont(UIConstants.f(Font.BOLD, 13));
            l.setForeground(color);
            l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            Dimension size = new Dimension(50, 24);
            l.setPreferredSize(size);
            l.setMinimumSize(size);
            l.setMaximumSize(size);

            wrap.add(l);
            return wrap;
        };
    }

    private JComboBox<String> deptCombo(int selectedId) {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(UIConstants.f(Font.PLAIN, 13));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        combo.addItem("학과 선택");

        if (allDepts != null) {
            for (Department d : allDepts) {
                combo.addItem(d.getDepartmentId() + " - " + d.getDepartmentName());
            }

            if (selectedId > 0) {
                for (int i = 0; i < combo.getItemCount(); i++) {
                    String item = combo.getItemAt(i);
                    if (item.startsWith(selectedId + " - ")) {
                        combo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        return combo;
    }

    private JComboBox<String> boothCombo(int selectedId) {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(UIConstants.f(Font.PLAIN, 13));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        combo.addItem("부스 선택");

        if (allBooths != null) {
            for (ConsultationBooth b : allBooths) {
                combo.addItem(b.getBoothId() + " - " + b.getBoothName());
            }

            if (selectedId > 0) {
                for (int i = 0; i < combo.getItemCount(); i++) {
                    String item = combo.getItemAt(i);
                    if (item.startsWith(selectedId + " - ")) {
                        combo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        return combo;
    }

    private String deptName(int departmentId) {
        if (allDepts == null) return String.valueOf(departmentId);

        return allDepts.stream()
                .filter(d -> d.getDepartmentId() == departmentId)
                .map(Department::getDepartmentName)
                .findFirst()
                .orElse(String.valueOf(departmentId));
    }

    private String boothName(int boothId) {
        if (allBooths == null) return String.valueOf(boothId);

        return allBooths.stream()
                .filter(b -> b.getBoothId() == boothId)
                .map(ConsultationBooth::getBoothName)
                .findFirst()
                .orElse(String.valueOf(boothId));
    }

    private JDialog dialog(String title, int width, int height) {
        JDialog d = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                title,
                Dialog.ModalityType.APPLICATION_MODAL
        );

        d.setSize(width, height);
        d.setLocationRelativeTo(this);
        d.setResizable(false);

        return d;
    }

    private JPanel formPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UIConstants.WHITE);
        p.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        return p;
    }

    private JLabel formTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UIConstants.f(Font.BOLD, 18));
        l.setForeground(UIConstants.TEXT_PRIMARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        return l;
    }

    private JTextField inputField(String value) {
        JTextField f = new JTextField(value);
        f.setFont(UIConstants.f(Font.PLAIN, 13));
        f.setBorder(new CompoundBorder(
                new LineBorder(UIConstants.BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        return f;
    }

    private JPanel labeledInput(String label, JTextField field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(label);
        l.setFont(UIConstants.f(Font.BOLD, 13));
        l.setForeground(UIConstants.TEXT_PRIMARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(l);
        p.add(field);

        return p;
    }

    private JPanel labeledCombo(String label, JComboBox<?> combo) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(label);
        l.setFont(UIConstants.f(Font.BOLD, 13));
        l.setForeground(UIConstants.TEXT_PRIMARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        combo.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(l);
        p.add(combo);

        return p;
    }

    private GreenButton saveBtn() {
        GreenButton b = new GreenButton("저장하기");
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);

        return b;
    }

    private Component vgap(int height) {
        return Box.createVerticalStrut(height);
    }

    private void warn(JDialog dialog, String msg) {
        JOptionPane.showMessageDialog(
                dialog,
                msg,
                "입력 오류",
                JOptionPane.WARNING_MESSAGE
        );
    }
}

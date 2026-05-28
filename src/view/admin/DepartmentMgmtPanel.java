package DB2026Team02.view.admin;

import DB2026Team02.model.Department;
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
import java.sql.SQLException;
import java.util.List;

public class DepartmentMgmtPanel extends JPanel {

    private static final String[] COLS   = {"ID", "학과명", "위치", "", ""};
    private static final int[]    WIDTHS = {60, 220, 280, 80, 80};

    private final AdminService adminService = new AdminService();
    private DefaultTableModel tableModel;
    private JTable table;
    private List<Department> allDepts;
    private JTextField searchField;

    public DepartmentMgmtPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG);
        add(new AdminSideBar(MainFrame.ADMIN_DEPT), BorderLayout.WEST);
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

        JLabel title = new JLabel("학과 관리");
        title.setFont(UIConstants.f(Font.BOLD, 22));
        title.setForeground(UIConstants.TEXT_PRIMARY);

        GreenButton addBtn = new GreenButton("+ 학과 추가");
        addBtn.setPreferredSize(new Dimension(110, 34));
        addBtn.setFont(UIConstants.f(Font.BOLD, 13));
        addBtn.addActionListener(e -> showFormDialog(null));

        p.add(title,  BorderLayout.WEST);
        p.add(addBtn, BorderLayout.EAST);
        return p;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(UIConstants.BG);
        body.setBorder(BorderFactory.createEmptyBorder(20, 32, 20, 32));

        searchField = new JTextField();
        searchField.setFont(UIConstants.f(Font.PLAIN, 13));
        searchField.setBorder(new CompoundBorder(
                new LineBorder(UIConstants.BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        searchField.setPreferredSize(new Dimension(260, 36));
        searchField.putClientProperty("JTextField.placeholderText", "검색...");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { applyFilter(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { applyFilter(); }
        });

        JPanel searchWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchWrap.setOpaque(false);
        searchWrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        searchWrap.add(searchField);
        body.add(searchWrap, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        styleTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(UIConstants.BORDER));
        scroll.getViewport().setBackground(UIConstants.WHITE);
        body.add(scroll, BorderLayout.CENTER);

        return body;
    }

    private void styleTable() {
        table.setFont(UIConstants.f(Font.PLAIN, 13));
        table.setRowHeight(50);
        table.setShowVerticalLines(false);
        table.setGridColor(UIConstants.BORDER);
        table.setBackground(UIConstants.WHITE);
        table.setSelectionBackground(new Color(248, 250, 252));
        table.setFocusable(false);

        JTableHeader hdr = table.getTableHeader();
        hdr.setFont(UIConstants.f(Font.BOLD, 12));
        hdr.setBackground(UIConstants.SURFACE);
        hdr.setForeground(UIConstants.TEXT_SECONDARY);
        hdr.setReorderingAllowed(false);
        hdr.setPreferredSize(new Dimension(hdr.getPreferredSize().width, 34));

        DefaultTableCellRenderer base = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, false, false, r, c);
                l.setFont(UIConstants.f(Font.PLAIN, 13));
                l.setForeground(UIConstants.TEXT_PRIMARY);
                l.setBackground(UIConstants.WHITE);
                l.setOpaque(true);
                l.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return l;
            }
        };

        for (int i = 0; i < COLS.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(WIDTHS[i]);
            table.getColumnModel().getColumn(i).setCellRenderer(base);
        }

        table.getColumnModel().getColumn(3).setCellRenderer((t, v, sel, foc, row, col) -> {
            JPanel wrap = new JPanel(new GridBagLayout());
            wrap.setBackground(UIConstants.WHITE);
            wrap.add(linkBtn("수정", UIConstants.CONFIRMED_FG));
            return wrap;
        });

        table.getColumnModel().getColumn(4).setCellRenderer((t, v, sel, foc, row, col) -> {
            JPanel wrap = new JPanel(new GridBagLayout());
            wrap.setBackground(UIConstants.WHITE);
            wrap.add(linkBtn("삭제", UIConstants.DANGER));
            return wrap;
        });

        table.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0) return;

                if (col == 3) onEdit(row);
                else if (col == 4) onDelete(row);
            }
        });
    }

    private JLabel linkBtn(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(UIConstants.f(Font.BOLD, 13));
        l.setForeground(color);
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return l;
    }

    private void onEdit(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        Department dept = allDepts.stream().filter(d -> d.getDepartmentId() == id).findFirst().orElse(null);
        if (dept != null) showFormDialog(dept);
    }

    private void onDelete(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        String name = tableModel.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "학과 [" + name + "]을(를) 삭제하시겠습니까?\n관련 교수/부스 데이터도 영향받을 수 있습니다.",
                "학과 삭제",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = adminService.deleteDepartment(id);

            if (ok) {
                JOptionPane.showMessageDialog(
                        this,
                        "학과가 삭제되었습니다.",
                        "완료",
                        JOptionPane.INFORMATION_MESSAGE
                );
                refresh();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "삭제 실패.",
                        "오류",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (SQLException ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "";

            if (msg.contains("foreign key constraint fails")
                    || msg.contains("fk_booth_department")
                    || msg.contains("fk_professor_department")) {

                JOptionPane.showMessageDialog(
                        this,
                        "해당 학과에 연결된 부스 또는 교수가 있어 삭제할 수 없습니다.\n" +
                                "먼저 관련 부스/교수 정보를 삭제하거나 다른 학과로 이동해주세요.",
                        "삭제 불가",
                        JOptionPane.WARNING_MESSAGE
                );

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "오류: " + ex.getMessage(),
                        "오류",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void showFormDialog(Department dept) {
        boolean isEdit = dept != null;
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                isEdit ? "학과 수정" : "+ 학과 추가",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel(isEdit ? "학과 수정" : "+ 학과 추가");
        title.setFont(UIConstants.f(Font.BOLD, 18));
        title.setForeground(UIConstants.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        JTextField nameField = formField("학과명 *", dept != null ? dept.getDepartmentName() : "", "예: 컴퓨터공학과");
        JTextField locField  = formField("위치",     dept != null ? dept.getLocation() : "",        "예: 공학관 A동 302호");

        panel.add(labeledField("학과명 *", nameField));
        panel.add(Box.createVerticalStrut(14));
        panel.add(labeledField("위치",     locField));
        panel.add(Box.createVerticalStrut(24));

        GreenButton saveBtn = new GreenButton("저장하기");
        saveBtn.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "학과명은 필수입니다.", "입력 오류", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                if (isEdit) {
                    adminService.updateDepartment(dept.getDepartmentId(), name, locField.getText().trim());
                    JOptionPane.showMessageDialog(dialog, "학과 정보가 수정되었습니다.", "완료", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    adminService.addDepartment(name, locField.getText().trim());
                    JOptionPane.showMessageDialog(dialog, "학과가 추가되었습니다.", "완료", JOptionPane.INFORMATION_MESSAGE);
                }
                dialog.dispose();
                refresh();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(saveBtn);
        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private JTextField formField(String hint, String value, String placeholder) {
        JTextField f = new JTextField(value);
        f.setFont(UIConstants.f(Font.PLAIN, 13));
        f.setBorder(new CompoundBorder(
                new LineBorder(UIConstants.BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return f;
    }

    private JPanel labeledField(String label, JTextField field) {
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

    private void applyFilter() {
        if (allDepts == null) return;
        String q = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        for (Department d : allDepts) {
            boolean match = q.isEmpty()
                    || d.getDepartmentName().toLowerCase().contains(q)
                    || (d.getLocation() != null && d.getLocation().toLowerCase().contains(q));
            if (match) tableModel.addRow(new Object[]{
                    d.getDepartmentId(),
                    d.getDepartmentName(),
                    d.getLocation() != null ? d.getLocation() : "",
                    "", ""
            });
        }
    }

    public void refresh() {
        try {
            allDepts = adminService.getAllDepartments();
            applyFilter();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "데이터 로딩 오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}

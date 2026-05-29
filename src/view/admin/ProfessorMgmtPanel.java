package DB2026Team02.view.admin;

import DB2026Team02.model.Department;
import DB2026Team02.model.Professor;
import DB2026Team02.service.AdminService;
import DB2026Team02.view.MainFrame;
import DB2026Team02.view.common.GreenButton;
import DB2026Team02.view.common.UIConstants;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class ProfessorMgmtPanel extends JPanel {

    private static final String[] COLS   = {"ID", "교수명", "소속 학과", "이메일", "전화번호", "", ""};
    private static final int[]    WIDTHS = {55, 120, 160, 200, 140, 70, 70};

    private final AdminService adminService = new AdminService();
    private DefaultTableModel tableModel;
    private JTable table;
    private List<Professor>  allProfs;
    private List<Department> allDepts;
    private JTextField searchField;

    public ProfessorMgmtPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG);
        add(new AdminSideBar(MainFrame.ADMIN_PROF), BorderLayout.WEST);
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
        JLabel title = new JLabel("교수 관리");
        title.setFont(UIConstants.f(Font.BOLD, 22));
        title.setForeground(UIConstants.TEXT_PRIMARY);

        GreenButton addBtn = new GreenButton("+ 교수 추가");
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

        searchField = buildSearchField();
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

    private JTextField buildSearchField() {
        JTextField f = new JTextField();
        f.setFont(UIConstants.f(Font.PLAIN, 13));
        f.setBorder(new CompoundBorder(new LineBorder(UIConstants.BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        f.setPreferredSize(new Dimension(260, 36));
        f.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { applyFilter(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { applyFilter(); }
        });
        return f;
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

        TableCellRenderer editRenderer = (t, v, sel, foc, row, col) -> {
            JPanel wrap = new JPanel(new GridBagLayout());
            wrap.setBackground(UIConstants.WHITE);
            wrap.add(linkBtn("수정", UIConstants.CONFIRMED_FG));
            return wrap;
        };
        TableCellRenderer delRenderer = (t, v, sel, foc, row, col) -> {
            JPanel wrap = new JPanel(new GridBagLayout());
            wrap.setBackground(UIConstants.WHITE);
            wrap.add(linkBtn("삭제", UIConstants.DANGER));
            return wrap;
        };

        table.getColumnModel().getColumn(5).setCellRenderer(editRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(delRenderer);

        table.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0) return;
                if (col == 5) onEdit(row);
                else if (col == 6) onDelete(row);
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
        Professor prof = allProfs.stream().filter(p -> p.getProfessorId() == id).findFirst().orElse(null);
        if (prof != null) showFormDialog(prof);
    }

    private void onDelete(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        String name = tableModel.getValueAt(row, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "교수 [" + name + "]을(를) 삭제하시겠습니까?\n관련 예약 데이터도 함께 삭제됩니다.",
                "교수 삭제",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        if (!checkAdminPassword()) return;

        try {
            boolean ok = adminService.deleteProfessorCascade(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "교수가 삭제되었습니다.", "완료", JOptionPane.INFORMATION_MESSAGE);
                refresh();
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

    private void showFormDialog(Professor prof) {
        boolean isEdit = prof != null;
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                isEdit ? "교수 수정" : "+ 교수 추가",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(440, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel(isEdit ? "교수 수정" : "+ 교수 추가");
        title.setFont(UIConstants.f(Font.BOLD, 18));
        title.setForeground(UIConstants.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));

        JTextField nameField  = inputField(prof != null ? prof.getProfessorName() : "");
        JTextField emailField = inputField(prof != null ? (prof.getEmail() != null ? prof.getEmail() : "") : "");
        JTextField phoneField = inputField(prof != null ? (prof.getPhone() != null ? prof.getPhone() : "") : "");

        JComboBox<String> deptCombo = new JComboBox<>();
        deptCombo.setFont(UIConstants.f(Font.PLAIN, 13));
        deptCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        if (allDepts != null) {
            deptCombo.addItem("학과 선택");
            for (Department d : allDepts) deptCombo.addItem(d.getDepartmentId() + " - " + d.getDepartmentName());
            if (prof != null) {
                for (int i = 0; i < deptCombo.getItemCount(); i++) {
                    String item = deptCombo.getItemAt(i);
                    if (item.startsWith(prof.getDepartmentId() + " - ")) { deptCombo.setSelectedIndex(i); break; }
                }
            }
        }

        panel.add(labeledInput("교수명 *",    nameField));  panel.add(Box.createVerticalStrut(12));
        panel.add(labeledCombo("소속 학과 *", deptCombo));  panel.add(Box.createVerticalStrut(12));
        panel.add(labeledInput("이메일",       emailField)); panel.add(Box.createVerticalStrut(12));
        panel.add(labeledInput("전화번호",     phoneField)); panel.add(Box.createVerticalStrut(22));

        GreenButton saveBtn = new GreenButton("저장하기");
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) { JOptionPane.showMessageDialog(dialog, "교수명은 필수입니다.", "입력 오류", JOptionPane.WARNING_MESSAGE); return; }
            if (deptCombo.getSelectedIndex() == 0) { JOptionPane.showMessageDialog(dialog, "소속 학과를 선택하세요.", "입력 오류", JOptionPane.WARNING_MESSAGE); return; }

            int deptId = allDepts.get(deptCombo.getSelectedIndex() - 1).getDepartmentId();
            try {
                if (isEdit) {
                    adminService.updateProfessor(prof.getProfessorId(), deptId, name,
                            emailField.getText().trim(), phoneField.getText().trim());
                    JOptionPane.showMessageDialog(dialog, "교수 정보가 수정되었습니다.", "완료", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    adminService.addProfessor(deptId, name, emailField.getText().trim(), phoneField.getText().trim());
                    JOptionPane.showMessageDialog(dialog, "교수가 추가되었습니다.", "완료", JOptionPane.INFORMATION_MESSAGE);
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

    private JTextField inputField(String value) {
        JTextField f = new JTextField(value);
        f.setFont(UIConstants.f(Font.PLAIN, 13));
        f.setBorder(new CompoundBorder(new LineBorder(UIConstants.BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return f;
    }

    private JPanel labeledInput(String label, JTextField field) {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); p.setOpaque(false); p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(label); lbl.setFont(UIConstants.f(Font.BOLD, 13)); lbl.setForeground(UIConstants.TEXT_PRIMARY); lbl.setAlignmentX(Component.LEFT_ALIGNMENT); lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        field.setAlignmentX(Component.LEFT_ALIGNMENT); p.add(lbl); p.add(field); return p;
    }

    private JPanel labeledCombo(String label, JComboBox<String> combo) {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); p.setOpaque(false); p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(label); lbl.setFont(UIConstants.f(Font.BOLD, 13)); lbl.setForeground(UIConstants.TEXT_PRIMARY); lbl.setAlignmentX(Component.LEFT_ALIGNMENT); lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT); p.add(lbl); p.add(combo); return p;
    }

    private void applyFilter() {
        if (allProfs == null) return;
        String q = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        for (Professor p : allProfs) {
            String deptName = allDepts != null ? allDepts.stream()
                    .filter(d -> d.getDepartmentId() == p.getDepartmentId())
                    .map(Department::getDepartmentName).findFirst().orElse("") : "";
            boolean match = q.isEmpty()
                    || p.getProfessorName().toLowerCase().contains(q)
                    || deptName.toLowerCase().contains(q)
                    || (p.getEmail() != null && p.getEmail().toLowerCase().contains(q));
            if (match) tableModel.addRow(new Object[]{
                    p.getProfessorId(), p.getProfessorName(), deptName,
                    p.getEmail() != null ? p.getEmail() : "",
                    p.getPhone() != null ? p.getPhone() : "",
                    "", ""
            });
        }
    }

    public void refresh() {
        try {
            allDepts = adminService.getAllDepartments();
            allProfs = adminService.getAllProfessors();
            applyFilter();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "데이터 로딩 오류: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}

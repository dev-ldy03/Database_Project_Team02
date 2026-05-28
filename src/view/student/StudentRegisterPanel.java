package DB2026Team02.view.student;

import DB2026Team02.service.StudentService;
import DB2026Team02.view.MainFrame;
import DB2026Team02.view.common.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.SQLException;

public class StudentRegisterPanel extends JPanel {

    private JTextField tfName, tfEmail, tfPhone, tfMajor;

    public StudentRegisterPanel() {
        setLayout(new BorderLayout());
        add(buildLeft(), BorderLayout.WEST);
        add(buildRight(), BorderLayout.CENTER);
    }

    private JPanel buildLeft() {
        JPanel left = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                GradientPaint gp = new GradientPaint(
                        0, 0, UIConstants.PRIMARY_DARK,
                        0, getHeight(), UIConstants.PRIMARY
                );

                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };

        left.setPreferredSize(new Dimension(380, 800));

        JLabel logo = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(new Color(255, 255, 255, 50));
                g2.fill(new Ellipse2D.Float(0, 0, 60, 60));

                g2.setColor(Color.WHITE);
                g2.fill(new Ellipse2D.Float(7, 7, 46, 46));

                g2.setColor(UIConstants.PRIMARY);
                g2.setFont(UIConstants.f(Font.BOLD, 22));
                g2.drawString("E", 22, 38);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        logo.setBounds(60, 100, 64, 64);
        left.add(logo);

        JLabel title = new JLabel("<html>대학원 박람회<br>상담 예약 시스템</html>");
        title.setFont(UIConstants.f(Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        title.setBounds(60, 185, 280, 80);

        left.add(title);

        return left;
    }

    private JPanel buildStep(int num, String label, boolean active) {
        JPanel p = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                if (active) {
                    Graphics2D g2 = (Graphics2D) g.create();

                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));

                    g2.dispose();
                }

                super.paintComponent(g);
            }
        };

        p.setOpaque(false);

        JLabel numLabel = new JLabel(String.valueOf(num)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(Color.WHITE);
                g2.fill(new Ellipse2D.Float(0, 0, 28, 28));

                g2.setColor(UIConstants.PRIMARY);
                g2.setFont(UIConstants.f(Font.BOLD, 12));

                FontMetrics fm = g2.getFontMetrics();
                String t = String.valueOf(num);

                g2.drawString(t, 14 - fm.stringWidth(t) / 2, 19);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        numLabel.setOpaque(false);
        numLabel.setBounds(10, 10, 28, 28);

        p.add(numLabel);

        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.f(active ? Font.BOLD : Font.PLAIN, 14));
        lbl.setForeground(new Color(255, 255, 255, active ? 255 : 160));
        lbl.setBounds(48, 13, 200, 22);

        p.add(lbl);

        return p;
    }

    private JPanel buildRight() {
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(UIConstants.WHITE);
        right.setBorder(BorderFactory.createEmptyBorder(60, 80, 60, 80));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel title = new JLabel("학생 등록");
        title.setFont(UIConstants.f(Font.BOLD, 28));
        title.setForeground(UIConstants.TEXT_PRIMARY);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 4, 0);

        right.add(title, gbc);

        JLabel sub = new JLabel("정보를 입력하여 예약을 시작하세요. 이메일은 중복 등록이 불가합니다.");
        sub.setFont(UIConstants.f(Font.PLAIN, 13));
        sub.setForeground(UIConstants.TEXT_SECONDARY);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 20, 0);

        right.add(sub, gbc);

        JSeparator sep = new JSeparator();
        sep.setForeground(UIConstants.BORDER);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 28, 0);

        right.add(sep, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 16);
        right.add(buildField("이름 *", tfName = new JTextField()), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        right.add(buildField("이메일 *", tfEmail = new JTextField()), gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 32, 16);
        right.add(buildField("전화번호", tfPhone = new JTextField()), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 32, 0);
        right.add(buildField("학부 / 전공", tfMajor = new JTextField()), gbc);

        GreenButton submitBtn = new GreenButton("등록 완료");
        submitBtn.setPreferredSize(new Dimension(0, 48));

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 10, 0);

        right.add(submitBtn, gbc);

        JPanel loginLinkBox = buildLoginLinkBox();

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 0, 0);

        right.add(loginLinkBox, gbc);

        submitBtn.addActionListener(e -> onSubmit());

        return right;
    }

    private JPanel buildLoginLinkBox() {
        JPanel loginLinkBox = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.dispose();

                super.paintComponent(g);
            }
        };

        loginLinkBox.setOpaque(false);
        loginLinkBox.setPreferredSize(new Dimension(0, 44));
        loginLinkBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLinkBox.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JLabel loginLink = new JLabel("이미 등록하셨나요?");
        loginLink.setFont(UIConstants.f(Font.BOLD, 13));
        loginLink.setForeground(UIConstants.PRIMARY);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        loginLinkBox.add(loginLink);

        MouseAdapter lookupAction = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onLookupByEmail();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                loginLink.setForeground(UIConstants.PRIMARY_DARK);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginLink.setForeground(UIConstants.PRIMARY);
            }
        };

        loginLinkBox.addMouseListener(lookupAction);
        loginLink.addMouseListener(lookupAction);

        return loginLinkBox;
    }

    private JPanel buildField(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.f(Font.BOLD, 13));
        lbl.setForeground(UIConstants.TEXT_PRIMARY);

        field.setFont(UIConstants.f(Font.PLAIN, 14));
        field.setForeground(UIConstants.TEXT_PRIMARY);
        field.setBackground(UIConstants.BG);
        field.setBorder(new CompoundBorder(
                new LineBorder(UIConstants.BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(0, 44));

        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);

        return p;
    }

    private void onSubmit() {
        String name = tfName.getText().trim();
        String email = tfEmail.getText().trim();
        String phone = tfPhone.getText().trim();
        String major = tfMajor.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "이름과 이메일은 필수 입력 항목입니다.",
                    "입력 오류",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            StudentService svc = new StudentService();

            int id = svc.registerStudent(name, email, phone, major);

            if (id == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "이미 등록된 이메일이거나 등록에 실패했습니다.\n다른 이메일을 사용하거나 이메일로 조회해 주세요.",
                        "등록 실패",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            MainFrame.setStudent(id, name, email);

            JOptionPane.showMessageDialog(
                    this,
                    "환영합니다, " + name + " 님!",
                    "등록 완료",
                    JOptionPane.INFORMATION_MESSAGE
            );

            MainFrame.navigate(MainFrame.STUDENT_HOME);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "데이터베이스 오류: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void onLookupByEmail() {
        String email = JOptionPane.showInputDialog(
                this,
                "등록된 이메일을 입력하세요:",
                "이메일 조회",
                JOptionPane.PLAIN_MESSAGE
        );

        if (email == null || email.trim().isEmpty()) {
            return;
        }

        try {
            StudentService svc = new StudentService();

            var student = svc.getStudentByEmail(email.trim());

            if (student == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "해당 이메일로 등록된 학생이 없습니다.",
                        "조회 실패",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            MainFrame.setStudent(
                    student.getStudentId(),
                    student.getStudentName(),
                    student.getEmail()
            );

            MainFrame.navigate(MainFrame.STUDENT_HOME);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "데이터베이스 오류: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
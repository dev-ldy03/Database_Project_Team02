package DB2026Team02.view.student;

import DB2026Team02.model.ConsultationBooth;
import DB2026Team02.model.Professor;
import DB2026Team02.service.ReservationService;
import DB2026Team02.service.StudentService;
import DB2026Team02.view.MainFrame;
import DB2026Team02.view.common.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.SQLException;
import java.util.List;

public class MakeReservationPanel extends JPanel {

    private JTextArea notesArea;

    private JLabel lblDept;
    private JLabel lblProf;
    private JLabel lblBooth;
    private JLabel lblDate;
    private JLabel lblTime;
    private JLabel lblStudent;

    public MakeReservationPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG);

        add(new NavBar(NavBar.STUDENT), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 0));
        body.setBackground(UIConstants.BG);

        body.add(buildTitleBar(), BorderLayout.NORTH);
        body.add(buildContent(), BorderLayout.CENTER);

        return body;
    }

    private JPanel buildTitleBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UIConstants.WHITE);
        bar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UIConstants.BORDER),
                BorderFactory.createEmptyBorder(16, 80, 16, 80)
        ));

        JLabel title = new JLabel("상담 예약 신청");
        title.setFont(UIConstants.f(Font.BOLD, 23));
        title.setForeground(UIConstants.TEXT_PRIMARY);
        bar.add(title, BorderLayout.WEST);

        JPanel steps = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        steps.setOpaque(false);

        String[] stepNames = {"1. 학과 선택", "2. 시간 선택", "3. 예약 확인"};

        for (int i = 0; i < stepNames.length; i++) {
            steps.add(buildStepChip(stepNames[i], i == 2));
        }

        bar.add(steps, BorderLayout.EAST);

        return bar;
    }

    private JLabel buildStepChip(String text, boolean active) {
        JLabel chip = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(active ? UIConstants.PRIMARY : UIConstants.SURFACE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));

                g2.dispose();
                super.paintComponent(g);
            }
        };

        chip.setFont(UIConstants.f(Font.BOLD, 12));
        chip.setForeground(active ? Color.WHITE : UIConstants.TEXT_MUTED);
        chip.setPreferredSize(new Dimension(118, 34));
        chip.setOpaque(false);

        return chip;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new GridLayout(1, 2, 24, 0));
        content.setBackground(UIConstants.BG);
        content.setBorder(BorderFactory.createEmptyBorder(26, 80, 26, 80));

        content.add(buildSummaryCard());
        content.add(buildNotesCard());

        return content;
    }

    private JPanel buildSummaryCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 0));
        card.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        JLabel title = new JLabel("예약 정보 요약");
        title.setFont(UIConstants.f(Font.BOLD, 17));
        title.setForeground(UIConstants.TEXT_PRIMARY);
        title.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, UIConstants.BORDER),
                BorderFactory.createEmptyBorder(0, 0, 12, 0)
        ));

        card.add(title, BorderLayout.NORTH);

        JPanel rows = new JPanel();
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        rows.setOpaque(false);
        rows.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        lblDept = summaryValue("");
        lblProf = summaryValue("");
        lblBooth = summaryValue("");
        lblDate = summaryValue("");
        lblTime = summaryValue("");
        lblStudent = summaryValue("");

        Object[][] items = {
                {"학과", lblDept},
                {"교수", lblProf},
                {"부스", lblBooth},
                {"날짜", lblDate},
                {"시간", lblTime},
                {"학생", lblStudent}
        };

        boolean shaded = true;

        for (Object[] item : items) {
            rows.add(buildSummaryRow((String) item[0], (JLabel) item[1], shaded));
            rows.add(Box.createVerticalStrut(6));
            shaded = !shaded;
        }

        JScrollPane scroll = new JScrollPane(rows);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JLabel summaryValue(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.f(Font.BOLD, 14));
        label.setForeground(UIConstants.TEXT_PRIMARY);
        label.setHorizontalAlignment(SwingConstants.RIGHT);

        return label;
    }

    private JPanel buildSummaryRow(String label, JLabel value, boolean shaded) {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setBackground(shaded ? UIConstants.SURFACE : UIConstants.WHITE);
        row.setBorder(BorderFactory.createEmptyBorder(13, 14, 13, 14));
        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 52));

        JLabel labelText = new JLabel(label);
        labelText.setFont(UIConstants.f(Font.PLAIN, 14));
        labelText.setForeground(UIConstants.TEXT_SECONDARY);

        row.add(labelText, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);

        return row;
    }

    private JPanel buildNotesCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 18));
        card.setBorder(BorderFactory.createEmptyBorder(26, 26, 26, 26));

        JPanel notesSection = new JPanel(new BorderLayout(0, 10));
        notesSection.setOpaque(false);

        JLabel notesTitle = new JLabel("상담 요청 사항 (선택)");
        notesTitle.setFont(UIConstants.f(Font.BOLD, 17));
        notesTitle.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel notesSub = new JLabel("교수님께 미리 전달하고 싶은 내용을 입력하세요.");
        notesSub.setFont(UIConstants.f(Font.PLAIN, 13));
        notesSub.setForeground(UIConstants.TEXT_MUTED);

        JPanel topInfo = new JPanel();
        topInfo.setLayout(new BoxLayout(topInfo, BoxLayout.Y_AXIS));
        topInfo.setOpaque(false);
        topInfo.add(notesTitle);
        topInfo.add(Box.createVerticalStrut(6));
        topInfo.add(notesSub);

        notesArea = new JTextArea(8, 0);
        notesArea.setFont(UIConstants.f(Font.PLAIN, 14));
        notesArea.setForeground(UIConstants.TEXT_PRIMARY);
        notesArea.setBackground(UIConstants.BG);
        notesArea.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setBorder(new LineBorder(UIConstants.BORDER, 1, true));
        notesScroll.getViewport().setBackground(UIConstants.BG);

        notesSection.add(topInfo, BorderLayout.NORTH);
        notesSection.add(notesScroll, BorderLayout.CENTER);

        card.add(notesSection, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.setOpaque(false);

        JPanel warning = buildWarningBox();
        bottom.add(warning);
        bottom.add(Box.createVerticalStrut(16));

        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        btnPanel.setOpaque(false);

        GreenButton confirmBtn = new GreenButton("예약 확정하기");
        confirmBtn.setPreferredSize(new Dimension(0, 54));
        confirmBtn.setFont(UIConstants.f(Font.BOLD, 15));
        confirmBtn.addActionListener(e -> onConfirm());

        JButton cancelBtn = new JButton("취소하고 돌아가기");
        cancelBtn.setFont(UIConstants.f(Font.PLAIN, 13));
        cancelBtn.setForeground(UIConstants.TEXT_MUTED);
        cancelBtn.setContentAreaFilled(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> MainFrame.navigate(MainFrame.DEPT_DETAIL));

        btnPanel.add(confirmBtn);
        btnPanel.add(cancelBtn);

        bottom.add(btnPanel);

        card.add(bottom, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildWarningBox() {
        JPanel warning = new JPanel(new BorderLayout());
        warning.setBackground(UIConstants.WARNING_BG);
        warning.setBorder(new CompoundBorder(
                new LineBorder(new Color(253, 230, 138), 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));

        JLabel warnText = new JLabel(
                "<html>"
                        + "예약 취소는 상담 시작 1시간 전까지 가능합니다.<br>"
                        + "중복 예약은 허용되지 않으며, 노쇼 처리 시 패널티가 부여될 수 있습니다."
                        + "</html>"
        );

        warnText.setFont(UIConstants.f(Font.PLAIN, 13));
        warnText.setForeground(UIConstants.WARNING_FG);

        warning.add(warnText, BorderLayout.CENTER);

        return warning;
    }

    private JPanel roundCard() {
        return new JPanel() {
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
    }

    private void onConfirm() {
        int studentId = MainFrame.getStudentId();
        int slotId = MainFrame.getSelectedSlotId();
        int profId = MainFrame.getSelectedProfId();
        int boothId = MainFrame.getSelectedBoothId();

        if (studentId == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "로그인 정보가 없습니다. 다시 로그인해 주세요.",
                    "오류",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (slotId == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "예약할 시간대를 선택해 주세요.",
                    "선택 오류",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String notes = notesArea.getText().trim();

        try {
            ReservationService svc = new ReservationService();

            int reservationId = svc.createReservation(
                    studentId,
                    slotId,
                    profId,
                    boothId,
                    notes
            );

            if (reservationId == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "예약에 실패했습니다.\n이미 해당 시간대에 예약이 있거나 정원이 마감되었습니다.",
                        "예약 실패",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "예약이 완료되었습니다.\n예약 번호: #" + reservationId,
                    "예약 완료",
                    JOptionPane.INFORMATION_MESSAGE
            );

            MainFrame.navigate(MainFrame.MY_RESERVATIONS);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "데이터베이스 오류: " + ex.getMessage(),
                    "오류",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void refresh() {
        if (notesArea != null) {
            notesArea.setText("");
        }

        if (lblDept != null) {
            lblDept.setText(nullToBlank(MainFrame.getSelectedDeptName()));
        }

        if (lblDate != null) {
            lblDate.setText(nullToBlank(MainFrame.getSelectedDate()));
        }

        if (lblTime != null) {
            lblTime.setText(nullToBlank(MainFrame.getSelectedTime()));
        }

        if (lblStudent != null) {
            String studentName = nullToBlank(MainFrame.getStudentName());
            String studentEmail = nullToBlank(MainFrame.getStudentEmail());

            if (studentEmail.isBlank()) {
                lblStudent.setText(studentName);
            } else {
                lblStudent.setText(studentName + " (" + studentEmail + ")");
            }
        }

        loadProfessorSummaryFromBackend();
        loadBoothSummaryFromBackend();
    }

    private void loadProfessorSummaryFromBackend() {
        if (lblProf == null) return;

        int selectedProfId = MainFrame.getSelectedProfId();
        int selectedDeptId = MainFrame.getSelectedDeptId();

        if (selectedProfId == -1 || selectedDeptId == -1) {
            lblProf.setText("");
            return;
        }

        try {
            StudentService svc = new StudentService();
            List<Professor> professors = svc.getProfessorsByDepartment(selectedDeptId);

            for (Professor p : professors) {
                if (p.getProfessorId() == selectedProfId) {
                    lblProf.setText(p.getProfessorName() + " 교수");
                    return;
                }
            }

            lblProf.setText("교수 ID " + selectedProfId);

        } catch (SQLException ex) {
            lblProf.setText("교수 ID " + selectedProfId);
        }
    }

    private void loadBoothSummaryFromBackend() {
        if (lblBooth == null) return;

        int selectedBoothId = MainFrame.getSelectedBoothId();
        int selectedDeptId = MainFrame.getSelectedDeptId();

        if (selectedBoothId == -1 || selectedDeptId == -1) {
            lblBooth.setText(nullToBlank(MainFrame.getSelectedBoothName()));
            return;
        }

        try {
            StudentService svc = new StudentService();
            List<ConsultationBooth> booths = svc.getBoothsByDepartment(selectedDeptId);

            for (ConsultationBooth b : booths) {
                if (b.getBoothId() == selectedBoothId) {
                    String boothName = nullToBlank(b.getBoothName());
                    String boothType = nullToBlank(b.getBoothType());

                    if (boothType.isBlank()) {
                        lblBooth.setText(boothName);
                    } else {
                        lblBooth.setText(boothName + " (" + boothType + ")");
                    }
                    return;
                }
            }

            lblBooth.setText(nullToBlank(MainFrame.getSelectedBoothName()));

        } catch (SQLException ex) {
            lblBooth.setText(nullToBlank(MainFrame.getSelectedBoothName()));
        }
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }
}
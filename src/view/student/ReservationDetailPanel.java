package DB2026Team02.view.student;

import DB2026Team02.model.ReservationDetail;
import DB2026Team02.service.ReservationService;
import DB2026Team02.view.MainFrame;
import DB2026Team02.view.common.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.SQLException;

public class ReservationDetailPanel extends JPanel {

    private JLabel lblId, lblDept, lblProf, lblBooth, lblDate, lblTime;
    private JLabel lblStudent, lblEmail, lblCreated;
    private JLabel pageTitle, statusBadge;

    private JLabel notesContent;
    private JPanel timelinePanel;

    public ReservationDetailPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG);

        add(new NavBar(NavBar.STUDENT), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(UIConstants.BG);

        body.add(buildPageHeader(), BorderLayout.NORTH);
        body.add(buildContent(), BorderLayout.CENTER);

        return body;
    }

    private JPanel buildPageHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        header.setBackground(UIConstants.BG);
        header.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 80));

        JLabel back = new JLabel("← 내 예약으로");
        back.setFont(UIConstants.f(Font.BOLD, 13));
        back.setForeground(UIConstants.PRIMARY);
        back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        back.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                MainFrame.navigate(MainFrame.MY_RESERVATIONS);
            }
        });

        pageTitle = new JLabel("예약 상세 정보");
        pageTitle.setFont(UIConstants.f(Font.BOLD, 22));
        pageTitle.setForeground(UIConstants.TEXT_PRIMARY);

        statusBadge = new JLabel("CONFIRMED", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));

                g2.dispose();
                super.paintComponent(g);
            }
        };

        statusBadge.setFont(UIConstants.f(Font.BOLD, 12));
        statusBadge.setForeground(UIConstants.CONFIRMED_FG);
        statusBadge.setBackground(UIConstants.CONFIRMED_BG);
        statusBadge.setOpaque(false);
        statusBadge.setPreferredSize(new Dimension(110, 28));

        header.add(back);
        header.add(pageTitle);
        header.add(statusBadge);

        return header;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setBackground(UIConstants.BG);
        content.setBorder(BorderFactory.createEmptyBorder(0, 80, 24, 80));

        content.add(buildDetailCard());
        content.add(buildRightColumn());

        return content;
    }

    private JPanel buildDetailCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 0));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("예약 상세");
        title.setFont(UIConstants.f(Font.BOLD, 15));
        title.setForeground(UIConstants.TEXT_PRIMARY);
        title.setBorder(new MatteBorder(0, 0, 1, 0, UIConstants.BORDER));

        card.add(title, BorderLayout.NORTH);

        JPanel rows = new JPanel();
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        rows.setOpaque(false);

        String[][] fields = {
                {"예약 ID",     ""},  // 0
                {"학과",        ""},  // 1
                {"담당 교수",   ""},  // 2
                {"상담 부스",   ""},  // 3
                {"예약 날짜",   ""},  // 4
                {"상담 시간",   ""},  // 5
                {"학생 이름",   ""},  // 6
                {"학생 이메일", ""},  // 7
                {"생성일",      ""},  // 8
        };

        JLabel[] labels = {
                lblId, lblDept, lblProf, lblBooth, lblDate, lblTime,
                lblStudent, lblEmail, lblCreated
        };

        for (int i = 0; i < fields.length; i++) {
            labels[i] = new JLabel(fields[i][1]);
            labels[i].setFont(UIConstants.f(Font.BOLD, 13));
            labels[i].setForeground(UIConstants.TEXT_PRIMARY);

            rows.add(buildDetailRow(fields[i][0], labels[i], i % 2 == 0));
        }

        lblId      = labels[0];
        lblDept    = labels[1];
        lblProf    = labels[2];
        lblBooth   = labels[3];
        lblDate    = labels[4];
        lblTime    = labels[5];
        lblStudent = labels[6];
        lblEmail   = labels[7];
        lblCreated = labels[8];

        JScrollPane scroll = new JScrollPane(rows);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UIConstants.WHITE);

        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private JPanel buildDetailRow(String label, JLabel value, boolean shaded) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(shaded ? UIConstants.SURFACE : UIConstants.WHITE);
        row.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 46));

        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.f(Font.PLAIN, 13));
        lbl.setForeground(UIConstants.TEXT_SECONDARY);
        lbl.setPreferredSize(new Dimension(120, 22));

        row.add(lbl, BorderLayout.WEST);
        row.add(value, BorderLayout.CENTER);

        return row;
    }

    private JPanel buildRightColumn() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);

        col.add(buildNotesCard());
        col.add(Box.createVerticalStrut(16));
        col.add(buildTimelineCard());
        col.add(Box.createVerticalStrut(16));
        col.add(buildCancelButton());

        return col;
    }

    private JPanel buildNotesCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 160));

        JLabel title = new JLabel("📝  상담 요청 사항");
        title.setFont(UIConstants.emoji(14));
        title.setForeground(UIConstants.TEXT_PRIMARY);

        card.add(title, BorderLayout.NORTH);

        JPanel notesBox = new JPanel(new BorderLayout());
        notesBox.setBackground(UIConstants.BG);
        notesBox.setBorder(new LineBorder(UIConstants.BORDER, 1, true));

        notesContent = new JLabel("<html><div style='padding:8px'>-</div></html>");
        notesContent.setFont(UIConstants.f(Font.PLAIN, 13));
        notesContent.setForeground(UIConstants.TEXT_PRIMARY);

        notesBox.add(notesContent, BorderLayout.CENTER);
        card.add(notesBox, BorderLayout.CENTER);

        return card;
    }

    private JPanel buildTimelineCard() {
        JPanel card = roundCard();
        card.setLayout(new BorderLayout(0, 8));
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        card.setMaximumSize(new Dimension(Short.MAX_VALUE, 220));

        JLabel title = new JLabel("📋  예약 진행 상황");
        title.setFont(UIConstants.emoji(14));
        title.setForeground(UIConstants.TEXT_PRIMARY);

        card.add(title, BorderLayout.NORTH);

        timelinePanel = new JPanel();
        timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
        timelinePanel.setOpaque(false);

        card.add(timelinePanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel buildCancelButton() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Short.MAX_VALUE, 56));

        JLabel cancelBtn = new JLabel("예약 취소하기", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(UIConstants.DANGER_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));

                g2.setColor(new Color(220, 38, 38, 80));
                g2.setStroke(new BasicStroke(1));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));

                g2.dispose();
                super.paintComponent(g);
            }
        };

        cancelBtn.setFont(UIConstants.f(Font.BOLD, 15));
        cancelBtn.setForeground(UIConstants.DANGER);
        cancelBtn.setOpaque(false);
        cancelBtn.setPreferredSize(new Dimension(0, 52));
        cancelBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        cancelBtn.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { onCancel(); }
        });

        wrapper.add(cancelBtn, BorderLayout.CENTER);

        return wrapper;
    }

    private void onCancel() {
        ReservationDetail rd = MainFrame.getSelectedReservation();

        if (rd == null) return;

        if ("COMPLETED".equals(rd.getStatus()) || "CANCELLED".equals(rd.getStatus())) {
            JOptionPane.showMessageDialog(MainFrame.getInstance(),
                    "이미 완료되었거나 취소된 예약입니다.",
                    "취소 불가",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(MainFrame.getInstance(),
                "예약 #" + rd.getReservationId() + "를 취소하시겠습니까?",
                "예약 취소",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = new ReservationService().cancelReservation(rd.getReservationId());

            if (ok) {
                JOptionPane.showMessageDialog(MainFrame.getInstance(),
                        "예약이 취소되었습니다.",
                        "완료",
                        JOptionPane.INFORMATION_MESSAGE
                );

                SwingUtilities.invokeLater(() -> MainFrame.navigate(MainFrame.MY_RESERVATIONS));

            } else {
                JOptionPane.showMessageDialog(MainFrame.getInstance(),
                        "취소에 실패했습니다.",
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

    public void refresh() {
        ReservationDetail r = MainFrame.getSelectedReservation();

        if (r == null) return;

        if (pageTitle != null) {
            pageTitle.setText("예약 #" + r.getReservationId() + " 상세 정보");
        }

        updateStatusBadge(r.getStatus());
        updateDetailInfo(r);
        updateNotes(r);
        updateTimeline(r);
    }

    private void updateStatusBadge(String status) {
        if (statusBadge == null) return;

        String safeStatus = status == null ? "" : status;

        Color fg = UIConstants.CONFIRMED_FG;
        Color bg = UIConstants.CONFIRMED_BG;

        switch (safeStatus) {
            case "PENDING":
                fg = UIConstants.PENDING_FG;
                bg = UIConstants.PENDING_BG;
                break;

            case "COMPLETED":
                fg = UIConstants.COMPLETED_FG;
                bg = UIConstants.COMPLETED_BG;
                break;

            case "CANCELLED":
                fg = UIConstants.CANCELLED_FG;
                bg = UIConstants.CANCELLED_BG;
                break;

            case "CONFIRMED":
            default:
                fg = UIConstants.CONFIRMED_FG;
                bg = UIConstants.CONFIRMED_BG;
                break;
        }

        statusBadge.setText(safeStatus);
        statusBadge.setForeground(fg);
        statusBadge.setBackground(bg);
        statusBadge.repaint();
    }

    private void updateDetailInfo(ReservationDetail r) {
        if (lblId != null) {
            lblId.setText("#" + r.getReservationId());
        }

        if (lblDept != null) {
            lblDept.setText(nullToBlank(r.getDepartmentName()));
        }

        if (lblProf != null) {
            lblProf.setText(formatProfessorName(r.getProfessorName()));
        }

        if (lblBooth != null) {
            String boothName = nullToBlank(r.getBoothName());
            String boothType = nullToBlank(r.getBoothType());

            if (!boothType.isBlank()) {
                lblBooth.setText(boothName + "  (" + boothType + ")");
            } else {
                lblBooth.setText(boothName);
            }
        }

        if (lblDate != null) {
            lblDate.setText(r.getSlotDate() != null ? r.getSlotDate().toString() : "");
        }

        if (lblTime != null) {
            lblTime.setText(formatTimeRange(r));
        }

        if (lblStudent != null) {
            lblStudent.setText(nullToBlank(r.getStudentName()));
        }

        if (lblEmail != null) {
            lblEmail.setText(nullToBlank(r.getStudentEmail()));
        }

        if (lblCreated != null) {
            lblCreated.setText(
                    r.getCreatedAt() != null
                            ? r.getCreatedAt().toLocalDate().toString()
                            : ""
            );
        }
    }

    private void updateNotes(ReservationDetail r) {
        if (notesContent == null) return;

        notesContent.setText(
                "<html><div style='padding:8px'>"
                        + escapeHtml(extractNotes(r)).replace("\n", "<br>")
                        + "</div></html>"
        );
    }

    private void updateTimeline(ReservationDetail r) {
        if (timelinePanel == null) return;

        timelinePanel.removeAll();

        String status = r.getStatus() == null ? "" : r.getStatus();

        timelinePanel.add(buildTimelineRow(
                "green",
                "예약 생성",
                r.getCreatedAt() != null ? r.getCreatedAt().toLocalDate().toString() : ""
        ));
        timelinePanel.add(Box.createVerticalStrut(4));

        if ("PENDING".equals(status)) {
            timelinePanel.add(buildTimelineRow(
                    "blue",
                    "예약 대기",
                    "관리자 확인 대기"
            ));
            timelinePanel.add(Box.createVerticalStrut(4));

            timelinePanel.add(buildTimelineRow(
                    "gray",
                    "체크인 대기",
                    "예약 확정 후 상담 당일 현장 체크인 필요"
            ));

        } else if ("CONFIRMED".equals(status)) {
            timelinePanel.add(buildTimelineRow(
                    "blue",
                    "예약 확인",
                    "CONFIRMED"
            ));
            timelinePanel.add(Box.createVerticalStrut(4));

            timelinePanel.add(buildTimelineRow(
                    "gray",
                    "체크인 대기",
                    "상담 당일 현장 체크인 필요"
            ));
            timelinePanel.add(Box.createVerticalStrut(4));

            timelinePanel.add(buildTimelineRow(
                    "gray",
                    "상담 예정",
                    r.getSlotDate() != null ? r.getSlotDate() + " 예정" : ""
            ));

        } else if ("COMPLETED".equals(status)) {
            timelinePanel.add(buildTimelineRow(
                    "green",
                    "예약 확인",
                    "CONFIRMED"
            ));
            timelinePanel.add(Box.createVerticalStrut(4));

            timelinePanel.add(buildTimelineRow(
                    "green",
                    "상담 완료",
                    r.getSlotDate() != null ? r.getSlotDate().toString() : ""
            ));

        } else if ("CANCELLED".equals(status)) {
            timelinePanel.add(buildTimelineRow(
                    "gray",
                    "예약 취소",
                    "CANCELLED"
            ));

        } else {
            timelinePanel.add(buildTimelineRow(
                    "gray",
                    "상태 확인 필요",
                    status
            ));
        }

        timelinePanel.revalidate();
        timelinePanel.repaint();
    }

    private String extractNotes(ReservationDetail r) {
        if (r == null) return "-";

        String[] methodNames = {
                "getNotes",
                "getMemo",
                "getRequestNotes",
                "getConsultationNotes"
        };

        for (String methodName : methodNames) {
            try {
                java.lang.reflect.Method method = r.getClass().getMethod(methodName);
                Object value = method.invoke(r);

                if (value != null && !value.toString().isBlank()) {
                    return value.toString();
                }

            } catch (Exception ignored) {
            }
        }

        return "-";
    }

    private String escapeHtml(String text) {
        if (text == null) return "";

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private JPanel buildTimelineRow(String color, String label, String detail) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        row.setOpaque(false);

        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                Color c;

                if ("green".equals(color)) {
                    c = UIConstants.COMPLETED_FG;
                } else if ("blue".equals(color)) {
                    c = UIConstants.CONFIRMED_FG;
                } else {
                    c = UIConstants.BORDER;
                }

                g2.setColor(c);
                g2.fill(new Ellipse2D.Float(0, 0, 10, 10));

                g2.dispose();
            }
        };

        dot.setPreferredSize(new Dimension(10, 10));
        dot.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(UIConstants.f(Font.BOLD, 13));
        lbl.setForeground("gray".equals(color) ? UIConstants.TEXT_MUTED : UIConstants.TEXT_PRIMARY);

        JLabel det = new JLabel(detail);
        det.setFont(UIConstants.f(Font.PLAIN, 11));
        det.setForeground(UIConstants.TEXT_MUTED);

        row.add(dot);
        row.add(lbl);

        if (detail != null && !detail.isBlank()) {
            row.add(det);
        }

        return row;
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

        if (start.length() >= 5) {
            start = start.substring(0, 5);
        }

        if (end.length() >= 5) {
            end = end.substring(0, 5);
        }

        return end.isBlank() ? start : start + " - " + end;
    }

    private String nullToBlank(String value) {
        return value == null ? "" : value;
    }
}
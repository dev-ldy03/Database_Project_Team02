package DB2026Team02.view.student;

import DB2026Team02.model.ConsultationBooth;
import DB2026Team02.model.Department;
import DB2026Team02.model.Professor;
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

public class DepartmentSearchPanel extends JPanel {

    private JTextField searchField;
    private JPanel cardsGrid;
    private JLabel resultCountLabel;

    public DepartmentSearchPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BG);

        add(new NavBar(NavBar.STUDENT), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(buildBody());
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UIConstants.BG);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildBody() {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(UIConstants.BG);

        body.add(buildSearchBar());
        body.add(Box.createVerticalStrut(28));

        cardsGrid = new JPanel(new GridLayout(0, 3, 24, 24));
        cardsGrid.setOpaque(false);
        cardsGrid.setBorder(BorderFactory.createEmptyBorder(0, 80, 40, 80));
        body.add(cardsGrid);

        return body;
    }

    private JPanel buildSearchBar() {
        JPanel bar = new JPanel(new BorderLayout(0, 12));
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(32, 80, 16, 80));
        bar.setMaximumSize(new Dimension(Short.MAX_VALUE, 130));

        JLabel title = new JLabel("학과 / 교수 검색");
        title.setFont(UIConstants.f(Font.BOLD, 22));
        title.setForeground(UIConstants.TEXT_PRIMARY);
        bar.add(title, BorderLayout.NORTH);

        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);

        searchField = new JTextField();
        searchField.setFont(UIConstants.f(Font.PLAIN, 14));
        searchField.setBorder(new CompoundBorder(
                new LineBorder(UIConstants.PRIMARY, 1, true),
                BorderFactory.createEmptyBorder(8, 36, 8, 12)
        ));
        searchField.setBackground(UIConstants.BG);
        searchField.addActionListener(e -> doSearch());

        JPanel fieldWrapper = new JPanel(new BorderLayout());
        fieldWrapper.setOpaque(false);

        JLabel searchIcon = new JLabel("  🔍  ");
        searchIcon.setFont(UIConstants.f(Font.PLAIN, 14));

        fieldWrapper.add(searchIcon, BorderLayout.WEST);
        fieldWrapper.add(searchField, BorderLayout.CENTER);

        GreenButton btnSearch = new GreenButton("검색");
        btnSearch.setPreferredSize(new Dimension(90, 44));
        btnSearch.addActionListener(e -> doSearch());

        row.add(fieldWrapper, BorderLayout.CENTER);
        row.add(btnSearch, BorderLayout.EAST);

        resultCountLabel = new JLabel("");
        resultCountLabel.setFont(UIConstants.f(Font.PLAIN, 13));
        resultCountLabel.setForeground(UIConstants.TEXT_MUTED);

        bar.add(row, BorderLayout.CENTER);
        bar.add(resultCountLabel, BorderLayout.SOUTH);

        return bar;
    }

    private void doSearch() {
        String keyword = searchField.getText().trim();
        cardsGrid.removeAll();

        try {
            StudentService svc = new StudentService();

            List<Department> depts = svc.searchDepartments(keyword);

            for (Department d : depts) {
                cardsGrid.add(buildDeptCard(d, svc));
            }

            if (depts.isEmpty()) {
                resultCountLabel.setText("결과가 없습니다.");
            } else if (keyword.isEmpty()) {
                resultCountLabel.setText("전체 학과 " + depts.size() + "개");
            } else {
                resultCountLabel.setText("검색 결과 " + depts.size() + "개 학과");
            }

        } catch (SQLException ex) {
            resultCountLabel.setText("DB 오류: " + ex.getMessage());
        }

        cardsGrid.revalidate();
        cardsGrid.repaint();
    }

    private JPanel buildDeptCard(Department d, StudentService svc) {
        int professorCount = 0;
        int boothCount = 0;
        String consultationType = "OFFLINE";

        try {
            List<Professor> professors = svc.getProfessorsByDepartment(d.getDepartmentId());
            List<ConsultationBooth> booths = svc.getBoothsByDepartment(d.getDepartmentId());

            professorCount = professors == null ? 0 : professors.size();
            boothCount = booths == null ? 0 : booths.size();
            consultationType = getConsultationTypeFromBooths(booths);

        } catch (SQLException ex) {
            professorCount = 0;
            boothCount = 0;
            consultationType = "OFFLINE";
        }

        final String type = consultationType;

        JPanel card = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(UIConstants.WHITE);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));

                g2.setColor(getTypeColor(type));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), 6, 6, 6));

                g2.dispose();
            }
        };

        card.setOpaque(false);
        card.setPreferredSize(new Dimension(360, 170));

        JLabel icon = new JLabel("🏫");
        icon.setFont(UIConstants.f(Font.PLAIN, 28));
        icon.setBounds(22, 28, 48, 48);
        card.add(icon);

        JLabel name = new JLabel(d.getDepartmentName());
        name.setFont(UIConstants.f(Font.BOLD, 17));
        name.setForeground(UIConstants.TEXT_PRIMARY);
        name.setBounds(78, 26, 250, 26);
        card.add(name);

        JLabel loc = new JLabel("📍 " + (d.getLocation() != null ? d.getLocation() : ""));
        loc.setFont(UIConstants.f(Font.PLAIN, 12));
        loc.setForeground(UIConstants.TEXT_SECONDARY);
        loc.setBounds(78, 54, 250, 20);
        card.add(loc);

        JLabel badge = buildTypeBadge(type);
        badge.setBounds(78, 82, 92, 24);
        card.add(badge);

        JLabel prof = new JLabel("교수 " + professorCount + "명");
        prof.setFont(UIConstants.f(Font.PLAIN, 13));
        prof.setForeground(UIConstants.TEXT_MUTED);
        prof.setBounds(24, 125, 90, 22);
        card.add(prof);

        JLabel booth = new JLabel("부스 " + boothCount + "개");
        booth.setFont(UIConstants.f(Font.PLAIN, 13));
        booth.setForeground(UIConstants.TEXT_MUTED);
        booth.setBounds(120, 125, 90, 22);
        card.add(booth);

        GreenButton btn = new GreenButton("상세 보기");
        btn.setBounds(230, 118, 110, 36);
        btn.addActionListener(e -> {
            MainFrame.setSelectedDept(d.getDepartmentId(), d.getDepartmentName());
            MainFrame.navigate(MainFrame.DEPT_DETAIL);
        });
        card.add(btn);

        return card;
    }

    private String getConsultationTypeFromBooths(List<ConsultationBooth> booths) {
        if (booths == null || booths.isEmpty()) {
            return "OFFLINE";
        }

        boolean hasOnline = false;
        boolean hasOffline = false;
        boolean hasHybrid = false;

        for (ConsultationBooth booth : booths) {
            if (booth == null || booth.getBoothType() == null) {
                continue;
            }

            String type = booth.getBoothType().trim().toUpperCase();

            if ("ONLINE".equals(type)) {
                hasOnline = true;
            } else if ("OFFLINE".equals(type)) {
                hasOffline = true;
            } else if ("HYBRID".equals(type)) {
                hasHybrid = true;
            }
        }

        if (hasHybrid) {
            return "HYBRID";
        }

        if (hasOnline && hasOffline) {
            return "HYBRID";
        }

        if (hasOnline) {
            return "ONLINE";
        }

        return "OFFLINE";
    }

    private JLabel buildTypeBadge(String type) {
        JLabel badge = new JLabel(type, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg;
                if ("ONLINE".equals(type)) {
                    bg = new Color(220, 252, 231);
                } else if ("HYBRID".equals(type)) {
                    bg = new Color(237, 233, 254);
                } else {
                    bg = new Color(219, 234, 254);
                }

                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();

                super.paintComponent(g);
            }
        };

        badge.setOpaque(false);
        badge.setFont(UIConstants.f(Font.BOLD, 12));
        badge.setForeground(getTypeColor(type));

        return badge;
    }

    private Color getTypeColor(String type) {
        if ("ONLINE".equals(type)) {
            return new Color(22, 163, 74);
        }
        if ("HYBRID".equals(type)) {
            return new Color(124, 58, 237);
        }
        return new Color(37, 99, 235);
    }

    public void refresh() {
        searchField.setText("");
        doSearch();
    }
}
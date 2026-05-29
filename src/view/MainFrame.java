package DB2026Team02.view;

import DB2026Team02.model.ReservationDetail;
import DB2026Team02.view.admin.*;
import DB2026Team02.view.student.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public static final String LANDING          = "LANDING";
    public static final String STUDENT_REGISTER = "STUDENT_REGISTER";
    public static final String STUDENT_HOME     = "STUDENT_HOME";
    public static final String DEPT_SEARCH      = "DEPT_SEARCH";
    public static final String DEPT_DETAIL      = "DEPT_DETAIL";
    public static final String MAKE_RESERVATION = "MAKE_RESERVATION";
    public static final String MY_RESERVATIONS  = "MY_RESERVATIONS";
    public static final String RESERVATION_DETAIL = "RESERVATION_DETAIL";

    public static final String ADMIN_DASHBOARD   = "ADMIN_DASHBOARD";
    public static final String ADMIN_RESERVATIONS = "ADMIN_RESERVATIONS";
    public static final String ADMIN_CHECKIN     = "ADMIN_CHECKIN";
    public static final String ADMIN_DEPT        = "ADMIN_DEPT";
    public static final String ADMIN_PROF        = "ADMIN_PROF";
    public static final String ADMIN_BOOTH       = "ADMIN_BOOTH";
    public static final String ADMIN_TIMESLOT    = "ADMIN_TIMESLOT";

    private static MainFrame instance;
    public static MainFrame getInstance() { return instance; }

    private static int    studentId         = -1;
    private static String studentName       = "";
    private static String studentEmail      = "";

    private static int    selectedDeptId    = -1;
    private static String selectedDeptName  = "";
    private static int    selectedSlotId    = -1;
    private static int    selectedProfId    = -1;
    private static int    selectedBoothId   = -1;
    private static String selectedBoothName = "";
    private static String selectedDate      = "";
    private static String selectedTime      = "";
    private static ReservationDetail selectedReservation = null;

    private final CardLayout     cardLayout = new CardLayout();
    private final JPanel         cardPanel  = new JPanel(cardLayout);

    private StudentHomePanel      homePanel;
    private DepartmentSearchPanel searchPanel;
    private DepartmentDetailPanel detailPanel;
    private MakeReservationPanel  reservePanel;
    private MyReservationsPanel   myResPanel;
    private ReservationDetailPanel resDetailPanel;

    private AdminDashboardPanel   adminDashboardPanel;
    private ReservationListPanel  adminResvPanel;
    private CheckInPanel          adminCheckInPanel;
    private DepartmentMgmtPanel   adminDeptPanel;
    private ProfessorMgmtPanel    adminProfPanel;
    private BoothTimeSlotPanel    adminBoothSlotPanel;

    public MainFrame() {
        instance = this;
        setTitle("대학원 박람회 상담 예약 시스템 — 이화여자대학교");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setResizable(false);

        cardPanel.setBackground(Color.WHITE);

        cardPanel.add(new LandingPanel(),         LANDING);
        cardPanel.add(new StudentRegisterPanel(),  STUDENT_REGISTER);

        homePanel      = new StudentHomePanel();
        searchPanel    = new DepartmentSearchPanel();
        detailPanel    = new DepartmentDetailPanel();
        reservePanel   = new MakeReservationPanel();
        myResPanel     = new MyReservationsPanel();
        resDetailPanel = new ReservationDetailPanel();

        cardPanel.add(homePanel,      STUDENT_HOME);
        cardPanel.add(searchPanel,    DEPT_SEARCH);
        cardPanel.add(detailPanel,    DEPT_DETAIL);
        cardPanel.add(reservePanel,   MAKE_RESERVATION);
        cardPanel.add(myResPanel,     MY_RESERVATIONS);
        cardPanel.add(resDetailPanel, RESERVATION_DETAIL);

        adminDashboardPanel  = new AdminDashboardPanel();
        adminResvPanel       = new ReservationListPanel();
        adminCheckInPanel    = new CheckInPanel();
        adminDeptPanel       = new DepartmentMgmtPanel();
        adminProfPanel       = new ProfessorMgmtPanel();
        adminBoothSlotPanel  = new BoothTimeSlotPanel();

        cardPanel.add(adminDashboardPanel,  ADMIN_DASHBOARD);
        cardPanel.add(adminResvPanel,       ADMIN_RESERVATIONS);
        cardPanel.add(adminCheckInPanel,    ADMIN_CHECKIN);
        cardPanel.add(adminDeptPanel,       ADMIN_DEPT);
        cardPanel.add(adminProfPanel,       ADMIN_PROF);
        cardPanel.add(adminBoothSlotPanel,  ADMIN_BOOTH);

        add(cardPanel);
        cardLayout.show(cardPanel, LANDING);
    }

    public static void navigate(String panel) {
        if (instance == null) return;
        try {
            switch (panel) {
                case STUDENT_HOME:
                    instance.homePanel.refresh(); break;
                case DEPT_SEARCH:
                    instance.searchPanel.refresh(); break;
                case DEPT_DETAIL:
                    instance.detailPanel.refresh(); break;
                case MAKE_RESERVATION:
                    instance.reservePanel.refresh(); break;
                case MY_RESERVATIONS:
                    instance.myResPanel.refresh(); break;
                case RESERVATION_DETAIL:
                    instance.resDetailPanel.refresh(); break;
                case ADMIN_DASHBOARD:
                    instance.adminDashboardPanel.refresh(); break;
                case ADMIN_RESERVATIONS:
                    instance.adminResvPanel.refresh(); break;
                case ADMIN_CHECKIN:
                    instance.adminCheckInPanel.refresh(); break;
                case ADMIN_DEPT:
                    instance.adminDeptPanel.refresh(); break;
                case ADMIN_PROF:
                    instance.adminProfPanel.refresh(); break;
                case ADMIN_BOOTH:
                    instance.adminBoothSlotPanel.showBooth(); break;
                case ADMIN_TIMESLOT:
                    instance.adminBoothSlotPanel.showTimeSlot();
                    panel = ADMIN_BOOTH;
                    break;
            }
        } catch (Exception ignored) {}
        instance.cardLayout.show(instance.cardPanel, panel);
    }

    public static int    getStudentId()    { return studentId; }
    public static String getStudentName()  { return studentName; }
    public static String getStudentEmail() { return studentEmail; }

    public static void setStudent(int id, String name, String email) {
        studentId = id; studentName = name; studentEmail = email;
    }

    public static int    getSelectedDeptId()    { return selectedDeptId; }
    public static String getSelectedDeptName()  { return selectedDeptName; }
    public static int    getSelectedSlotId()    { return selectedSlotId; }
    public static int    getSelectedProfId()    { return selectedProfId; }
    public static int    getSelectedBoothId()   { return selectedBoothId; }
    public static String getSelectedBoothName() { return selectedBoothName; }
    public static String getSelectedDate()      { return selectedDate; }
    public static String getSelectedTime()      { return selectedTime; }
    public static ReservationDetail getSelectedReservation() { return selectedReservation; }

    public static void setSelectedDept(int id, String name) {
        selectedDeptId = id; selectedDeptName = name;
    }

    public static void setSelectedSlot(int slotId, int profId, int boothId,
                                       String boothName, String date, String time) {
        selectedSlotId   = slotId;
        selectedProfId   = profId;
        selectedBoothId  = boothId;
        selectedBoothName = boothName;
        selectedDate     = date;
        selectedTime     = time;
    }

    public static void setSelectedReservation(ReservationDetail rd) {
        selectedReservation = rd;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new MainFrame().setVisible(true);
        });
    }
}

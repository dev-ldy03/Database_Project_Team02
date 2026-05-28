package DB2026Team02.service;

import DB2026Team02.dao.AdminDAO;
import DB2026Team02.db.DatabaseConnection;
import DB2026Team02.model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AdminService {

    private final AdminDAO adminDAO;

    public AdminService() {
        this.adminDAO = new AdminDAO();
    }

    // =========================================================================
    // DEPARTMENT
    // =========================================================================

    public List<Department> getAllDepartments() throws SQLException {
        return adminDAO.findAllDepartments();
    }

    public int addDepartment(String name, String location) throws SQLException {
        Department dept = new Department(name, location);
        int id = adminDAO.insertDepartment(dept);
        if (id == -1) System.out.println("학과 추가에 실패했습니다.");
        return id;
    }

    public boolean updateDepartment(int id, String name, String location) throws SQLException {
        Department dept = new Department(name, location);
        dept.setDepartmentId(id);
        boolean success = adminDAO.updateDepartment(dept);
        if (!success) System.out.println("해당 학과를 찾을 수 없습니다.");
        return success;
    }

    public boolean deleteDepartment(int id) throws SQLException {
        boolean success = adminDAO.deleteDepartment(id);
        if (!success) System.out.println("해당 학과를 찾을 수 없습니다.");
        return success;
    }

    // =========================================================================
    // PROFESSOR
    // =========================================================================

    public List<Professor> getAllProfessors() throws SQLException {
        return adminDAO.findAllProfessors();
    }

    public int addProfessor(int departmentId, String name, String email, String phone) throws SQLException {
        Professor prof = new Professor(departmentId, name, email, phone);
        int id = adminDAO.insertProfessor(prof);
        if (id == -1) System.out.println("교수 추가에 실패했습니다.");
        return id;
    }

    public boolean updateProfessor(int id, int departmentId, String name, String email, String phone) throws SQLException {
        Professor prof = new Professor(departmentId, name, email, phone);
        prof.setProfessorId(id);
        boolean success = adminDAO.updateProfessor(prof);
        if (!success) System.out.println("해당 교수를 찾을 수 없습니다.");
        return success;
    }

    public boolean deleteProfessor(int id) throws SQLException {
        boolean success = adminDAO.deleteProfessor(id);
        if (!success) System.out.println("해당 교수를 찾을 수 없습니다.");
        return success;
    }

    // =========================================================================
    // CONSULTATION_BOOTH
    // =========================================================================

    public List<ConsultationBooth> getAllBooths() throws SQLException {
        return adminDAO.findAllBooths();
    }

    public int addBooth(int departmentId, String name, String boothType, int capacity) throws SQLException {
        ConsultationBooth booth = new ConsultationBooth(departmentId, name, boothType, capacity);
        int id = adminDAO.insertBooth(booth);
        if (id == -1) System.out.println("부스 추가에 실패했습니다.");
        return id;
    }

    public boolean updateBooth(int id, int departmentId, String name, String boothType, int capacity) throws SQLException {
        ConsultationBooth booth = new ConsultationBooth(departmentId, name, boothType, capacity);
        booth.setBoothId(id);
        boolean success = adminDAO.updateBooth(booth);
        if (!success) System.out.println("해당 부스를 찾을 수 없습니다.");
        return success;
    }

    public boolean deleteBooth(int id) throws SQLException {
        boolean success = adminDAO.deleteBooth(id);
        if (!success) System.out.println("해당 부스를 찾을 수 없습니다.");
        return success;
    }

    // =========================================================================
    // TIME_SLOT
    // =========================================================================

    public List<TimeSlot> getAllTimeSlots() throws SQLException {
        return adminDAO.findAllTimeSlots();
    }

    public int addTimeSlot(int boothId, java.sql.Date slotDate,
                           java.sql.Time startTime, java.sql.Time endTime,
                           int maxReservations) throws SQLException {
        TimeSlot slot = new TimeSlot();
        slot.setBoothId(boothId);
        slot.setSlotDate(slotDate);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setMaxReservations(maxReservations);
        int id = adminDAO.insertTimeSlot(slot);
        if (id == -1) System.out.println("시간대 추가에 실패했습니다.");
        return id;
    }

    public boolean updateTimeSlot(int id, int boothId, java.sql.Date slotDate,
                                  java.sql.Time startTime, java.sql.Time endTime,
                                  int maxReservations) throws SQLException {
        TimeSlot slot = new TimeSlot();
        slot.setSlotId(id);
        slot.setBoothId(boothId);
        slot.setSlotDate(slotDate);
        slot.setStartTime(startTime);
        slot.setEndTime(endTime);
        slot.setMaxReservations(maxReservations);
        boolean success = adminDAO.updateTimeSlot(slot);
        if (!success) System.out.println("해당 시간대를 찾을 수 없습니다.");
        return success;
    }

    public boolean deleteTimeSlot(int id) throws SQLException {
        boolean success = adminDAO.deleteTimeSlot(id);
        if (!success) System.out.println("해당 시간대를 찾을 수 없습니다.");
        return success;
    }

    // =========================================================================
    // 예약 현황 조회
    // =========================================================================

    public List<ReservationDetail> getAllReservationDetails() throws SQLException {
        return adminDAO.findAllReservationDetails();
    }

    public List<ReservationDetail> getReservationDetailsByStatus(String status) throws SQLException {
        return adminDAO.findReservationDetailsByStatus(status);
    }

    // =========================================================================
    // CHECK-IN 처리 (트랜잭션)
    // =========================================================================

    public boolean processCheckIn(int reservationId) throws SQLException {
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Reservation reservation = adminDAO.findReservationById(conn, reservationId);

            if (reservation == null) {
                conn.rollback();
                System.out.println("해당 예약을 찾을 수 없습니다.");
                return false;
            }

            if (!"CONFIRMED".equals(reservation.getStatus()) && !"PENDING".equals(reservation.getStatus())) {
                conn.rollback();
                System.out.println("체크인 처리 불가 - 현재 상태: " + reservation.getStatus());
                return false;
            }

            boolean success = adminDAO.processCheckIn(conn, reservationId);

            if (!success) {
                conn.rollback();
                System.out.println("체크인 처리에 실패했습니다.");
                return false;
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;

        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
                conn.close();
            }
        }
    }

    public boolean processCheckOut(int reservationId) throws SQLException {
        boolean success = adminDAO.processCheckOut(reservationId);
        if (!success) System.out.println("체크아웃 처리 불가 - 이미 체크아웃되었거나 체크인 기록이 없습니다.");
        return success;
    }

    // =========================================================================
    // NO-SHOW 관리 (트랜잭션)
    // =========================================================================

    // 노쇼 처리 대상 목록 조회 (상담 시간이 지난 미체크인 예약)
    public List<ReservationDetail> getNoShowCandidates() throws SQLException {
        return adminDAO.findNoShowCandidates();
    }

    // 특정 예약을 노쇼로 처리
    public boolean processNoShow(int reservationId) throws SQLException {
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Reservation reservation = adminDAO.findReservationById(conn, reservationId);

            if (reservation == null) {
                conn.rollback();
                System.out.println("해당 예약을 찾을 수 없습니다.");
                return false;
            }

            if (!"CONFIRMED".equals(reservation.getStatus()) && !"PENDING".equals(reservation.getStatus())) {
                conn.rollback();
                System.out.println("노쇼 처리 불가 - 현재 상태: " + reservation.getStatus());
                return false;
            }

            boolean success = adminDAO.markAsNoShow(conn, reservationId);

            if (!success) {
                conn.rollback();
                System.out.println("노쇼 처리에 실패했습니다.");
                return false;
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;

        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
                conn.close();
            }
        }
    }

    // 노쇼 대상 전체 일괄 처리
    public int processAllNoShows() throws SQLException {
        List<ReservationDetail> candidates = adminDAO.findNoShowCandidates();
        int count = 0;

        for (ReservationDetail detail : candidates) {
            if (processNoShow(detail.getReservationId())) {
                count++;
            }
        }

        System.out.println("노쇼 처리 완료: " + count + "건");
        return count;
    }

    // =========================================================================
    // 통계 조회
    // =========================================================================

    public List<String[]> getReservationStatsByStatus() throws SQLException {
        return adminDAO.getReservationStatsByStatus();
    }

    public List<String[]> getReservationStatsByDepartment() throws SQLException {
        return adminDAO.getReservationStatsByDepartment();
    }

    public List<String[]> getBoothScheduleStats() throws SQLException {
        return adminDAO.getBoothScheduleStats();
    }

    public int[] getOverallStats() throws SQLException {
        return adminDAO.getOverallStats();
    }
}

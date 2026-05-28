package DB2026Team02.dao;

import DB2026Team02.db.DatabaseConnection;
import DB2026Team02.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    // =========================================================================
    // DEPARTMENT
    // =========================================================================

    public List<Department> findAllDepartments() throws SQLException {
        String sql = "SELECT department_id, department_name, location FROM DEPARTMENT ORDER BY department_id";
        List<Department> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Department dept = new Department();
                dept.setDepartmentId(rs.getInt("department_id"));
                dept.setDepartmentName(rs.getString("department_name"));
                dept.setLocation(rs.getString("location"));
                list.add(dept);
            }
        }
        return list;
    }

    public int insertDepartment(Department dept) throws SQLException {
        String sql = "INSERT INTO DEPARTMENT (department_name, location) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, dept.getDepartmentName());
            pstmt.setString(2, dept.getLocation());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public boolean updateDepartment(Department dept) throws SQLException {
        String sql = "UPDATE DEPARTMENT SET department_name = ?, location = ? WHERE department_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dept.getDepartmentName());
            pstmt.setString(2, dept.getLocation());
            pstmt.setInt(3, dept.getDepartmentId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteDepartment(int departmentId) throws SQLException {
        String sql = "DELETE FROM DEPARTMENT WHERE department_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, departmentId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // PROFESSOR
    // =========================================================================

    public List<Professor> findAllProfessors() throws SQLException {
        String sql = "SELECT professor_id, department_id, professor_name, email, phone FROM PROFESSOR ORDER BY professor_id";
        List<Professor> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Professor prof = new Professor();
                prof.setProfessorId(rs.getInt("professor_id"));
                prof.setDepartmentId(rs.getInt("department_id"));
                prof.setProfessorName(rs.getString("professor_name"));
                prof.setEmail(rs.getString("email"));
                prof.setPhone(rs.getString("phone"));
                list.add(prof);
            }
        }
        return list;
    }

    public int insertProfessor(Professor prof) throws SQLException {
        String sql = "INSERT INTO PROFESSOR (department_id, professor_name, email, phone) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, prof.getDepartmentId());
            pstmt.setString(2, prof.getProfessorName());
            pstmt.setString(3, prof.getEmail());
            pstmt.setString(4, prof.getPhone());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public boolean updateProfessor(Professor prof) throws SQLException {
        String sql = """
                UPDATE PROFESSOR
                SET department_id = ?, professor_name = ?, email = ?, phone = ?
                WHERE professor_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, prof.getDepartmentId());
            pstmt.setString(2, prof.getProfessorName());
            pstmt.setString(3, prof.getEmail());
            pstmt.setString(4, prof.getPhone());
            pstmt.setInt(5, prof.getProfessorId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteProfessor(int professorId) throws SQLException {
        String sql = "DELETE FROM PROFESSOR WHERE professor_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, professorId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // CONSULTATION_BOOTH
    // =========================================================================

    public List<ConsultationBooth> findAllBooths() throws SQLException {
        String sql = "SELECT booth_id, department_id, booth_name, booth_type, capacity FROM CONSULTATION_BOOTH ORDER BY booth_id";
        List<ConsultationBooth> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ConsultationBooth booth = new ConsultationBooth();
                booth.setBoothId(rs.getInt("booth_id"));
                booth.setDepartmentId(rs.getInt("department_id"));
                booth.setBoothName(rs.getString("booth_name"));
                booth.setBoothType(rs.getString("booth_type"));
                booth.setCapacity(rs.getInt("capacity"));
                list.add(booth);
            }
        }
        return list;
    }

    public int insertBooth(ConsultationBooth booth) throws SQLException {
        String sql = "INSERT INTO CONSULTATION_BOOTH (department_id, booth_name, booth_type, capacity) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, booth.getDepartmentId());
            pstmt.setString(2, booth.getBoothName());
            pstmt.setString(3, booth.getBoothType());
            pstmt.setInt(4, booth.getCapacity());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public boolean updateBooth(ConsultationBooth booth) throws SQLException {
        String sql = """
                UPDATE CONSULTATION_BOOTH
                SET department_id = ?, booth_name = ?, booth_type = ?, capacity = ?
                WHERE booth_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, booth.getDepartmentId());
            pstmt.setString(2, booth.getBoothName());
            pstmt.setString(3, booth.getBoothType());
            pstmt.setInt(4, booth.getCapacity());
            pstmt.setInt(5, booth.getBoothId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteBooth(int boothId) throws SQLException {
        String sql = "DELETE FROM CONSULTATION_BOOTH WHERE booth_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, boothId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // TIME_SLOT
    // =========================================================================

    public List<TimeSlot> findAllTimeSlots() throws SQLException {
        String sql = """
                SELECT slot_id, booth_id, slot_date, start_time, end_time, max_reservations
                FROM TIME_SLOT
                ORDER BY slot_date, start_time
                """;
        List<TimeSlot> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                TimeSlot slot = new TimeSlot();
                slot.setSlotId(rs.getInt("slot_id"));
                slot.setBoothId(rs.getInt("booth_id"));
                slot.setSlotDate(rs.getDate("slot_date"));
                slot.setStartTime(rs.getTime("start_time"));
                slot.setEndTime(rs.getTime("end_time"));
                slot.setMaxReservations(rs.getInt("max_reservations"));
                list.add(slot);
            }
        }
        return list;
    }

    public int insertTimeSlot(TimeSlot slot) throws SQLException {
        String sql = "INSERT INTO TIME_SLOT (booth_id, slot_date, start_time, end_time, max_reservations) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, slot.getBoothId());
            pstmt.setDate(2, slot.getSlotDate());
            pstmt.setTime(3, slot.getStartTime());
            pstmt.setTime(4, slot.getEndTime());
            pstmt.setInt(5, slot.getMaxReservations());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public boolean updateTimeSlot(TimeSlot slot) throws SQLException {
        String sql = """
                UPDATE TIME_SLOT
                SET booth_id = ?, slot_date = ?, start_time = ?, end_time = ?, max_reservations = ?
                WHERE slot_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, slot.getBoothId());
            pstmt.setDate(2, slot.getSlotDate());
            pstmt.setTime(3, slot.getStartTime());
            pstmt.setTime(4, slot.getEndTime());
            pstmt.setInt(5, slot.getMaxReservations());
            pstmt.setInt(6, slot.getSlotId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteTimeSlot(int slotId) throws SQLException {
        String sql = "DELETE FROM TIME_SLOT WHERE slot_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, slotId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // 예약 현황 조회 (v_reservation_detail 뷰 활용)
    // =========================================================================

    public List<ReservationDetail> findAllReservationDetails() throws SQLException {
        String sql = """
                SELECT reservation_id, status, created_at,
                       student_id, student_name, student_email,
                       department_name, professor_name,
                       booth_name, booth_type,
                       slot_date, start_time, end_time
                FROM v_reservation_detail
                ORDER BY slot_date, start_time
                """;
        return queryReservationDetails(sql);
    }

    public List<ReservationDetail> findReservationDetailsByStatus(String status) throws SQLException {
        String sql = """
                SELECT reservation_id, status, created_at,
                       student_id, student_name, student_email,
                       department_name, professor_name,
                       booth_name, booth_type,
                       slot_date, start_time, end_time
                FROM v_reservation_detail
                WHERE status = ?
                ORDER BY slot_date, start_time
                """;
        List<ReservationDetail> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapReservationDetail(rs));
                }
            }
        }
        return list;
    }

    private List<ReservationDetail> queryReservationDetails(String sql) throws SQLException {
        List<ReservationDetail> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapReservationDetail(rs));
            }
        }
        return list;
    }

    private ReservationDetail mapReservationDetail(ResultSet rs) throws SQLException {
        ReservationDetail detail = new ReservationDetail();
        detail.setReservationId(rs.getInt("reservation_id"));
        detail.setStatus(rs.getString("status"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            detail.setCreatedAt(createdAt.toLocalDateTime());
        }

        detail.setStudentId(rs.getInt("student_id"));
        detail.setStudentName(rs.getString("student_name"));
        detail.setStudentEmail(rs.getString("student_email"));
        detail.setDepartmentName(rs.getString("department_name"));
        detail.setProfessorName(rs.getString("professor_name"));
        detail.setBoothName(rs.getString("booth_name"));
        detail.setBoothType(rs.getString("booth_type"));
        detail.setSlotDate(rs.getDate("slot_date"));
        detail.setStartTime(rs.getTime("start_time"));
        detail.setEndTime(rs.getTime("end_time"));
        return detail;
    }

    // =========================================================================
    // CHECK-IN 처리
    // =========================================================================

    public boolean processCheckIn(Connection conn, int reservationId) throws SQLException {
        // 체크인 기록 생성
        String insertSql = "INSERT INTO CHECK_IN_RECORD (reservation_id) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, reservationId);
            if (pstmt.executeUpdate() == 0) return false;
        }

        // 예약 상태를 COMPLETED로 변경
        String updateSql = "UPDATE RESERVATION SET status = 'COMPLETED' WHERE reservation_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            pstmt.setInt(1, reservationId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean processCheckOut(int reservationId) throws SQLException {
        String sql = """
                UPDATE CHECK_IN_RECORD
                SET check_out_time = NOW()
                WHERE reservation_id = ? AND check_out_time IS NULL
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reservationId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // 예약 기본 정보 조회 (체크인/노쇼 처리용)
    public Reservation findReservationById(Connection conn, int reservationId) throws SQLException {
        String sql = """
                SELECT reservation_id, student_id, slot_id, professor_id,
                       booth_id, status, created_at, notes
                FROM RESERVATION
                WHERE reservation_id = ?
                FOR UPDATE
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Reservation r = new Reservation();
                    r.setReservationId(rs.getInt("reservation_id"));
                    r.setStudentId(rs.getInt("student_id"));
                    r.setSlotId(rs.getInt("slot_id"));
                    r.setProfessorId(rs.getInt("professor_id"));
                    r.setBoothId(rs.getInt("booth_id"));
                    r.setStatus(rs.getString("status"));

                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        r.setCreatedAt(createdAt.toLocalDateTime());
                    }
                    r.setNotes(rs.getString("notes"));
                    return r;
                }
            }
        }
        return null;
    }

    // 예약 확정 (PENDING → CONFIRMED)
    public boolean confirmReservation(Connection conn, int reservationId) throws SQLException {
        String sql = """
                UPDATE RESERVATION
                SET status = 'CONFIRMED'
                WHERE reservation_id = ?
                  AND status = 'PENDING'
                """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // NO-SHOW 관리
    // =========================================================================

    // 시간이 지났지만 체크인 기록 없는 CONFIRMED/PENDING 예약 조회
    public List<ReservationDetail> findNoShowCandidates() throws SQLException {
        String sql = """
                SELECT v.reservation_id, v.status, v.created_at,
                       v.student_id, v.student_name, v.student_email,
                       v.department_name, v.professor_name,
                       v.booth_name, v.booth_type,
                       v.slot_date, v.start_time, v.end_time
                FROM v_reservation_detail v
                WHERE v.status IN ('PENDING', 'CONFIRMED')
                  AND TIMESTAMP(v.slot_date, v.end_time) < NOW()
                  AND NOT EXISTS (
                      SELECT 1 FROM CHECK_IN_RECORD c
                      WHERE c.reservation_id = v.reservation_id
                  )
                ORDER BY v.slot_date, v.start_time
                """;
        return queryReservationDetails(sql);
    }

    public boolean markAsNoShow(Connection conn, int reservationId) throws SQLException {
        String sql = """
                UPDATE RESERVATION
                SET status = 'CANCELLED'
                WHERE reservation_id = ?
                  AND status IN ('PENDING', 'CONFIRMED')
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // =========================================================================
    // 통계 조회
    // =========================================================================

    // 상태별 예약 건수
    public List<String[]> getReservationStatsByStatus() throws SQLException {
        String sql = """
                SELECT status, COUNT(*) AS cnt
                FROM RESERVATION
                GROUP BY status
                ORDER BY status
                """;
        List<String[]> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                result.add(new String[]{rs.getString("status"), String.valueOf(rs.getInt("cnt"))});
            }
        }
        return result;
    }

    // 학과별 예약 건수
    public List<String[]> getReservationStatsByDepartment() throws SQLException {
        String sql = """
                SELECT d.department_name,
                       COUNT(r.reservation_id) AS total,
                       SUM(CASE WHEN r.status = 'CONFIRMED'  THEN 1 ELSE 0 END) AS confirmed,
                       SUM(CASE WHEN r.status = 'COMPLETED'  THEN 1 ELSE 0 END) AS completed,
                       SUM(CASE WHEN r.status = 'CANCELLED'  THEN 1 ELSE 0 END) AS cancelled,
                       SUM(CASE WHEN r.status = 'PENDING'    THEN 1 ELSE 0 END) AS pending
                FROM DEPARTMENT d
                LEFT JOIN PROFESSOR p  ON d.department_id = p.department_id
                LEFT JOIN RESERVATION r ON p.professor_id = r.professor_id
                GROUP BY d.department_id, d.department_name
                ORDER BY total DESC
                """;
        List<String[]> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                result.add(new String[]{
                        rs.getString("department_name"),
                        String.valueOf(rs.getInt("total")),
                        String.valueOf(rs.getInt("confirmed")),
                        String.valueOf(rs.getInt("completed")),
                        String.valueOf(rs.getInt("cancelled")),
                        String.valueOf(rs.getInt("pending"))
                });
            }
        }
        return result;
    }

    // 부스별 예약 현황 (v_booth_schedule 뷰 활용)
    public List<String[]> getBoothScheduleStats() throws SQLException {
        String sql = """
                SELECT booth_name, department_name, slot_date, start_time, end_time,
                       max_reservations, current_reservations
                FROM v_booth_schedule
                ORDER BY slot_date, start_time, booth_name
                """;
        List<String[]> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                result.add(new String[]{
                        rs.getString("booth_name"),
                        rs.getString("department_name"),
                        String.valueOf(rs.getDate("slot_date")),
                        String.valueOf(rs.getTime("start_time")),
                        String.valueOf(rs.getTime("end_time")),
                        String.valueOf(rs.getInt("max_reservations")),
                        String.valueOf(rs.getInt("current_reservations"))
                });
            }
        }
        return result;
    }

    // 전체 통계 요약
    public int[] getOverallStats() throws SQLException {
        String sql = """
                SELECT
                    COUNT(*) AS total,
                    SUM(CASE WHEN status = 'CONFIRMED'  THEN 1 ELSE 0 END) AS confirmed,
                    SUM(CASE WHEN status = 'COMPLETED'  THEN 1 ELSE 0 END) AS completed,
                    SUM(CASE WHEN status = 'CANCELLED'  THEN 1 ELSE 0 END) AS cancelled,
                    SUM(CASE WHEN status = 'PENDING'    THEN 1 ELSE 0 END) AS pending
                FROM RESERVATION
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return new int[]{
                        rs.getInt("total"),
                        rs.getInt("confirmed"),
                        rs.getInt("completed"),
                        rs.getInt("cancelled"),
                        rs.getInt("pending")
                };
            }
        }
        return new int[]{0, 0, 0, 0, 0};
    }
}

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


    public boolean deleteDepartmentCascade(Connection conn, int departmentId) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement(
                "DELETE FROM RESERVATION WHERE professor_id IN " +
                "(SELECT professor_id FROM PROFESSOR WHERE department_id = ?) " +
                "OR booth_id IN " +
                "(SELECT booth_id FROM CONSULTATION_BOOTH WHERE department_id = ?)")) {
            p.setInt(1, departmentId);
            p.setInt(2, departmentId);
            p.executeUpdate();
        }

        try (PreparedStatement p = conn.prepareStatement(
                "DELETE FROM PROFESSOR WHERE department_id = ?")) {
            p.setInt(1, departmentId);
            p.executeUpdate();
        }

        try (PreparedStatement p = conn.prepareStatement(
                "DELETE FROM CONSULTATION_BOOTH WHERE department_id = ?")) {
            p.setInt(1, departmentId);
            p.executeUpdate();
        }

        try (PreparedStatement p = conn.prepareStatement(
                "DELETE FROM DEPARTMENT WHERE department_id = ?")) {
            p.setInt(1, departmentId);
            return p.executeUpdate() > 0;
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

    public boolean deleteProfessorCascade(Connection conn, int professorId) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement(
                "DELETE cir FROM CHECK_IN_RECORD cir " +
                "JOIN RESERVATION r ON cir.reservation_id = r.reservation_id " +
                "WHERE r.professor_id = ?")) {
            p.setInt(1, professorId);
            p.executeUpdate();
        }
        try (PreparedStatement p = conn.prepareStatement(
                "DELETE FROM RESERVATION WHERE professor_id = ?")) {
            p.setInt(1, professorId);
            p.executeUpdate();
        }
        try (PreparedStatement p = conn.prepareStatement(
                "DELETE FROM PROFESSOR WHERE professor_id = ?")) {
            p.setInt(1, professorId);
            return p.executeUpdate() > 0;
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

    public int getBoothCapacity(int boothId) throws SQLException {
        String sql = "SELECT capacity FROM CONSULTATION_BOOTH WHERE booth_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, boothId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("capacity");
                }
            }
        }

        return -1;
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

    public boolean deleteBoothCascade(Connection conn, int boothId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "DELETE cir FROM CHECK_IN_RECORD cir " +
                        "JOIN RESERVATION r ON cir.reservation_id = r.reservation_id " +
                        "WHERE r.booth_id = ?")) {
            pstmt.setInt(1, boothId);
            pstmt.executeUpdate();
        }

        try (PreparedStatement pstmt = conn.prepareStatement(
                "DELETE FROM RESERVATION WHERE booth_id = ?")) {
            pstmt.setInt(1, boothId);
            pstmt.executeUpdate();
        }

        try (PreparedStatement pstmt = conn.prepareStatement(
                "DELETE FROM TIME_SLOT WHERE booth_id = ?")) {
            pstmt.setInt(1, boothId);
            pstmt.executeUpdate();
        }

        try (PreparedStatement pstmt = conn.prepareStatement(
                "DELETE FROM CONSULTATION_BOOTH WHERE booth_id = ?")) {
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

    public boolean existsOverlappingTimeSlot(int boothId, java.sql.Date date,
                                              java.sql.Time startTime, java.sql.Time endTime,
                                              int excludeSlotId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TIME_SLOT " +
                     "WHERE booth_id = ? AND slot_date = ? AND slot_id != ? " +
                     "AND start_time < ? AND end_time > ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setInt(1, boothId);
            p.setDate(2, date);
            p.setInt(3, excludeSlotId);
            p.setTime(4, endTime);
            p.setTime(5, startTime);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
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
                       student_major, department_name, professor_name,
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
                       student_major, department_name, professor_name,
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
        detail.setStudentMajor(rs.getString("student_major"));
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
        String insertSql = "INSERT INTO CHECK_IN_RECORD (reservation_id) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setInt(1, reservationId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean processCheckOut(Connection conn, int reservationId) throws SQLException {
        String updateCheckIn = "UPDATE CHECK_IN_RECORD SET check_out_time = NOW() WHERE reservation_id = ? AND check_out_time IS NULL";
        try (PreparedStatement pstmt = conn.prepareStatement(updateCheckIn)) {
            pstmt.setInt(1, reservationId);
            if (pstmt.executeUpdate() == 0) return false;
        }
        String updateStatus = "UPDATE RESERVATION SET status = 'COMPLETED' WHERE reservation_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateStatus)) {
            pstmt.setInt(1, reservationId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<ReservationDetail> findConfirmedNotCheckedIn() throws SQLException {
        String sql = """
                SELECT reservation_id, status, created_at,
                       student_id, student_name, student_email,
                       student_major, department_name, professor_name,
                       booth_name, booth_type, slot_date, start_time, end_time
                FROM v_reservation_detail
                WHERE status = 'CONFIRMED'
                AND reservation_id NOT IN (SELECT reservation_id FROM CHECK_IN_RECORD)
                ORDER BY slot_date, start_time
                """;
        return queryReservationDetails(sql);
    }

    public List<ReservationDetail> findCheckedInNoCheckout() throws SQLException {
        String sql = """
                SELECT v.reservation_id, v.status, v.created_at,
                       v.student_id, v.student_name, v.student_email,
                       v.student_major, v.department_name, v.professor_name,
                       v.booth_name, v.booth_type, v.slot_date, v.start_time, v.end_time
                FROM v_reservation_detail v
                JOIN CHECK_IN_RECORD c ON v.reservation_id = c.reservation_id
                WHERE v.status = 'CONFIRMED'
                AND c.check_out_time IS NULL
                ORDER BY v.slot_date, v.start_time
                """;
        return queryReservationDetails(sql);
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
                       v.student_major, v.department_name, v.professor_name,
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

    // =========================================================================
    // ONLINE_LINK
    // =========================================================================

    public int insertOnlineLink(int reservationId, String meetingUrl, String password, String expiresAt)
            throws SQLException {
        String sql = """
                INSERT INTO ONLINE_LINK (reservation_id, meeting_url, meeting_password, expires_at)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, reservationId);
            pstmt.setString(2, meetingUrl);
            pstmt.setString(3, password);

            Timestamp expires = parseExpiresAt(expiresAt);
            if (expires == null) {
                pstmt.setNull(4, Types.TIMESTAMP);
            } else {
                pstmt.setTimestamp(4, expires);
            }

            int affected = pstmt.executeUpdate();
            if (affected == 0) {
                return -1;
            }

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public OnlineLink findOnlineLinkByReservationId(int reservationId) throws SQLException {
        String sql = """
                SELECT link_id, reservation_id, meeting_url, meeting_password, expires_at
                FROM ONLINE_LINK
                WHERE reservation_id = ?
                ORDER BY link_id DESC
                LIMIT 1
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reservationId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapOnlineLink(rs);
                }
            }
        }
        return null;
    }

    public boolean isOnlineOrHybridReservation(int reservationId) throws SQLException {
        String sql = """
                SELECT b.booth_type
                FROM RESERVATION r
                JOIN CONSULTATION_BOOTH b ON r.booth_id = b.booth_id
                WHERE r.reservation_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reservationId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String boothType = rs.getString("booth_type");
                    return "ONLINE".equals(boothType) || "HYBRID".equals(boothType);
                }
            }
        }
        return false;
    }

    private OnlineLink mapOnlineLink(ResultSet rs) throws SQLException {
        OnlineLink link = new OnlineLink();
        link.setLinkId(rs.getInt("link_id"));
        link.setReservationId(rs.getInt("reservation_id"));
        link.setMeetingUrl(rs.getString("meeting_url"));
        link.setMeetingPassword(rs.getString("meeting_password"));

        Timestamp expiresAt = rs.getTimestamp("expires_at");
        if (expiresAt != null) {
            link.setExpiresAt(expiresAt.toLocalDateTime());
        }
        return link;
    }

    private Timestamp parseExpiresAt(String expiresAt) {
        if (expiresAt == null || expiresAt.isBlank()) {
            return null;
        }
        String normalized = expiresAt.trim().replace('T', ' ');
        if (normalized.length() == 16) {
            normalized += ":00";
        }
        return Timestamp.valueOf(normalized);
    }
}

package DB2026Team02.dao;

import DB2026Team02.db.DatabaseConnection;
import DB2026Team02.model.ConsultationBooth;
import DB2026Team02.model.Department;
import DB2026Team02.model.Professor;
import DB2026Team02.model.ReservationDetail;
import DB2026Team02.model.Student;
import DB2026Team02.model.TimeSlot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // STUDENT (학생 정보 등록/조회)
    // 이메일로 학생 조회 (등록 전 중복 확인용)
    public Student findByEmail(String email) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return findByEmail(conn, email);
        }
    }

    // 이메일로 학생 조회 (트랜잭션용)
    public Student findByEmail(Connection conn, String email) throws SQLException {
        String sql = """
                SELECT student_id, student_name, email, phone, major
                FROM STUDENT
                WHERE email = ?
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        }

        return null;
    }

    // 학생 ID로 조회
    public Student findById(int studentId) throws SQLException {
        String sql = """
                SELECT student_id, student_name, email, phone, major
                FROM STUDENT
                WHERE student_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        }

        return null;
    }

    // 학생 등록
    public int insertStudent(Student student) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return insertStudent(conn, student);
        }
    }

    // 학생 등록 (트랜잭션용)
    public int insertStudent(Connection conn, Student student) throws SQLException {
        String sql = """
                INSERT INTO STUDENT (student_name, email, phone, major)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, student.getStudentName());
            pstmt.setString(2, student.getEmail());
            pstmt.setString(3, student.getPhone());
            pstmt.setString(4, student.getMajor());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
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

    private Student mapStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setStudentName(rs.getString("student_name"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setMajor(rs.getString("major"));
        return student;
    }

    // 상담 정보 검색 (학과/교수/부스)
    // 학과명 키워드로 학과 검색
    public List<Department> findDepartmentsByKeyword(String keyword) throws SQLException {
        String sql = """
                SELECT department_id, department_name, location
                FROM DEPARTMENT
                WHERE department_name LIKE ?
                ORDER BY department_name
                """;
        List<Department> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Department dept = new Department();
                    dept.setDepartmentId(rs.getInt("department_id"));
                    dept.setDepartmentName(rs.getString("department_name"));
                    dept.setLocation(rs.getString("location"));
                    list.add(dept);
                }
            }
        }

        return list;
    }

    // 특정 학과 소속 교수 목록 조회
    public List<Professor> findProfessorsByDepartment(int departmentId) throws SQLException {
        String sql = """
                SELECT professor_id, department_id, professor_name, email, phone
                FROM PROFESSOR
                WHERE department_id = ?
                ORDER BY professor_name
                """;
        List<Professor> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, departmentId);

            try (ResultSet rs = pstmt.executeQuery()) {
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
        }

        return list;
    }

    // 특정 학과 소속 상담 부스 목록 조회
    public List<ConsultationBooth> findBoothsByDepartment(int departmentId) throws SQLException {
        String sql = """
                SELECT booth_id, department_id, booth_name, booth_type, capacity
                FROM CONSULTATION_BOOTH
                WHERE department_id = ?
                ORDER BY booth_name
                """;
        List<ConsultationBooth> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, departmentId);

            try (ResultSet rs = pstmt.executeQuery()) {
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
        }

        return list;
    }

    // 상담 가능 시간 조회 (v_booth_schedule 뷰 활용)
    // 특정 학과·날짜에서 예약 가능한 시간대 조회
    public List<TimeSlot> findAvailableSlotsByDepartment(int departmentId, Date slotDate) throws SQLException {
        String sql = """
                SELECT vs.slot_id, vs.booth_id, vs.slot_date, vs.start_time, vs.end_time,
                       vs.max_reservations
                FROM v_booth_schedule vs
                JOIN CONSULTATION_BOOTH b ON vs.booth_id = b.booth_id
                WHERE b.department_id = ?
                  AND vs.slot_date = ?
                  AND vs.current_reservations < vs.max_reservations
                ORDER BY vs.slot_date, vs.start_time
                """;
        List<TimeSlot> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, departmentId);
            pstmt.setDate(2, slotDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTimeSlot(rs));
                }
            }
        }

        return list;
    }

    // 특정 부스의 예약 가능한 시간대 조회
    public List<TimeSlot> findAvailableSlotsByBooth(int boothId) throws SQLException {
        String sql = """
                SELECT slot_id, booth_id, slot_date, start_time, end_time,
                       max_reservations
                FROM v_booth_schedule
                WHERE booth_id = ?
                  AND current_reservations < max_reservations
                ORDER BY slot_date, start_time
                """;
        List<TimeSlot> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, boothId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTimeSlot(rs));
                }
            }
        }

        return list;
    }

    private TimeSlot mapTimeSlot(ResultSet rs) throws SQLException {
        TimeSlot slot = new TimeSlot();
        slot.setSlotId(rs.getInt("slot_id"));
        slot.setBoothId(rs.getInt("booth_id"));
        slot.setSlotDate(rs.getDate("slot_date"));
        slot.setStartTime(rs.getTime("start_time"));
        slot.setEndTime(rs.getTime("end_time"));
        slot.setMaxReservations(rs.getInt("max_reservations"));
        return slot;
    }

    // 내 예약 내역 조회 (v_reservation_detail 뷰 활용)
    // 학생 ID로 전체 예약 내역 조회 (최신순)
    public List<ReservationDetail> findReservationsByStudentId(int studentId) throws SQLException {
        String sql = """
                SELECT reservation_id, status, created_at,
                       student_id, student_name, student_email,
                       department_name, professor_name,
                       booth_name, booth_type,
                       slot_date, start_time, end_time, notes
                FROM v_reservation_detail
                WHERE student_id = ?
                ORDER BY slot_date DESC, start_time DESC
                """;
        List<ReservationDetail> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapReservationDetail(rs));
                }
            }
        }

        return list;
    }

    // 학생 ID + 상태로 예약 내역 조회
    public List<ReservationDetail> findReservationsByStudentIdAndStatus(int studentId, String status)
            throws SQLException {
        String sql = """
                SELECT reservation_id, status, created_at,
                       student_id, student_name, student_email,
                       department_name, professor_name,
                       booth_name, booth_type,
                       slot_date, start_time, end_time, notes
                FROM v_reservation_detail
                WHERE student_id = ?
                  AND status = ?
                ORDER BY slot_date DESC, start_time DESC
                """;
        List<ReservationDetail> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, studentId);
            pstmt.setString(2, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapReservationDetail(rs));
                }
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
        detail.setNotes(rs.getString("notes"));
        return detail;
    }
}

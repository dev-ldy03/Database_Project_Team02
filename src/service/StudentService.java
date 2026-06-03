package DB2026Team02.service;

import DB2026Team02.dao.StudentDAO;
import DB2026Team02.db.DatabaseConnection;
import DB2026Team02.model.ConsultationBooth;
import DB2026Team02.model.Department;
import DB2026Team02.model.Professor;
import DB2026Team02.model.ReservationDetail;
import DB2026Team02.model.Student;
import DB2026Team02.model.TimeSlot;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class StudentService {

    private final StudentDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAO();
    }

    // 학생 정보 등록/조회
    // 학생 등록 - 트랜잭션 처리 (이메일 중복 확인 + 삽입)
    public int registerStudent(String studentName, String email, String phone, String major)
            throws SQLException {

        if (studentName == null || studentName.isBlank()) {
            System.out.println("학생 이름은 필수입니다.");
            return -1;
        }
        if (email == null || email.isBlank()) {
            System.out.println("이메일은 필수입니다.");
            return -1;
        }

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. 이메일 중복 확인
            Student existing = studentDAO.findByEmail(conn, email);
            if (existing != null) {
                conn.rollback();
                System.out.println("이미 등록된 이메일입니다: " + email);
                return -1;
            }

            // 2. 학생 등록
            Student student = new Student(studentName, email, phone, major);
            int studentId = studentDAO.insertStudent(conn, student);

            if (studentId == -1) {
                conn.rollback();
                System.out.println("학생 등록에 실패했습니다.");
                return -1;
            }

            conn.commit();
            return studentId;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.out.println("DB 연결 종료 중 오류 발생: " + e.getMessage());
                }
            }
        }
    }

    // 학생 ID로 조회
    public Student getStudentById(int studentId) throws SQLException {
        Student student = studentDAO.findById(studentId);
        if (student == null) {
            System.out.println("해당 학생을 찾을 수 없습니다. (student_id=" + studentId + ")");
        }
        return student;
    }

    // 이메일로 학생 조회
    public Student getStudentByEmail(String email) throws SQLException {
        if (email == null || email.isBlank()) {
            System.out.println("이메일은 필수입니다.");
            return null;
        }
        Student student = studentDAO.findByEmail(email);
        if (student == null) {
            System.out.println("해당 이메일로 등록된 학생이 없습니다: " + email);
        }
        return student;
    }

    // 상담 정보 검색
    // 학과 키워드 검색 (빈 키워드는 빈 리스트 반환)
    public List<Department> searchDepartments(String keyword) throws SQLException {
        if (keyword == null || keyword.isBlank()) {
            System.out.println("검색어를 입력하세요.");
            return Collections.emptyList();
        }
        return studentDAO.findDepartmentsByKeyword(keyword.trim());
    }

    // 학과별 교수 목록 조회
    public List<Professor> getProfessorsByDepartment(int departmentId) throws SQLException {
        return studentDAO.findProfessorsByDepartment(departmentId);
    }

    // 학과별 상담 부스 목록 조회
    public List<ConsultationBooth> getBoothsByDepartment(int departmentId) throws SQLException {
        return studentDAO.findBoothsByDepartment(departmentId);
    }


    // 상담 가능 시간 조회
    // 특정 학과·날짜에서 예약 가능한 시간대 조회
    public List<TimeSlot> getAvailableSlotsByDepartment(int departmentId, Date slotDate)
            throws SQLException {
        if (slotDate == null) {
            System.out.println("날짜를 지정해야 합니다.");
            return Collections.emptyList();
        }
        return studentDAO.findAvailableSlotsByDepartment(departmentId, slotDate);
    }

    // 특정 부스의 예약 가능한 시간대 조회
    public List<TimeSlot> getAvailableSlotsByBooth(int boothId) throws SQLException {
        return studentDAO.findAvailableSlotsByBooth(boothId);
    }

    // 내 예약 내역 조회
    // 학생 ID로 전체 예약 내역 조회
    public List<ReservationDetail> getMyReservations(int studentId) throws SQLException {
        // 학생 존재 여부 확인
        if (studentDAO.findById(studentId) == null) {
            System.out.println("해당 학생을 찾을 수 없습니다. (student_id=" + studentId + ")");
            return Collections.emptyList();
        }
        return studentDAO.findReservationsByStudentId(studentId);
    }

    // 학생 ID + 상태로 예약 내역 조회
    public List<ReservationDetail> getMyReservationsByStatus(int studentId, String status)
            throws SQLException {
        if (status == null || status.isBlank()) {
            System.out.println("상태를 지정하세요. (PENDING, CONFIRMED, CANCELLED, COMPLETED)");
            return Collections.emptyList();
        }
        return studentDAO.findReservationsByStudentIdAndStatus(studentId, status);
    }
}

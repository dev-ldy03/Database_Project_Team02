package DB2026Team02.test;

import DB2026Team02.model.ConsultationBooth;
import DB2026Team02.model.Department;
import DB2026Team02.model.Professor;
import DB2026Team02.model.ReservationDetail;
import DB2026Team02.model.Student;
import DB2026Team02.model.TimeSlot;
import DB2026Team02.service.StudentService;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class StudentTest {

    public static void main(String[] args) {
        StudentService studentService = new StudentService();

        try {
            System.out.println("===== 학생 기능 테스트 시작 =====");

            // ── 학생 정보 등록/조회 ────────────────────────────────────────────
            String uniqueEmail = "test_" + System.currentTimeMillis() + "@ewha.ac.kr";

            System.out.println("\n[TEST 1] 학생 등록 (정상)");
            int newStudentId = studentService.registerStudent(
                    "테스트학생", uniqueEmail, "010-1234-5678", "컴퓨터공학");

            if (newStudentId != -1) {
                System.out.println("학생 등록 성공, 학생 ID: " + newStudentId);
            } else {
                System.out.println("학생 등록 실패");
            }

            System.out.println("\n[TEST 2] 중복 이메일 등록 방지");
            int duplicateId = studentService.registerStudent(
                    "중복테스트", uniqueEmail, "010-0000-0000", "경영학");

            if (duplicateId == -1) {
                System.out.println("중복 등록 방지 성공");
            } else {
                System.out.println("중복 등록 방지 실패 - ID: " + duplicateId);
            }

            System.out.println("\n[TEST 3] 학생 ID로 조회 (초기 데이터 student_id=1)");
            Student student = studentService.getStudentById(1);
            if (student != null) {
                System.out.printf("  ID:%d | %s | %s | %s | %s%n",
                        student.getStudentId(), student.getStudentName(),
                        student.getEmail(), student.getPhone(), student.getMajor());
            }

            System.out.println("\n[TEST 4] 이메일로 학생 조회");
            Student byEmail = studentService.getStudentByEmail("student1@ewha.ac.kr");
            if (byEmail != null) {
                System.out.printf("  조회 성공: ID:%d | %s%n",
                        byEmail.getStudentId(), byEmail.getStudentName());
            }

            // ── 상담 정보 검색 ─────────────────────────────────────────────────
            System.out.println("\n[TEST 5] 학과 키워드 검색 ('학과')");
            List<Department> departments = studentService.searchDepartments("학과");
            for (Department d : departments) {
                System.out.printf("  ID:%d | %s | %s%n",
                        d.getDepartmentId(), d.getDepartmentName(), d.getLocation());
            }

            System.out.println("\n[TEST 6] 학과별 교수 목록 (department_id=1)");
            List<Professor> professors = studentService.getProfessorsByDepartment(1);
            for (Professor p : professors) {
                System.out.printf("  ID:%d | %s | %s%n",
                        p.getProfessorId(), p.getProfessorName(), p.getEmail());
            }

            System.out.println("\n[TEST 7] 학과별 상담 부스 (department_id=1)");
            List<ConsultationBooth> booths = studentService.getBoothsByDepartment(1);
            for (ConsultationBooth b : booths) {
                System.out.printf("  ID:%d | %s | %s | 정원:%d%n",
                        b.getBoothId(), b.getBoothName(), b.getBoothType(), b.getCapacity());
            }

            // ── 상담 가능 시간 조회 ─────────────────────────────────────────────
            Date targetDate = Date.valueOf("2026-05-20");

            System.out.println("\n[TEST 8] 학과·날짜로 예약 가능 시간대 (dept=1, 2026-05-20)");
            List<TimeSlot> deptSlots = studentService.getAvailableSlotsByDepartment(1, targetDate);
            if (deptSlots.isEmpty()) {
                System.out.println("  (예약 가능한 시간대 없음 — 초기 데이터에서 마감되었을 수 있음)");
            }
            for (TimeSlot s : deptSlots) {
                System.out.printf("  slot_id:%d | booth_id:%d | %s %s~%s | 최대:%d%n",
                        s.getSlotId(), s.getBoothId(),
                        s.getSlotDate(), s.getStartTime(), s.getEndTime(),
                        s.getMaxReservations());
            }

            System.out.println("\n[TEST 9] 부스별 예약 가능 시간대 (booth_id=3)");
            List<TimeSlot> boothSlots = studentService.getAvailableSlotsByBooth(3);
            for (TimeSlot s : boothSlots) {
                System.out.printf("  slot_id:%d | %s %s~%s | 최대:%d%n",
                        s.getSlotId(), s.getSlotDate(),
                        s.getStartTime(), s.getEndTime(),
                        s.getMaxReservations());
            }

            // ── 내 예약 내역 조회 ──────────────────────────────────────────────
            System.out.println("\n[TEST 10] 내 예약 내역 (student_id=1 — 홍길동)");
            List<ReservationDetail> myReservations = studentService.getMyReservations(1);
            for (ReservationDetail r : myReservations) {
                System.out.printf("  예약ID:%d | %s | %s | %s | %s %s~%s%n",
                        r.getReservationId(), r.getStatus(),
                        r.getProfessorName(), r.getBoothName(),
                        r.getSlotDate(), r.getStartTime(), r.getEndTime());
            }

            System.out.println("\n[TEST 11] 상태별 예약 내역 (student_id=1, CONFIRMED)");
            List<ReservationDetail> confirmed =
                    studentService.getMyReservationsByStatus(1, "CONFIRMED");
            System.out.println("  CONFIRMED 건수: " + confirmed.size());
            for (ReservationDetail r : confirmed) {
                System.out.printf("  예약ID:%d | %s | %s %s~%s%n",
                        r.getReservationId(), r.getProfessorName(),
                        r.getSlotDate(), r.getStartTime(), r.getEndTime());
            }

            System.out.println("\n[TEST 12] 존재하지 않는 학생 예약 조회 (student_id=9999)");
            List<ReservationDetail> none = studentService.getMyReservations(9999);
            System.out.println("  반환 건수: " + none.size() + " (0이어야 정상)");

            System.out.println("\n===== 학생 기능 테스트 종료 =====");

        } catch (SQLException e) {
            System.out.println("테스트 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

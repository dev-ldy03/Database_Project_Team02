package DB2026Team02.test;

import DB2026Team02.model.*;
import DB2026Team02.service.AdminService;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

public class AdminTest {

    public static void main(String[] args) {
        AdminService adminService = new AdminService();

        try {
            System.out.println("===== 관리자 기능 테스트 시작 =====");

            // ── 학과 조회 ──────────────────────────────────────────────────────
            System.out.println("\n[TEST 1] 학과 전체 조회");
            List<Department> departments = adminService.getAllDepartments();
            for (Department d : departments) {
                System.out.printf("  ID:%d | %s | %s%n",
                        d.getDepartmentId(), d.getDepartmentName(), d.getLocation());
            }

            // ── 학과 추가/수정/삭제 ────────────────────────────────────────────
            System.out.println("\n[TEST 2] 학과 추가");
            int newDeptId = adminService.addDepartment("테스트학과", "테스트관 F동");
            System.out.println("추가된 학과 ID: " + newDeptId);

            System.out.println("\n[TEST 3] 학과 수정");
            boolean updated = adminService.updateDepartment(newDeptId, "테스트학과(수정)", "테스트관 G동");
            System.out.println("수정 결과: " + (updated ? "성공" : "실패"));

            System.out.println("\n[TEST 4] 학과 삭제");
            boolean deleted = adminService.deleteDepartmentCascade(newDeptId);
            System.out.println("삭제 결과: " + (deleted ? "성공" : "실패"));

            // ── 교수 조회 ──────────────────────────────────────────────────────
            System.out.println("\n[TEST 5] 교수 전체 조회");
            List<Professor> professors = adminService.getAllProfessors();
            for (Professor p : professors) {
                System.out.printf("  ID:%d | %s | dept_id:%d | %s%n",
                        p.getProfessorId(), p.getProfessorName(),
                        p.getDepartmentId(), p.getEmail());
            }

            // ── 부스 조회 ──────────────────────────────────────────────────────
            System.out.println("\n[TEST 6] 상담 부스 전체 조회");
            List<ConsultationBooth> booths = adminService.getAllBooths();
            for (ConsultationBooth b : booths) {
                System.out.printf("  ID:%d | %s | %s | 정원:%d%n",
                        b.getBoothId(), b.getBoothName(), b.getBoothType(), b.getCapacity());
            }

            // ── 시간표 조회 ────────────────────────────────────────────────────
            System.out.println("\n[TEST 7] 시간표 전체 조회");
            List<TimeSlot> slots = adminService.getAllTimeSlots();
            for (TimeSlot s : slots) {
                System.out.printf("  ID:%d | booth_id:%d | %s %s~%s | 최대:%d%n",
                        s.getSlotId(), s.getBoothId(),
                        s.getSlotDate(), s.getStartTime(), s.getEndTime(),
                        s.getMaxReservations());
            }

            // ── 예약 현황 조회 ─────────────────────────────────────────────────
            System.out.println("\n[TEST 8] 전체 예약 현황 조회");
            List<ReservationDetail> allReservations = adminService.getAllReservationDetails();
            for (ReservationDetail r : allReservations) {
                System.out.printf("  예약ID:%d | %s | %s | %s교수 | %s | %s %s~%s%n",
                        r.getReservationId(), r.getStatus(),
                        r.getStudentName(), r.getProfessorName(),
                        r.getBoothName(), r.getSlotDate(),
                        r.getStartTime(), r.getEndTime());
            }

            System.out.println("\n[TEST 9] CONFIRMED 상태 예약 조회");
            List<ReservationDetail> confirmed = adminService.getReservationDetailsByStatus("CONFIRMED");
            System.out.println("CONFIRMED 예약 수: " + confirmed.size());

            // ── 체크인 처리 ────────────────────────────────────────────────────
            System.out.println("\n[TEST 10] 체크인 처리 (예약 ID: 2)");
            boolean checkInResult = adminService.processCheckIn(2);
            System.out.println("체크인 결과: " + (checkInResult ? "성공" : "실패"));

            // ── 이미 완료된 예약 체크인 방지 ──────────────────────────────────
            System.out.println("\n[TEST 11] 완료된 예약 체크인 방지 (예약 ID: 5)");
            boolean duplicateCheckIn = adminService.processCheckIn(5);
            System.out.println("체크인 방지 결과: " + (!duplicateCheckIn ? "방지 성공" : "방지 실패"));

            // ── 노쇼 관리 ──────────────────────────────────────────────────────
            System.out.println("\n[TEST 12] 노쇼 대상 조회");
            List<ReservationDetail> noShowCandidates = adminService.getNoShowCandidates();
            System.out.println("노쇼 대상 건수: " + noShowCandidates.size());
            for (ReservationDetail r : noShowCandidates) {
                System.out.printf("  예약ID:%d | %s | %s | %s%n",
                        r.getReservationId(), r.getStudentName(),
                        r.getSlotDate(), r.getStatus());
            }

            // ── 통계 조회 ──────────────────────────────────────────────────────
            System.out.println("\n[TEST 13] 전체 통계 요약");
            int[] stats = adminService.getOverallStats();
            System.out.printf("  전체:%d | CONFIRMED:%d | COMPLETED:%d | CANCELLED:%d | PENDING:%d%n",
                    stats[0], stats[1], stats[2], stats[3], stats[4]);

            System.out.println("\n[TEST 14] 상태별 예약 통계");
            List<String[]> statusStats = adminService.getReservationStatsByStatus();
            for (String[] row : statusStats) {
                System.out.printf("  %s: %s건%n", row[0], row[1]);
            }

            System.out.println("\n[TEST 15] 학과별 예약 통계");
            List<String[]> deptStats = adminService.getReservationStatsByDepartment();
            for (String[] row : deptStats) {
                System.out.printf("  %-15s | 전체:%s | 확정:%s | 완료:%s | 취소:%s | 대기:%s%n",
                        row[0], row[1], row[2], row[3], row[4], row[5]);
            }

            System.out.println("\n[TEST 16] 부스별 예약 현황");
            List<String[]> boothStats = adminService.getBoothScheduleStats();
            for (String[] row : boothStats) {
                System.out.printf("  %s | %s | %s %s~%s | %s/%s건%n",
                        row[0], row[1], row[2], row[3], row[4], row[6], row[5]);
            }

            System.out.println("\n===== 관리자 기능 테스트 종료 =====");

        } catch (SQLException e) {
            System.out.println("테스트 중 DB 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package DB2026Team02.test;

import DB2026Team02.model.Reservation;
import DB2026Team02.service.ReservationService;

import java.sql.SQLException;

public class ReservationTest {

    public static void main(String[] args) {
        ReservationService reservationService = new ReservationService();

        try {
            System.out.println("===== 예약 기능 테스트 시작 =====");

            // 1. 예약 ID로 조회 테스트
            System.out.println("\n[TEST 1] 예약 ID 조회");
            Reservation reservation = reservationService.findReservationById(1);

            if (reservation != null) {
                System.out.println("예약 조회 성공");
                System.out.println("예약 ID: " + reservation.getReservationId());
                System.out.println("학생 ID: " + reservation.getStudentId());
                System.out.println("시간대 ID: " + reservation.getSlotId());
                System.out.println("상태: " + reservation.getStatus());
            } else {
                System.out.println("예약을 찾을 수 없습니다.");
            }

            // 2. 중복 예약 방지 테스트
            System.out.println("\n[TEST 2] 중복 예약 방지");
            int duplicateResult = reservationService.createReservation(
                    1,
                    1,
                    1,
                    1,
                    "중복 예약 테스트"
            );

            if (duplicateResult == -1) {
                System.out.println("중복 예약 방지 성공");
            } else {
                System.out.println("중복 예약 방지 실패 - 예약 ID: " + duplicateResult);
            }

            // 3. 정상 예약 생성 테스트
            System.out.println("\n[TEST 3] 정상 예약 생성");
            int newReservationId = reservationService.createReservation(
                    4,
                    3,
                    4,
                    3,
                    "정상 예약 테스트"
            );

            if (newReservationId != -1) {
                System.out.println("예약 생성 성공, 예약 ID: " + newReservationId);
            } else {
                System.out.println("예약 생성 실패");
            }

            // 4. 생성된 예약 취소 테스트
            if (newReservationId != -1) {
                System.out.println("\n[TEST 4] 예약 취소");
                boolean cancelSuccess = reservationService.cancelReservation(newReservationId);

                if (cancelSuccess) {
                    System.out.println("예약 취소 성공");

                    Reservation cancelledReservation =
                            reservationService.findReservationById(newReservationId);

                    if (cancelledReservation != null) {
                        System.out.println("취소 후 상태: " + cancelledReservation.getStatus());
                    }
                } else {
                    System.out.println("예약 취소 실패");
                }
            }

            // 5. 완료된 예약 취소 실패 테스트
            System.out.println("\n[TEST 5] 완료된 예약 취소 방지");
            boolean completedCancelResult = reservationService.cancelReservation(5);

            if (!completedCancelResult) {
                System.out.println("완료된 예약 취소 방지 성공");
            } else {
                System.out.println("완료된 예약이 취소됨 - 문제 있음");
            }

            // 6. 시간대 예약 가능 여부 확인 테스트
            System.out.println("\n[TEST 6] 시간대 예약 가능 여부 확인");
            boolean available = reservationService.isSlotAvailable(3);

            if (available) {
                System.out.println("slot_id 3은 예약 가능합니다.");
            } else {
                System.out.println("slot_id 3은 예약 불가능합니다.");
            }

            System.out.println("\n===== 예약 기능 테스트 종료 =====");

        } catch (SQLException e) {
            System.out.println("테스트 중 DB 오류 발생: " + e.getMessage());
        }
    }
}
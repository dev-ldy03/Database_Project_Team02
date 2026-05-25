package DB2026Team02.service;

import DB2026Team02.dao.ReservationDAO;
import DB2026Team02.model.Reservation;

import java.sql.SQLException;

public class ReservationService {

    private final ReservationDAO reservationDAO;

    public ReservationService() {
        this.reservationDAO = new ReservationDAO();
    }

    // 예약 생성
    public int createReservation(int studentId, int slotId,
                                 int professorId, int boothId,
                                 String notes) throws SQLException {

        // 1. 중복 예약 확인
        if (reservationDAO.hasDuplicateReservation(studentId, slotId)) {
            System.out.println("이미 해당 시간대에 예약이 존재합니다.");
            return -1;
        }

        // 2. 예약 가능 여부 확인
        if (!reservationDAO.isSlotAvailable(slotId)) {
            System.out.println("해당 시간대는 예약이 마감되었습니다.");
            return -1;
        }

        // 3. 예약 객체 생성
        Reservation reservation = new Reservation(
                studentId,
                slotId,
                professorId,
                boothId,
                "CONFIRMED",
                notes
        );

        // 4. 예약 저장
        return reservationDAO.insertReservation(reservation);
    }

    // 예약 취소
    public boolean cancelReservation(int reservationId) throws SQLException {
        Reservation reservation = reservationDAO.findById(reservationId);

        if (reservation == null) {
            System.out.println("해당 예약을 찾을 수 없습니다.");
            return false;
        }

        if ("CANCELLED".equals(reservation.getStatus())) {
            System.out.println("이미 취소된 예약입니다.");
            return false;
        }

        if ("COMPLETED".equals(reservation.getStatus())) {
            System.out.println("이미 완료된 상담은 취소할 수 없습니다.");
            return false;
        }

        return reservationDAO.cancelReservation(reservationId);
    }

    // 예약 ID로 예약 조회
    public Reservation findReservationById(int reservationId) throws SQLException {
        return reservationDAO.findById(reservationId);
    }

    // 예약 가능 여부 확인
    public boolean isSlotAvailable(int slotId) throws SQLException {
        return reservationDAO.isSlotAvailable(slotId);
    }

    // 중복 예약 여부 확인
    public boolean hasDuplicateReservation(int studentId, int slotId) throws SQLException {
        return reservationDAO.hasDuplicateReservation(studentId, slotId);
    }
}
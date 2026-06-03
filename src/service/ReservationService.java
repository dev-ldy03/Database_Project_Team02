package DB2026Team02.service;

import DB2026Team02.dao.ReservationDAO;
import DB2026Team02.db.DatabaseConnection;
import DB2026Team02.model.Reservation;

import java.sql.Connection;
import java.sql.SQLException;

public class ReservationService {

    private final ReservationDAO reservationDAO;

    public ReservationService() {
        this.reservationDAO = new ReservationDAO();
    }

    // 예약 생성 - 트랜잭션 처리
    public int createReservation(int studentId, int slotId,
                                 int professorId, int boothId,
                                 String notes) throws SQLException {

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. 중복 예약 확인
            if (reservationDAO.hasDuplicateReservation(conn, studentId, slotId)) {
                conn.rollback();
                System.out.println("이미 해당 시간대에 예약이 존재합니다.");
                return -1;
            }

            // 2. 예약 가능 여부 확인
            if (!reservationDAO.isSlotAvailable(conn, slotId)) {
                conn.rollback();
                System.out.println("해당 시간대는 예약이 마감되었습니다.");
                return -1;
            }

            // 3. 예약 객체 생성
            Reservation reservation = new Reservation(
                    studentId,
                    slotId,
                    professorId,
                    boothId,
                    "PENDING",
                    notes
            );

            // 4. 예약 저장
            int reservationId = reservationDAO.insertReservation(conn, reservation);

            if (reservationId == -1) {
                conn.rollback();
                System.out.println("예약 저장에 실패했습니다.");
                return -1;
            }

            // 5. 모든 과정 성공 시 commit
            conn.commit();
            return reservationId;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;

        }
        finally {
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

    // 예약 취소 - 트랜잭션 처리
    public boolean cancelReservation(int reservationId) throws SQLException {
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            Reservation reservation = reservationDAO.findById(conn, reservationId);

            if (reservation == null) {
                conn.rollback();
                System.out.println("해당 예약을 찾을 수 없습니다.");
                return false;
            }

            if ("CANCELLED".equals(reservation.getStatus())) {
                conn.rollback();
                System.out.println("이미 취소된 예약입니다.");
                return false;
            }

            if ("COMPLETED".equals(reservation.getStatus())) {
                conn.rollback();
                System.out.println("이미 완료된 상담은 취소할 수 없습니다.");
                return false;
            }

            boolean success = reservationDAO.cancelReservation(conn, reservationId);

            if (!success) {
                conn.rollback();
                System.out.println("예약 취소에 실패했습니다.");
                return false;
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;

        }
        finally {
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

    // 예약 삭제
    public boolean deleteReservation(int reservationId) throws SQLException {
        return reservationDAO.deleteReservation(reservationId);
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
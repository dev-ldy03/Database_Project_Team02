package DB2026Team02.dao;

import DB2026Team02.db.DatabaseConnection;
import DB2026Team02.model.Reservation;

import java.sql.*;

public class ReservationDAO {

    // 중복 예약 여부 확인
    public boolean hasDuplicateReservation(int studentId, int slotId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return hasDuplicateReservation(conn, studentId, slotId);
        }
    }

    // 중복 예약 여부 확인 (트랜잭션용)
    public boolean hasDuplicateReservation(Connection conn, int studentId, int slotId) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM RESERVATION
                WHERE student_id = ?
                  AND slot_id = ?
                  AND status IN ('PENDING', 'CONFIRMED')
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, slotId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    // 상담 시간대 예약 가능 여부 확인
    public boolean isSlotAvailable(int slotId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return isSlotAvailable(conn, slotId);
        }
    }

    // 상담 시간대 예약 가능 여부 확인 (트랜잭션용)
    public boolean isSlotAvailable(Connection conn, int slotId) throws SQLException {
        String sql = """
                SELECT ts.max_reservations,
                       COUNT(r.reservation_id) AS current_reservations
                FROM TIME_SLOT ts
                LEFT JOIN RESERVATION r
                  ON ts.slot_id = r.slot_id
                 AND r.status IN ('PENDING', 'CONFIRMED')
                WHERE ts.slot_id = ?
                GROUP BY ts.slot_id, ts.max_reservations
                FOR UPDATE
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, slotId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int maxReservations = rs.getInt("max_reservations");
                    int currentReservations = rs.getInt("current_reservations");

                    return currentReservations < maxReservations;
                }
            }
        }

        return false;
    }

    // 예약 정보 저장
    public int insertReservation(Reservation reservation) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return insertReservation(conn, reservation);
        }
    }

    // 예약 정보 저장 (트랜잭션용)
    public int insertReservation(Connection conn, Reservation reservation) throws SQLException {
        String sql = """
                INSERT INTO RESERVATION
                (student_id, slot_id, professor_id, booth_id, status, notes)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, reservation.getStudentId());
            pstmt.setInt(2, reservation.getSlotId());
            pstmt.setInt(3, reservation.getProfessorId());
            pstmt.setInt(4, reservation.getBoothId());
            pstmt.setString(5, reservation.getStatus());
            pstmt.setString(6, reservation.getNotes());

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

    // 예약 취소
    public boolean cancelReservation(int reservationId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return cancelReservation(conn, reservationId);
        }
    }

    // 예약 취소 (트랜잭션용)
    public boolean cancelReservation(Connection conn, int reservationId) throws SQLException {
        String sql = """
                UPDATE RESERVATION
                SET status = 'CANCELLED'
                WHERE reservation_id = ?
                  AND status IN ('PENDING', 'CONFIRMED')
                """;

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    // 예약 ID로 예약 정보 조회
    public Reservation findById(int reservationId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return findById(conn, reservationId, false);
        }
    }

    // 예약 ID로 예약 정보 조회 (트랜잭션용)
    public Reservation findById(Connection conn, int reservationId) throws SQLException {
        return findById(conn, reservationId, true);
    }

    // 예약 ID로 예약 정보 조회 공통 메서드
    private Reservation findById(Connection conn, int reservationId, boolean forUpdate) throws SQLException {
        String sql = """
                SELECT reservation_id, student_id, slot_id, professor_id,
                       booth_id, status, created_at, notes
                FROM RESERVATION
                WHERE reservation_id = ?
                """;

        if (forUpdate) {
            sql += " FOR UPDATE";
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reservationId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Reservation reservation = new Reservation();

                    reservation.setReservationId(rs.getInt("reservation_id"));
                    reservation.setStudentId(rs.getInt("student_id"));
                    reservation.setSlotId(rs.getInt("slot_id"));
                    reservation.setProfessorId(rs.getInt("professor_id"));
                    reservation.setBoothId(rs.getInt("booth_id"));
                    reservation.setStatus(rs.getString("status"));

                    Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        reservation.setCreatedAt(createdAt.toLocalDateTime());
                    }

                    reservation.setNotes(rs.getString("notes"));

                    return reservation;
                }
            }
        }

        return null;
    }
}
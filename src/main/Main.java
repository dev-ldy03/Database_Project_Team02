package DB2026Team02.main;

import DB2026Team02.db.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
/**
 * 대학원 박람회 상담 예약 및 운영 관리 시스템 - 진입점
 */

public class Main {
    public static void main(String[] args) {
        System.out.println("DB2026Team02 - 대학원 박람회 상담 예약 시스템");
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("DB 연결 성공!");
        } catch (SQLException e) {
            System.out.println("DB 연결 실패: " + e.getMessage());
        }
    }
}
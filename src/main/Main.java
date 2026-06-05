package DB2026Team02.main;

import DB2026Team02.db.DatabaseConnection;
import DB2026Team02.view.MainFrame;

import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * 대학원 박람회 상담 예약 및 운영 관리 시스템 - 진입점
 */
public class Main {
    public static void main(String[] args) {
        // DB 연결 사전 확인
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("DB 연결 성공 — GUI를 시작합니다.");
        } catch (SQLException e) {
            System.out.println("DB 연결 실패: " + e.getMessage());
            System.out.println("MySQL이 실행 중인지, create.sql을 실행했는지 확인하세요.");
            return;
        }

        // GUI 실행
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new MainFrame().setVisible(true);
        });
    }
}
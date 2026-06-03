package DB2026Team02.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * MySQL JDBC 연결 유틸리티 (뼈대)
 */
public final class DatabaseConnection {

    private static final String JDBC_URL =
            "jdbc:mysql://localhost:3306/DB2026Team02"
                    + "?useSSL=false"
                    + "&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=Asia/Seoul"
                    + "&characterEncoding=UTF-8";
    private static final String DB_USER = "DB2026Team02";
    private static final String DB_PASSWORD = "DB2026Team02";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
    }
}

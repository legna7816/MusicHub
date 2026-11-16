package com.musichub.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBUtil {

    // 접속 정보 (db.properties 에서 읽어옴 — 실제 비밀번호는 깃허브에 올라가지 않음)
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    // 드라이버 등록 + 설정 파일 로딩은 클래스 로딩 시 한 번만 수행
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC 드라이버를 찾을 수 없습니다. "
                    + "mysql-connector-j.jar 를 WEB-INF/lib 에 추가하세요.", e);
        }

        Properties props = new Properties();
        try (InputStream is = DBUtil.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (is == null) {
                throw new RuntimeException(
                        "db.properties 를 찾을 수 없습니다. "
                      + "src/main/java/db.properties.example 를 복사해 "
                      + "db.properties 로 이름을 바꾸고 본인 환경에 맞게 값을 채워주세요.");
            }
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("db.properties 로딩 중 오류가 발생했습니다.", e);
        }

        URL      = props.getProperty("db.url");
        USER     = props.getProperty("db.user");
        PASSWORD = props.getProperty("db.password");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * try-with-resources 를 쓰면 Connection/Statement/ResultSet 은
     * 자동으로 닫히므로 보통 아래 메서드는 필요 없다.
     * 다만 자원을 수동으로 닫아야 하는 상황을 위해 헬퍼를 둔다.
     */
    public static void close(ResultSet rs, Statement st, Connection con) {
        try { if (rs != null) {
			rs.close();
		}  } catch (SQLException ignored) {}
        try { if (st != null) {
			st.close();
		}  } catch (SQLException ignored) {}
        try { if (con != null) {
			con.close();
		} } catch (SQLException ignored) {}
    }

    private DBUtil() { } // 인스턴스화 방지
}

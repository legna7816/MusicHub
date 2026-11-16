package com.musichub.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/*
 * [이서윤 담당]
 * 좋아요 기능 DAO
 *
 * 역할
 * - 좋아요 여부 확인
 * - 좋아요 추가
 * - 좋아요 삭제
 * - 좋아요 수 조회
 */
public class LikeDAO {

    private final String URL = "jdbc:mysql://localhost:3306/MusicHubDB";
    private final String USER = "root";
    private final String PASSWORD = "1234";

    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /*
     * 좋아요 여부 확인
     */
    public boolean isLiked(int musicId, int userId) {

        String sql =
            "SELECT like_id " +
            "FROM music_likes " +
            "WHERE music_id = ? AND user_id = ?";

        try (
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, musicId);
            pstmt.setInt(2, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /*
     * 좋아요 추가
     */
    public void addLike(int musicId, int userId) {

        String sql =
            "INSERT INTO music_likes(music_id, user_id) " +
            "VALUES(?, ?)";

        try (
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, musicId);
            pstmt.setInt(2, userId);

            pstmt.executeUpdate();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 좋아요 삭제
     */
    public void removeLike(int musicId, int userId) {

        String sql =
            "DELETE FROM music_likes " +
            "WHERE music_id = ? AND user_id = ?";

        try (
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, musicId);
            pstmt.setInt(2, userId);

            pstmt.executeUpdate();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 좋아요 수 조회
     */
    public int getLikeCount(int musicId) {

        String sql =
            "SELECT COUNT(*) AS like_count " +
            "FROM music_likes " +
            "WHERE music_id = ?";

        try (
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, musicId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()) {
                    return rs.getInt("like_count");
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
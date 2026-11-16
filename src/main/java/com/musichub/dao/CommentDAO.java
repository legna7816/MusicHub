package com.musichub.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.musichub.model.CommentVO;

/*
 * [이서윤 담당]
 * 댓글 기능 DAO
 *
 * 역할
 * - 댓글 등록
 * - 댓글 삭제
 *
 * 참고
 * - 댓글 목록 조회는 현재 musicDetail.jsp에서 사용 중
 * - 이후 musicDetail.jsp를 Servlet 구조로 바꿀 때 목록 조회도 이 DAO로 이동 가능
 */
public class CommentDAO {

    private final String URL = "jdbc:mysql://localhost:3306/MusicHubDB";
    private final String USER = "root";
    private final String PASSWORD = "1234";

    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /*
     * 댓글 등록
     *
     * 반환값
     * - 생성된 comment_id
     * - 실패 시 -1
     */
    public int addComment(int musicId, int userId, String writer, String content) {

        String sql =
            "INSERT INTO music_comments " +
            "(music_id, user_id, writer, content) " +
            "VALUES (?, ?, ?, ?)";

        try (
            Connection conn = getConnection();
            PreparedStatement pstmt =
                conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            pstmt.setInt(1, musicId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, writer);
            pstmt.setString(4, content);

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if(rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    /*
     * 댓글 삭제
     *
     * 본인이 작성한 댓글만 삭제되도록
     * comment_id와 user_id를 함께 조건으로 사용한다.
     */
    public boolean deleteComment(int commentId, int userId) {

        String sql =
            "DELETE FROM music_comments " +
            "WHERE comment_id = ? AND user_id = ?";

        try (
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, commentId);
            pstmt.setInt(2, userId);

            return pstmt.executeUpdate() > 0;

        } catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /*
     * 특정 음악의 댓글 목록 조회
     *
     * 음악 상세 페이지에서 댓글 목록을 출력하기 위해 사용한다.
     */
    public List<CommentVO> getCommentList(int musicId) {
        List<CommentVO> list = new ArrayList<>();

        String sql =
            "SELECT comment_id, music_id, user_id, writer, content, created_at " +
            "FROM music_comments " +
            "WHERE music_id = ? " +
            "ORDER BY comment_id DESC";

        try (
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, musicId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    CommentVO comment = new CommentVO();

                    comment.setCommentId(rs.getInt("comment_id"));
                    comment.setMusicId(rs.getInt("music_id"));
                    comment.setUserId(rs.getInt("user_id"));
                    comment.setWriter(rs.getString("writer"));
                    comment.setContent(rs.getString("content"));
                    comment.setCreatedAt(rs.getString("created_at"));

                    list.add(comment);
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
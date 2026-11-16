package com.musichub.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.musichub.model.MusicVO;

/*
 * [이서윤 담당]
 * 음악 상세 페이지 DAO
 *
 * 역할
 * - 음악 상세 정보 조회
 * - 좋아요 수 조회
 * - 조회수 조회
 * - 현재 사용자의 좋아요 여부 조회
 * - 현재 사용자의 플레이리스트 저장 여부 조회
 */
public class MusicDetailDAO {

    private final String URL = "jdbc:mysql://localhost:3306/MusicHubDB";
    private final String USER = "root";
    private final String PASSWORD = "1234";

    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /*
     * 음악 상세 정보 조회
     */
    public MusicVO getMusicDetail(int musicId) {

        // [머지 수정] play_count → view_count (DB 실제 컬럼명)
        // [머지 추가] file_path(30초 미리듣기용), user_id(본인 확인용) 함께 조회
        String sql =
            "SELECT id AS music_id, user_id, title, artist, genre, " +
            "file_path, thumbnail_path, view_count AS play_count " +
            "FROM musics " +
            "WHERE id = ? AND is_deleted = 0";

        try (
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, musicId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()) {
                    MusicVO music = new MusicVO();

                    music.setMusicId(rs.getInt("music_id"));
                    music.setTitle(rs.getString("title"));
                    music.setArtist(rs.getString("artist"));
                    music.setGenre(rs.getString("genre"));
                    music.setThumbnailPath(rs.getString("thumbnail_path"));
                    music.setPlayCount(rs.getInt("play_count"));
                    music.setUserId(rs.getInt("user_id"));          // [머지 추가] 본인 확인용
                    music.setFilePath(rs.getString("file_path"));   // [머지 추가] 30초 미리듣기용

                    return music;
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
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

    /*
     * 현재 사용자가 해당 음악을 좋아요했는지 확인
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
     * 현재 사용자가 해당 음악을 플레이리스트에 저장했는지 확인
     */
    public boolean isPlaylistSaved(int musicId, int userId) {

        String sql =
            "SELECT pm.id " +
            "FROM playlists p " +
            "JOIN playlist_musics pm " +
            "ON p.playlist_id = pm.playlist_id " +
            "WHERE p.user_id = ? AND pm.music_id = ?";

        try (
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, musicId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
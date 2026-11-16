package com.musichub.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.musichub.model.MusicVO;

/*
 * [이서윤 담당]
 * 사용자 추천 기능 DAO
 *
 * 역할
 * - 좋아요/플레이리스트 데이터를 기반으로 추천 음악 조회
 * - musics 테이블의 PK는 id이므로, Java 코드에서는 musicId로 변환해서 사용
 *
 * 참고
 * - 음악 담당 팀원이 만든 musics 테이블의 thumbnail_path 컬럼을 함께 조회한다.
 * - 추천 화면에서는 thumbnail_path가 있으면 썸네일 이미지를 출력하고,
 *   없거나 이미지 로딩에 실패하면 JSP에서 기본 음표 아이콘을 출력한다.
 */
public class RecommendDAO {

    private final String URL = "jdbc:mysql://localhost:3306/MusicHubDB";
    private final String USER = "root";
    private final String PASSWORD = "1234";

    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /*
     * 좋아요하거나 플레이리스트에 저장한 음악의 아티스트를 기준으로 추천
     *
     * 추천 조건
     * - 사용자가 좋아요/저장한 음악과 같은 아티스트
     * - 사용자가 이미 좋아요하거나 저장한 음악은 제외
     * - 재생수 높은 순으로 최대 5개 조회
     *
     * thumbnail_path
     * - 음악 담당 테이블의 썸네일 경로
     * - 추천 카드 이미지 출력에 사용
     */
    public List<MusicVO> getArtistRecommendList(int userId) {
        List<MusicVO> list = new ArrayList<>();

        String sql =
            "SELECT DISTINCT " +
            "   m.id AS music_id, " +
            "   m.title, " +
            "   m.artist, " +
            "   m.genre, " +
            "   m.thumbnail_path, " +
            "   m.view_count AS play_count " +
            "FROM musics m " +
            "WHERE m.artist IN ( " +
            "   SELECT lm.artist " +
            "   FROM music_likes ul " +
            "   JOIN musics lm ON ul.music_id = lm.id " +
            "   WHERE ul.user_id = ? " +
            "   UNION " +
            "   SELECT sm.artist " +
            "   FROM playlists p " +
            "   JOIN playlist_musics pm ON p.playlist_id = pm.playlist_id " +
            "   JOIN musics sm ON pm.music_id = sm.id " +
            "   WHERE p.user_id = ? " +
            ") " +
            "AND m.id NOT IN ( " +
            "   SELECT music_id FROM music_likes WHERE user_id = ? " +
            "   UNION " +
            "   SELECT pm.music_id " +
            "   FROM playlists p " +
            "   JOIN playlist_musics pm ON p.playlist_id = pm.playlist_id " +
            "   WHERE p.user_id = ? " +
            ") " +
            "ORDER BY m.view_count DESC " +
            "LIMIT 5";

        try (
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            pstmt.setInt(4, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(makeMusicVO(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /*
     * 좋아요하거나 플레이리스트에 저장한 음악의 장르를 기준으로 추천
     *
     * 추천 조건
     * - 사용자가 좋아요/저장한 음악과 같은 장르
     * - 사용자가 이미 좋아요하거나 저장한 음악은 제외
     * - 재생수 높은 순으로 최대 5개 조회
     *
     * thumbnail_path
     * - 추천 페이지에서 썸네일 이미지를 보여주기 위해 함께 조회
     */
    public List<MusicVO> getGenreRecommendList(int userId) {
        List<MusicVO> list = new ArrayList<>();

        String sql =
            "SELECT DISTINCT " +
            "   m.id AS music_id, " +
            "   m.title, " +
            "   m.artist, " +
            "   m.genre, " +
            "   m.thumbnail_path, " +
            "   m.view_count AS play_count " +
            "FROM musics m " +
            "WHERE m.genre IN ( " +
            "   SELECT lm.genre " +
            "   FROM music_likes ul " +
            "   JOIN musics lm ON ul.music_id = lm.id " +
            "   WHERE ul.user_id = ? " +
            "   UNION " +
            "   SELECT sm.genre " +
            "   FROM playlists p " +
            "   JOIN playlist_musics pm ON p.playlist_id = pm.playlist_id " +
            "   JOIN musics sm ON pm.music_id = sm.id " +
            "   WHERE p.user_id = ? " +
            ") " +
            "AND m.id NOT IN ( " +
            "   SELECT music_id FROM music_likes WHERE user_id = ? " +
            "   UNION " +
            "   SELECT pm.music_id " +
            "   FROM playlists p " +
            "   JOIN playlist_musics pm ON p.playlist_id = pm.playlist_id " +
            "   WHERE p.user_id = ? " +
            ") " +
            "ORDER BY m.view_count DESC " +
            "LIMIT 5";

        try (
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);
            pstmt.setInt(4, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(makeMusicVO(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /*
     * 전체 사용자 데이터를 기반으로 인기 음악 추천
     *
     * 추천 조건
     * - 사용자가 이미 좋아요하거나 저장한 음악은 제외
     *
     * 정렬 기준
     * 1. 좋아요 수
     * 2. 댓글 수
     * 3. 재생 수
     *
     * thumbnail_path
     * - 인기 음악 카드에서도 썸네일 이미지를 출력하기 위해 함께 조회
     */
    public List<MusicVO> getPopularRecommendList(int userId) {
        List<MusicVO> list = new ArrayList<>();

        String sql =
            "SELECT " +
            "   m.id AS music_id, " +
            "   m.title, " +
            "   m.artist, " +
            "   m.genre, " +
            "   m.thumbnail_path, " +
            "   IFNULL(m.view_count, 0) AS play_count, " +
            "   (SELECT COUNT(*) FROM music_likes ml WHERE ml.music_id = m.id) AS like_count, " +
            "   (SELECT COUNT(*) FROM music_comments mc WHERE mc.music_id = m.id) AS comment_count " +
            "FROM musics m " +
            "WHERE m.id NOT IN ( " +
            "   SELECT music_id FROM music_likes WHERE user_id = ? " +
            "   UNION " +
            "   SELECT pm.music_id " +
            "   FROM playlists p " +
            "   JOIN playlist_musics pm ON p.playlist_id = pm.playlist_id " +
            "   WHERE p.user_id = ? " +
            ") " +
            "ORDER BY like_count DESC, comment_count DESC, play_count DESC " +
            "LIMIT 5";

        try (
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(makeMusicVO(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /*
     * ResultSet 데이터를 MusicVO 객체로 변환
     *
     * thumbnail_path 처리
     * - musics.thumbnail_path 값을 MusicVO.thumbnailPath에 저장한다.
     * - 실제 이미지 출력 여부는 JSP에서 판단한다.
     * - thumbnail_path가 null이거나 이미지 로딩 실패 시 JSP에서 기본 음표 아이콘을 출력한다.
     */
    private MusicVO makeMusicVO(ResultSet rs) throws SQLException {
        MusicVO music = new MusicVO();

        music.setMusicId(rs.getInt("music_id"));
        music.setTitle(rs.getString("title"));
        music.setArtist(rs.getString("artist"));
        music.setGenre(rs.getString("genre"));
        music.setPlayCount(rs.getInt("play_count"));
        music.setThumbnailPath(rs.getString("thumbnail_path"));

        return music;
    }
}
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
 * 음악 목록 DAO
 *
 * 역할
 * - 전체 음악 목록 조회
 * - 검색어 조회
 * - 장르 필터 조회
 * - 좋아요한 음악 조회
 * - 정렬 조건 처리
 *
 * 참고
 * - musics 테이블의 PK는 음악 담당 팀원 테이블 기준 id
 * - Java/JSP에서는 musicId라는 이름으로 사용하기 위해 m.id AS music_id 별칭 사용
 */
public class MusicListDAO {

    private final String URL = "jdbc:mysql://localhost:3306/MusicHubDB";
    private final String USER = "root";
    private final String PASSWORD = "1234";

    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /*
     * 음악 목록 조회
     *
     * filter
     * - all   : 전체 음악
     * - liked : 사용자가 좋아요한 음악
     *
     * sort
     * - latest : 최신순
     * - view   : 재생수순
     * - like   : 좋아요순
     */
    public List<MusicVO> getMusicList(
            String keyword,
            String filter,
            String[] selectedGenres,
            String sort,
            int userId) {

        List<MusicVO> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        if("liked".equals(filter)) {
            sql.append(
                "SELECT m.id AS music_id, m.title, m.artist, m.genre, " +
                "m.thumbnail_path, IFNULL(m.view_count, 0) AS play_count, " +
                "COUNT(DISTINCT all_likes.like_id) AS like_count " +
                "FROM musics m " +
                "JOIN music_likes my_like ON m.id = my_like.music_id " +
                "LEFT JOIN music_likes all_likes ON m.id = all_likes.music_id " +
                "WHERE my_like.user_id = ? "
            );
        } else {
            sql.append(
                "SELECT m.id AS music_id, m.title, m.artist, m.genre, " +
                "m.thumbnail_path, IFNULL(m.view_count, 0) AS play_count, " +
                "COUNT(DISTINCT all_likes.like_id) AS like_count " +
                "FROM musics m " +
                "LEFT JOIN music_likes all_likes ON m.id = all_likes.music_id " +
                "WHERE 1=1 "
            );
        }

        if(keyword != null && !keyword.trim().isEmpty()) {
            String[] words = keyword.trim().split("\\s+");

            for(int i = 0; i < words.length; i++) {
                sql.append(
                    "AND (m.title LIKE ? OR m.artist LIKE ? OR m.genre LIKE ?) "
                );
            }
        }

        if(selectedGenres != null && selectedGenres.length > 0) {
            sql.append("AND m.genre IN (");

            for(int i = 0; i < selectedGenres.length; i++) {
                if(i > 0) {
                    sql.append(",");
                }
                sql.append("?");
            }

            sql.append(") ");
        }

        sql.append(
            "GROUP BY m.id, m.title, m.artist, m.genre, m.thumbnail_path, m.view_count "
        );

        if("like".equals(sort)) {
            sql.append("ORDER BY like_count DESC");
        } else if("view".equals(sort)) {
            sql.append("ORDER BY m.view_count DESC");
        } else {
            sql.append("ORDER BY m.id DESC");
        }

        try (
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString())
        ) {
            int index = 1;

            if("liked".equals(filter)) {
                pstmt.setInt(index++, userId);
            }

            if(keyword != null && !keyword.trim().isEmpty()) {
                String[] words = keyword.trim().split("\\s+");

                for(String word : words) {
                    String searchKeyword = "%" + word + "%";

                    pstmt.setString(index++, searchKeyword);
                    pstmt.setString(index++, searchKeyword);
                    pstmt.setString(index++, searchKeyword);
                }
            }

            if(selectedGenres != null) {
                for(String genre : selectedGenres) {
                    pstmt.setString(index++, genre);
                }
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {

                    MusicVO music = makeMusicVO(rs);

                    /*
                     * 로그인 사용자가 해당 음악을 좋아요했는지 확인해서
                     * 목록 화면에서 ♥ / ♡ 표시를 구분한다.
                     */
                    if(userId > 0) {
                        music.setLiked(
                            isLiked(music.getMusicId(), userId)
                        );
                    }

                    list.add(music);
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /*
     * 현재 사용자가 특정 음악을 좋아요했는지 확인
     * musicList.jsp에서 하트 표시를 위해 사용
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
     * ResultSet 데이터를 MusicVO 객체로 변환
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
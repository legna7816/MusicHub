package com.musichub.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.musichub.model.MusicVO;
import com.musichub.model.PlaylistVO;

/*
 * [이서윤 담당]
 * 플레이리스트 DAO
 *
 * 역할
 * - 로그인 사용자의 저장한 음악 목록 조회
 * - 플레이리스트에 음악 저장
 * - 플레이리스트에서 음악 제거
 *
 * 참고
 * - musics 테이블의 PK는 음악 담당 테이블 기준 id
 * - Java/JSP에서는 musicId라는 이름으로 사용하기 위해 m.id AS music_id 별칭 사용
 */
public class PlaylistDAO {

    private final String URL = "jdbc:mysql://localhost:3306/MusicHubDB";
    private final String USER = "root";
    private final String PASSWORD = "1234";

    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /*
     * 사용자가 플레이리스트에 저장한 음악 목록 조회
     */
    public List<MusicVO> getPlaylistMusicList(int userId) {
        List<MusicVO> list = new ArrayList<>();

        String sql =
            "SELECT " +
            "   m.id AS music_id, " +
            "   m.title, " +
            "   m.artist, " +
            "   m.genre, " +
            "   m.thumbnail_path, " +
            "   IFNULL(m.view_count, 0) AS play_count " +
            "FROM playlists p " +
            "JOIN playlist_musics pm ON p.playlist_id = pm.playlist_id " +
            "JOIN musics m ON pm.music_id = m.id " +
            "WHERE p.user_id = ? " +
            "ORDER BY pm.id DESC";

        try (
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, userId);

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
     * 사용자의 기본 플레이리스트를 찾고,
     * 없으면 새로 생성한 뒤 playlist_id 반환
     */
    private int getOrCreatePlaylistId(Connection conn, int userId) throws SQLException {
        String findSql =
            "SELECT playlist_id FROM playlists " +
            "WHERE user_id = ? " +
            "ORDER BY playlist_id ASC " +
            "LIMIT 1";

        try (PreparedStatement pstmt = conn.prepareStatement(findSql)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("playlist_id");
                }
            }
        }

        String createSql =
            "INSERT INTO playlists(user_id, name) VALUES(?, ?)";

        try (
            PreparedStatement pstmt =
                conn.prepareStatement(createSql, Statement.RETURN_GENERATED_KEYS)
        ) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, "내 플레이리스트");
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    /*
     * 플레이리스트에 음악 저장
     *
     * 반환값
     * - SAVED   : 저장 성공
     * - ALREADY : 이미 저장된 음악
     * - ERROR   : 저장 실패
     */
    public String addMusicToPlaylist(int userId, int musicId) {
        try (Connection conn = getConnection()) {

            int playlistId = getOrCreatePlaylistId(conn, userId);

            if (playlistId == 0) {
                return "ERROR";
            }

            String checkSql =
                "SELECT id FROM playlist_musics " +
                "WHERE playlist_id = ? AND music_id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setInt(1, playlistId);
                pstmt.setInt(2, musicId);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return "ALREADY";
                    }
                }
            }

            String insertSql =
                "INSERT INTO playlist_musics(playlist_id, music_id) VALUES(?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setInt(1, playlistId);
                pstmt.setInt(2, musicId);
                pstmt.executeUpdate();
            }

            return "SAVED";

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    /*
     * 플레이리스트에서 음악 제거
     *
     * musics 테이블의 음악 자체를 삭제하는 것이 아니라,
     * playlist_musics 매핑 데이터만 삭제한다.
     */
    public boolean removeMusicFromPlaylist(int userId, int musicId) {
        String sql =
            "DELETE pm " +
            "FROM playlist_musics pm " +
            "JOIN playlists p ON pm.playlist_id = p.playlist_id " +
            "WHERE p.user_id = ? AND pm.music_id = ?";

        try (
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, musicId);

            return pstmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * ResultSet 데이터를 MusicVO 객체로 변환
     * thumbnail_path는 플레이리스트 카드 썸네일 출력에 사용한다.
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

    public boolean removeMusic(int userId, int musicId){

        String sql =
            "DELETE pm " +
            "FROM playlist_musics pm " +
            "JOIN playlists p " +
            "ON pm.playlist_id = p.playlist_id " +
            "WHERE p.user_id=? " +
            "AND pm.music_id=?";

        try(
            Connection conn = getConnection();
            PreparedStatement pstmt =
                conn.prepareStatement(sql);
        ){

            pstmt.setInt(1, userId);
            pstmt.setInt(2, musicId);

            return pstmt.executeUpdate() > 0;

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    /*
     * =========================================
     * [이서윤 담당 - 플레이리스트 확장]
     *
     * 사용자의 플레이리스트 목록 조회
     *
     * 역할
     * - 플레이리스트 카드 출력용
     * - playlist.jsp에서 사용
     *
     * 반환
     * - 플레이리스트 목록
     * =========================================
     */
    public List<PlaylistVO> getPlaylistList(int userId) {

        List<PlaylistVO> list = new ArrayList<>();

        String sql =
            "SELECT playlist_id, user_id, name, created_at " +
            "FROM playlists " +
            "WHERE user_id=? " +
            "ORDER BY playlist_id DESC";

        try(
            Connection conn = getConnection();
            PreparedStatement pstmt =
                conn.prepareStatement(sql);
        ){

            pstmt.setInt(1, userId);

            try(ResultSet rs = pstmt.executeQuery()){

                while(rs.next()){

                    PlaylistVO playlist =
                        new PlaylistVO();

                    playlist.setPlaylistId(
                        rs.getInt("playlist_id"));

                    playlist.setUserId(
                        rs.getInt("user_id"));

                    playlist.setPlaylistName(
                        rs.getString("name"));


                    /*
                     * =========================================
                     * [이서윤 담당 - 플레이리스트 확장]
                     *
                     * 대표 썸네일 목록 조회
                     *
                     * - 플레이리스트 카드 출력용
                     * - 4분할 이미지 생성용
                     * =========================================
                     */
                    playlist.setThumbnailList(
                        getPlaylistThumbnails(
                            conn,
                            playlist.getPlaylistId()));

                    list.add(playlist);
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return list;
    }

    /*
     * =========================================
     * [이서윤 담당 - 플레이리스트 확장]
     *
     * 플레이리스트 대표 썸네일 조회
     *
     * 역할
     * - 카드 4분할 이미지 구성
     * - 가장 먼저 추가된 음악 기준
     *
     * 규칙
     * - 음악 4개 이상 → 앞의 4개 사용
     * - 음악 1~3개 → 첫 음악 썸네일 반복
     * - 음악 없음 → 빈 리스트 반환
     * =========================================
     */
    private List<String> getPlaylistThumbnails(
            Connection conn,
            int playlistId) throws SQLException {

        List<String> thumbnails =
                new ArrayList<>();

        String sql =
            "SELECT m.thumbnail_path " +
            "FROM playlist_musics pm " +
            "JOIN musics m " +
            "ON pm.music_id = m.id " +
            "WHERE pm.playlist_id=? " +
            "ORDER BY pm.id ASC " +
            "LIMIT 4";

        try(
            PreparedStatement pstmt =
                conn.prepareStatement(sql);
        ){

            pstmt.setInt(1, playlistId);

            try(ResultSet rs =
                    pstmt.executeQuery()){

                while(rs.next()){

                    String path =
                        rs.getString(
                            "thumbnail_path");

                    thumbnails.add(path);
                }
            }
        }

        return thumbnails;
    }

    /*
     * =========================================
     * [이서윤 담당 - 플레이리스트 확장]
     *
     * 플레이리스트 상세 정보 조회
     *
     * 역할
     * - playlist/detail 페이지 상단에 출력할 플레이리스트 기본 정보 조회
     * - playlists 테이블의 name 값을 playlistName으로 사용
     *
     * 주의
     * - user_id 조건을 함께 걸어 로그인 사용자의 플레이리스트만 조회
     * - 다른 사용자의 playlist_id로 접근하는 것을 방지
     * =========================================
     */
    public PlaylistVO getPlaylistDetail(int userId, int playlistId) {

        PlaylistVO playlist = null;

        String sql =
            "SELECT playlist_id, user_id, name, created_at " +
            "FROM playlists " +
            "WHERE user_id=? AND playlist_id=?";

        try(
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ){
            pstmt.setInt(1, userId);
            pstmt.setInt(2, playlistId);

            try(ResultSet rs = pstmt.executeQuery()){

                if(rs.next()){
                    playlist = new PlaylistVO();

                    playlist.setPlaylistId(rs.getInt("playlist_id"));
                    playlist.setUserId(rs.getInt("user_id"));
                    playlist.setPlaylistName(rs.getString("name"));
                    playlist.setCreatedAt(String.valueOf(rs.getTimestamp("created_at")));

                    /*
                     * [이서윤 담당 - 플레이리스트 확장]
                     * 상세 페이지 상단 대표 이미지 출력용 썸네일 목록 저장
                     */
                    playlist.setThumbnailList(
                        getPlaylistThumbnails(conn, playlistId)
                    );

                    /*
                     * [이서윤 담당 - 플레이리스트 확장]
                     * 상세 페이지 하단 음악 목록 출력용 음악 리스트 저장
                     */
                    playlist.setMusicList(
                        getMusicListByPlaylistId(conn, userId, playlistId)
                    );
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return playlist;
    }


    /*
     * =========================================
     * [이서윤 담당 - 플레이리스트 확장]
     *
     * 특정 플레이리스트 안의 음악 목록 조회
     *
     * 역할
     * - playlistDetail.jsp 하단에 음악을 세로 리스트로 출력
     *
     * 정렬
     * - 플레이리스트에 먼저 추가된 음악 순서대로 출력
     * =========================================
     */
    private List<MusicVO> getMusicListByPlaylistId(
            Connection conn,
            int userId,
            int playlistId) throws SQLException {

        List<MusicVO> list = new ArrayList<>();

        String sql =
            "SELECT " +
            "   m.id AS music_id, " +
            "   m.title, " +
            "   m.artist, " +
            "   m.genre, " +
            "   m.thumbnail_path, " +
            "   IFNULL(m.view_count, 0) AS play_count " +
            "FROM playlists p " +
            "JOIN playlist_musics pm ON p.playlist_id = pm.playlist_id " +
            "JOIN musics m ON pm.music_id = m.id " +
            "WHERE p.user_id=? AND p.playlist_id=? " +
            "ORDER BY pm.id ASC";

        try(PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, userId);
            pstmt.setInt(2, playlistId);

            try(ResultSet rs = pstmt.executeQuery()){
                while(rs.next()){
                    list.add(makeMusicVO(rs));
                }
            }
        }

        return list;
    }

    /*
     * =========================================
     * [이서윤 담당 - 플레이리스트 확장]
     *
     * 특정 플레이리스트에서 특정 음악 제거
     *
     * 역할
     * - playlistDetail.jsp에서 휴지통 버튼 클릭 시 실행
     * - musics 테이블의 음악 자체는 삭제하지 않음
     * - playlist_musics 연결 데이터만 삭제
     *
     * 수정 내용
     * - JOIN DELETE 대신 서브쿼리 방식으로 변경
     * - 현재 로그인 사용자의 playlist_id인지 먼저 검증한 뒤 삭제
     * =========================================
     */
    public boolean removeMusicFromSpecificPlaylist(int userId, int playlistId, int musicId) {

        String sql =
            "DELETE FROM playlist_musics " +
            "WHERE playlist_id = ( " +
            "   SELECT playlist_id FROM playlists " +
            "   WHERE playlist_id = ? AND user_id = ? " +
            ") " +
            "AND music_id = ?";

        try(
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ){
            pstmt.setInt(1, playlistId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, musicId);

            int count = pstmt.executeUpdate();

            /*
             * [이서윤 담당 - 플레이리스트 확장]
             * 삭제된 행 수 확인용
             * - 1 이상이면 삭제 성공
             * - 0이면 해당 플레이리스트 안에 해당 음악이 없거나 user_id가 맞지 않음
             */
            System.out.println("[PlaylistDAO] remove count = " + count);

            return count > 0;

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    /*
     * =========================================
     * [이서윤 담당 - 플레이리스트 확장]
     *
     * 새 플레이리스트 생성
     *
     * 역할
     * - playlist.jsp의 "+ 새 플레이리스트" 모달에서 입력한 이름으로 생성
     * - DB 테이블 구조 변경 없이 playlists(user_id, name)에 INSERT
     *
     * 반환값
     * - true  : 생성 성공
     * - false : 생성 실패
     * =========================================
     */
    public boolean createPlaylist(int userId, String playlistName) {

        String sql =
            "INSERT INTO playlists(user_id, name) " +
            "VALUES(?, ?)";

        try(
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ){
            pstmt.setInt(1, userId);
            pstmt.setString(2, playlistName);

            return pstmt.executeUpdate() > 0;

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    /*
     * =========================================
     * [이서윤 담당 - 플레이리스트 확장]
     *
     * 선택한 플레이리스트에 음악 저장
     *
     * 역할
     * - musicDetail.jsp의 플레이리스트 선택 모달에서 선택한 playlist_id에 음악 저장
     *
     * 반환값
     * - SAVED   : 저장 성공
     * - ALREADY : 이미 해당 플레이리스트에 저장됨
     * - ERROR   : 저장 실패
     *
     * 주의
     * - 로그인 사용자의 플레이리스트인지 user_id로 검증
     * - musics 테이블은 건드리지 않고 playlist_musics 매핑만 추가
     * =========================================
     */
    public String addMusicToSelectedPlaylist(int userId, int playlistId, int musicId) {

        String checkPlaylistSql =
            "SELECT playlist_id FROM playlists " +
            "WHERE playlist_id=? AND user_id=?";

        String checkMusicSql =
            "SELECT id FROM playlist_musics " +
            "WHERE playlist_id=? AND music_id=?";

        String insertSql =
            "INSERT INTO playlist_musics(playlist_id, music_id) " +
            "VALUES(?, ?)";

        try(Connection conn = getConnection()){

            try(PreparedStatement pstmt = conn.prepareStatement(checkPlaylistSql)){
                pstmt.setInt(1, playlistId);
                pstmt.setInt(2, userId);

                try(ResultSet rs = pstmt.executeQuery()){
                    if(!rs.next()){
                        return "ERROR";
                    }
                }
            }

            try(PreparedStatement pstmt = conn.prepareStatement(checkMusicSql)){
                pstmt.setInt(1, playlistId);
                pstmt.setInt(2, musicId);

                try(ResultSet rs = pstmt.executeQuery()){
                    if(rs.next()){
                        return "ALREADY";
                    }
                }
            }

            try(PreparedStatement pstmt = conn.prepareStatement(insertSql)){
                pstmt.setInt(1, playlistId);
                pstmt.setInt(2, musicId);
                pstmt.executeUpdate();
            }

            return "SAVED";

        }catch(Exception e){
            e.printStackTrace();
            return "ERROR";
        }
    }

    /*
     * =========================================
     * [이서윤 담당 - 플레이리스트 확장]
     *
     * 플레이리스트 삭제
     *
     * 역할
     * - 플레이리스트 자체 삭제
     * - playlist_musics 데이터도 함께 삭제
     *
     * 참고
     * - FK 설정 여부와 관계없이 안전하게 처리하기 위해
     *   playlist_musics 먼저 삭제
     * =========================================
     */
    public boolean deletePlaylist(int userId, int playlistId){

        try(Connection conn = getConnection()){

            /*
             * 플레이리스트 내부 음악 삭제
             */
            String deleteMusicSql =
                "DELETE FROM playlist_musics " +
                "WHERE playlist_id=?";

            try(PreparedStatement pstmt =
                    conn.prepareStatement(deleteMusicSql)){

                pstmt.setInt(1, playlistId);
                pstmt.executeUpdate();
            }

            /*
             * 플레이리스트 삭제
             */
            String deletePlaylistSql =
                "DELETE FROM playlists " +
                "WHERE playlist_id=? " +
                "AND user_id=?";

            try(PreparedStatement pstmt =
                    conn.prepareStatement(deletePlaylistSql)){

                pstmt.setInt(1, playlistId);
                pstmt.setInt(2, userId);

                return pstmt.executeUpdate() > 0;
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }

    /*
     * =========================================
     * [이서윤 담당 - 플레이리스트 확장]
     *
     * 플레이리스트 이름 수정
     *
     * 역할
     * - 사용자가 만든 플레이리스트의 이름 변경
     * - user_id 조건을 함께 사용해 본인 플레이리스트만 수정
     * =========================================
     */
    public boolean updatePlaylistName(int userId, int playlistId, String playlistName){

        String sql =
            "UPDATE playlists " +
            "SET name=? " +
            "WHERE playlist_id=? AND user_id=?";

        try(
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
        ){
            pstmt.setString(1, playlistName);
            pstmt.setInt(2, playlistId);
            pstmt.setInt(3, userId);

            return pstmt.executeUpdate() > 0;

        }catch(Exception e){
            e.printStackTrace();
        }

        return false;
    }
}
package com.musichub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.musichub.model.MusicVO;
import com.musichub.util.DBUtil;

/**
 * 랭킹 DB 접근 객체
 * 담당: 승민 - Top5 조회 / 중복 IP 방지 조회수 증가
 *
 * [중복 IP 방지 원리]
 * music_views 테이블에 (music_id + ip_address + 날짜) UNIQUE 키 설정
 * INSERT IGNORE로 중복이면 무시, 신규일 때만 view_count + 1
 * → 같은 IP가 하루에 몇 번 새로고침해도 조회수 1번만 증가
 */
public class RankingDAO {

    /**
     * 조회수 기준 Top5 음악 조회
     * 삭제된 음악 제외, view_count 내림차순 상위 5개
     */
    public List<MusicVO> getTop5() {
        List<MusicVO> list = new ArrayList<>();
        String sql =
            "SELECT m.id, m.title, m.artist, m.genre, " +
            "m.thumbnail_path, m.view_count, u.nickname AS uploader_nickname " +
            "FROM musics m " +
            "JOIN users u ON m.user_id = u.user_id " +
            "WHERE m.is_deleted = 0 " +
            "ORDER BY m.view_count DESC " +
            "LIMIT 5";

        try (
            Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
                MusicVO m = new MusicVO();
                m.setId              (rs.getInt   ("id"));
                m.setTitle           (rs.getString("title"));
                m.setArtist          (rs.getString("artist"));
                m.setGenre           (rs.getString("genre"));
                m.setThumbnailPath   (rs.getString("thumbnail_path"));
                m.setViewCount       (rs.getInt   ("view_count"));
                m.setUploaderNickname(rs.getString("uploader_nickname"));
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 조회수 증가 (중복 IP 방지)
     * 1단계: music_views에 INSERT IGNORE → 중복이면 0건, 신규면 1건
     * 2단계: 1건 삽입된 경우에만 musics.view_count + 1
     *
     * @param musicId   조회된 음악 id
     * @param ipAddress 조회자 IP (하루 1회만 카운트)
     */
    public void increaseViewCount(int musicId, String ipAddress) {
        // [버그수정] viewed_slot 컬럼 추가 (1분 단위)
        // 기존: (music_id, ip_address)만 UNIQUE → 최초 1회만 카운트, 이후 영원히 안 오름
        // 수정: (music_id, ip_address, viewed_slot) UNIQUE → 1분에 1회 카운트로 정상 동작
        // DATE_FORMAT(NOW(),'%Y%m%d%H%i') = 연월일시분 (예: 202506011435 = 14시 35분)
        // → 1분이 지나면 새 조회로 인정됨
        String insertViewSql  =
            "INSERT IGNORE INTO music_views (music_id, ip_address, viewed_slot) " +
            "VALUES (?, ?, DATE_FORMAT(NOW(), '%Y%m%d%H%i'))";
        String updateCountSql =
            "UPDATE musics SET view_count = view_count + 1 WHERE id = ?";

        // [버그수정] 로컬 환경(127.0.0.1, 0:0:0:0:0:0:0:1)에서도 조회수가 오르도록
        // 로컬 IP는 사용자 구분이 불가능하므로 세션/타임스탬프를 섞어 가상 IP 생성
        String effectiveIp = ipAddress;
        if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
            // 로컬 환경에서는 음악ID + 현재 분(minute) 기준으로 구분
            // → 1분에 1번씩 조회수 증가 (데모/발표용으로 적합)
            effectiveIp = "local_" + musicId + "_" +
                          (System.currentTimeMillis() / 60000); // 60초 단위
        }

        try (Connection conn = DBUtil.getConnection()) {

            // 1단계: 조회 기록 INSERT (중복이면 IGNORE로 무시)
            int inserted;
            try (PreparedStatement pstmt = conn.prepareStatement(insertViewSql)) {
                pstmt.setInt   (1, musicId);
                pstmt.setString(2, effectiveIp);
                inserted = pstmt.executeUpdate(); // 0=중복, 1=신규
            }

            // 2단계: 신규 조회일 때만 view_count 증가
            if (inserted > 0) {
                try (PreparedStatement pstmt = conn.prepareStatement(updateCountSql)) {
                    pstmt.setInt(1, musicId);
                    pstmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

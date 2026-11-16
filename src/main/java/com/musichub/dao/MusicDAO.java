package com.musichub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.musichub.model.MusicVO;
import com.musichub.util.DBUtil;

/**
 * 음악 테이블(musics) DB 접근 객체
 * 담당: 승민 - 음악 등록 / 목록 / 상세 / 삭제
 */
public class MusicDAO {

    // ====================================================================
    // [추가 기능 - 승민] 음악 등록 제약 조건 관련 메서드 2종
    // ====================================================================

    /**
     * [추가] 제목+아티스트 중복 체크
     * 제목과 아티스트가 "둘 다" 같은 곡이 이미 있는지 확인한다.
     * 하나라도 다르면 등록 가능 (둘 다 같을 때만 중복으로 판단).
     * 삭제된 곡(is_deleted=1)은 중복 대상에서 제외한다.
     *
     * @param title  등록하려는 음악 제목
     * @param artist 등록하려는 아티스트
     * @return 이미 존재하면 true(등록 불가), 없으면 false(등록 가능)
     */
    public boolean existsByTitleAndArtist(String title, String artist) {
        String sql =
            "SELECT COUNT(*) FROM musics " +
            "WHERE title = ? AND artist = ? AND is_deleted = 0";

        try (
            Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setString(1, title);
            pstmt.setString(2, artist);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
					return rs.getInt(1) > 0;
				}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * [추가] 특정 회원이 등록한 음악 개수 조회
     * 등록 개수 제한(최대 3개) 체크에 사용한다.
     * 삭제된 곡(is_deleted=1)은 개수에서 제외 → 삭제 후 재등록 가능.
     *
     * @param userId 회원 id
     * @return 해당 회원이 등록한 활성 음악 개수
     */
    public int countByUser(int userId) {
        String sql =
            "SELECT COUNT(*) FROM musics " +
            "WHERE user_id = ? AND is_deleted = 0";

        try (
            Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
					return rs.getInt(1);
				}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 음악 등록 - musics 테이블 INSERT
     * @return 생성된 음악 id (실패 시 -1)
     */
    public int insert(MusicVO music) {
        String sql =
            "INSERT INTO musics (user_id, title, artist, genre, file_path, thumbnail_path, duration) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            pstmt.setInt   (1, music.getUserId());
            pstmt.setString(2, music.getTitle());
            pstmt.setString(3, music.getArtist());
            pstmt.setString(4, music.getGenre());
            pstmt.setString(5, music.getFilePath());
            pstmt.setString(6, music.getThumbnailPath());
            pstmt.setInt   (7, music.getDuration());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
					return rs.getInt(1); // 자동 생성된 PK 반환
				}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 전체 음악 목록 조회 (삭제 제외, 최신순)
     * users 테이블 JOIN → 업로더 닉네임 포함
     */
    public List<MusicVO> findAll() {
        List<MusicVO> list = new ArrayList<>();
        String sql =
            "SELECT m.*, u.nickname AS uploader_nickname " +
            "FROM musics m " +
            "JOIN users u ON m.user_id = u.user_id " +
            "WHERE m.is_deleted = 0 " +
            "ORDER BY m.created_at DESC";

        try (
            Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()
        ) {
            while (rs.next()) {
				list.add(mapRow(rs));
			}
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 음악 1건 상세 조회
     * @param id 음악 id
     * @return MusicVO (없으면 null)
     */
    public MusicVO findById(int id) {
        String sql =
            "SELECT m.*, u.nickname AS uploader_nickname " +
            "FROM musics m " +
            "JOIN users u ON m.user_id = u.user_id " +
            "WHERE m.id = ?";

        try (
            Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
					return mapRow(rs);
				}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 음악 소프트 삭제 - is_deleted = 1 로 변경
     * 본인 음악만 삭제 가능 (user_id 조건으로 권한 체크)
     * @return 성공 시 true
     */
    public boolean delete(int musicId, int userId) {
        String sql = "UPDATE musics SET is_deleted = 1 WHERE id = ? AND user_id = ?";

        try (
            Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {
            pstmt.setInt(1, musicId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0; // 1 이상이면 성공
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** ResultSet 한 행 → MusicVO 변환 (공통 사용) */
    private MusicVO mapRow(ResultSet rs) throws SQLException {
        MusicVO m = new MusicVO();
        m.setId              (rs.getInt      ("id"));
        m.setUserId          (rs.getInt      ("user_id"));
        m.setTitle           (rs.getString   ("title"));
        m.setArtist          (rs.getString   ("artist"));
        m.setGenre           (rs.getString   ("genre"));
        m.setFilePath        (rs.getString   ("file_path"));
        m.setThumbnailPath   (rs.getString   ("thumbnail_path"));
        m.setDuration        (rs.getInt      ("duration"));
        m.setViewCount       (rs.getInt      ("view_count"));
        m.setDeleted         (rs.getInt      ("is_deleted") == 1);
        m.setCreatedAt       (rs.getTimestamp("created_at"));
        m.setUpdatedAt       (rs.getTimestamp("updated_at"));
        m.setUploaderNickname(rs.getString   ("uploader_nickname"));
        return m;
    }
}

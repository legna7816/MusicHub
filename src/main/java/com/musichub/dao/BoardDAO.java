package com.musichub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.musichub.model.BoardVO;
import com.musichub.util.DBUtil;

public class BoardDAO {

	public int insert(BoardVO board) {
		String sql = "INSERT INTO boards (user_id, title, content) VALUES (?, ?, ?)";
		try (Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(
					sql, Statement.RETURN_GENERATED_KEYS)){
			ps.setInt(1, board.getUserId());
			ps.setString(2, board.getTitle());
			ps.setString(3, board.getContent());

			if(ps.executeUpdate() == 0) {
				return -1;
			}
			try (ResultSet keys = ps.getGeneratedKeys()) {
				if (keys.next()) {
					return keys.getInt(1);
				}
			}

			return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int count(String type, String keyword) {
		String sql = "SELECT COUNT(*) FROM boards b "
				   + "LEFT JOIN users u ON b.user_id = u.user_id "
				   + buildWhere(type, keyword);
		try (Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(sql)) {
			bindKeyword(ps, 1, type, keyword);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public List<BoardVO> selectList(int offset, int limit, String type, String keyword) {
		String sql =
				"SELECT b.board_id, b.user_id, b.title, b.view_count, "
				+ "		b.created_at, b.updated_at, "
				+ "		(SELECT COUNT(*) FROM board_comments cm WHERE cm.board_id = b.board_id) AS comment_count, "
				+ "		CASE WHEN u.user_id IS NULL OR u.is_deleted = 1 "
				+ "			 THEN '탈퇴한 회원' ELSE u.nickname END AS writer_nickname "
				+ "FROM boards b "
				+ "LEFT JOIN users u ON b.user_id = u.user_id "
				+ buildWhere(type, keyword)
				+ "ORDER BY b.board_id DESC "
				+ "LIMIT ? OFFSET ?";

		List<BoardVO> list = new ArrayList<>();
		try (Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(sql)){
			int idx = bindKeyword(ps, 1, type, keyword);
			ps.setInt(idx++, limit);
			ps.setInt(idx++, offset);
			try (ResultSet rs = ps.executeQuery()) {
				while(rs.next()) {
					list.add(mapRow(rs, false, true));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	// 검색어가 유효한지 (type 이 all/null 이거나 keyword 가 비면 검색 안 함)
	private boolean hasKeyword(String type, String keyword) {
		return keyword != null && !keyword.trim().isEmpty()
			&& type != null && !type.equals("all");
	}

	// type 에 맞는 WHERE 절 조각 생성 (검색 없으면 "")
	private String buildWhere(String type, String keyword) {
		if (!hasKeyword(type, keyword)) {
			return "";
		}
		switch (type) {
			case "title":        return "WHERE b.title LIKE ? ";
			case "content":      return "WHERE b.content LIKE ? ";
			case "titlecontent": return "WHERE (b.title LIKE ? OR b.content LIKE ?) ";
			case "writer":       return "WHERE u.nickname LIKE ? ";
			case "comment":      return "WHERE b.board_id IN "
									+ "(SELECT cm.board_id FROM board_comments cm WHERE cm.content LIKE ?) ";
			default:             return "";
		}
	}

	// WHERE 절의 ? 자리에 검색어를 바인딩하고, 다음 파라미터 인덱스를 반환
	private int bindKeyword(PreparedStatement ps, int idx, String type, String keyword)
			throws SQLException {
		if (!hasKeyword(type, keyword)) {
			return idx;
		}
		String like = "%" + keyword.trim() + "%";
		if (type.equals("titlecontent")) {
			ps.setString(idx++, like);
			ps.setString(idx++, like);
		} else {
			ps.setString(idx++, like);
		}
		return idx;
	}

	public BoardVO selectById(int boardId) {
	    String sql = "SELECT b.board_id, b.user_id, b.title, b.content, b.view_count, "
	               + "b.created_at, b.updated_at, "
	               + "CASE WHEN u.user_id IS NULL OR u.is_deleted = 1 "
	               + "     THEN '탈퇴한 회원' ELSE u.nickname END AS writer_nickname "
	               + "FROM boards b "
	               + "LEFT JOIN users u ON b.user_id = u.user_id "
	               + "WHERE b.board_id = ?";
	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setInt(1, boardId);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
					return mapRow(rs, true, false);
				}
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

	public boolean increaseViewCount(int boardId) {
		String sql =
				"UPDATE boards "
			  + "SET view_count = view_count + 1 "
			  + "WHERE board_id = ?";
		try (Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, boardId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean update(int boardId, String title, String content) {
		String sql =
				"UPDATE boards "
			  + "SET title = ?, content = ? "
			  + "WHERE board_id = ?";
		try (Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, title);
			ps.setString(2, content);
			ps.setInt(3, boardId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean delete(int boardId) {
		String sql =
				"DELETE FROM boards WHERE board_id = ?";
		try (Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, boardId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private BoardVO mapRow(ResultSet rs, boolean withContent, boolean withCommentCount)
			throws SQLException {
		BoardVO b = new BoardVO();
		b.setBoardId(rs.getInt("board_id"));
		b.setUserId(rs.getInt("user_id"));
		b.setTitle(rs.getString("title"));
		if(withContent) {
			b.setContent(rs.getString("content"));
		}
		if(withCommentCount) {
			b.setCommentCount(rs.getInt("comment_count"));
		}
		b.setViewCount(rs.getInt("view_count"));
		b.setCreatedAt(rs.getTimestamp("created_at"));
		b.setUpdatedAt(rs.getTimestamp("updated_at"));
		b.setWriterNickname(rs.getString("writer_nickname"));
		return b;
	}
}

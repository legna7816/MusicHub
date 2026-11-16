package com.musichub.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.musichub.model.BoardCommentVO;
import com.musichub.util.DBUtil;

// 게시판 댓글 DAO (테이블: board_comments)
public class BoardCommentDAO {

	public int insert(BoardCommentVO c) {
		String sql = "INSERT INTO board_comments (board_id, user_id, content) VALUES (?, ?, ?)";
		try (Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, c.getBoardId());
			ps.setInt(2, c.getUserId());
			ps.setString(3, c.getContent());
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public List<BoardCommentVO> selectByBoardId(int boardId) {
		String sql =
				"SELECT c.comment_id, c.board_id, c.user_id, c.content, c.created_at, "
			  + "      CASE WHEN u.user_id IS NULL OR u.is_deleted = 1 "
			  + "           THEN '탈퇴한 회원' ELSE u.nickname END AS writer_nickname "
			  + "FROM board_comments c "
			  + "LEFT JOIN users u ON c.user_id = u.user_id "
			  + "WHERE c.board_id = ? "
			  + "ORDER BY c.comment_id ASC";

		List<BoardCommentVO> list = new ArrayList<>();
		try (Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, boardId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(mapRow(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public BoardCommentVO selectById(int commentId) {
		String sql = "SELECT comment_id, board_id, user_id, content, created_at "
				   + "FROM board_comments WHERE comment_id = ?";
		try (Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, commentId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					BoardCommentVO c = new BoardCommentVO();
					c.setCommentId(rs.getInt("comment_id"));
					c.setBoardId(rs.getInt("board_id"));
					c.setUserId(rs.getInt("user_id"));
					c.setContent(rs.getString("content"));
					c.setCreatedAt(rs.getTimestamp("created_at"));
					return c;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean delete(int commentId) {
		String sql = "DELETE FROM board_comments WHERE comment_id = ?";
		try (Connection con = DBUtil.getConnection();
			PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, commentId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private BoardCommentVO mapRow(ResultSet rs) throws SQLException {
		BoardCommentVO c = new BoardCommentVO();
		c.setCommentId(rs.getInt("comment_id"));
		c.setBoardId(rs.getInt("board_id"));
		c.setUserId(rs.getInt("user_id"));
		c.setContent(rs.getString("content"));
		c.setCreatedAt(rs.getTimestamp("created_at"));
		c.setWriterNickname(rs.getString("writer_nickname"));
		return c;
	}
}

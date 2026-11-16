package com.musichub.service;

import java.util.List;

import com.musichub.dao.BoardCommentDAO;
import com.musichub.model.BoardCommentVO;

// 게시판 댓글 Service
public class BoardCommentService {

	private final BoardCommentDAO commentDAO = new BoardCommentDAO();

	public enum Result { SUCCESS, NOT_FOUND, NOT_OWNER, INVALID, DB_ERROR }

	public boolean write(int boardId, int userId, String content) {
		if (isBlank(content)) {
			return false;
		}
		BoardCommentVO c = new BoardCommentVO(boardId, userId, content.trim());
		return commentDAO.insert(c) > 0;
	}

	public List<BoardCommentVO> getList(int boardId) {
		return commentDAO.selectByBoardId(boardId);
	}

	public Result delete(int commentId, int loginUserId) {
		BoardCommentVO c = commentDAO.selectById(commentId);
		if (c == null) {
			return Result.NOT_FOUND;
		}
		if (c.getUserId() != loginUserId) {
			return Result.NOT_OWNER;
		}
		return commentDAO.delete(commentId) ? Result.SUCCESS : Result.DB_ERROR;
	}

	private boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}
}

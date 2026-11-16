package com.musichub.model;

import java.sql.Timestamp;

// 게시판 댓글 VO (음악 댓글 CommentVO 와 충돌 방지를 위해 Board 접두사 사용)
public class BoardCommentVO {
	private int commentId;
	private int boardId;
	private int userId;
	private String content;
	private Timestamp createdAt;

	private String writerNickname;   // 화면 표시용 (users JOIN 결과)

	public BoardCommentVO() { }

	public BoardCommentVO(int boardId, int userId, String content) {
		this.boardId = boardId;
		this.userId = userId;
		this.content = content;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public int getBoardId() {
		return boardId;
	}

	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public String getWriterNickname() {
		return writerNickname;
	}

	public void setWriterNickname(String writerNickname) {
		this.writerNickname = writerNickname;
	}
}

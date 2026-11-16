package com.musichub.model;

/*
 * [이서윤 담당]
 * 댓글 정보를 담는 VO
 *
 * music_comments 테이블의 댓글 데이터를
 * JSP 화면에 출력하기 위해 사용한다.
 */
public class CommentVO {

    private int commentId;
    private int musicId;
    private int userId;
    private String writer;
    private String content;
    private String createdAt;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getMusicId() {
        return musicId;
    }

    public void setMusicId(int musicId) {
        this.musicId = musicId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
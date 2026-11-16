package com.musichub.service;

import java.util.List;

import com.musichub.dao.CommentDAO;
import com.musichub.model.CommentVO;

/*
 * [이서윤 담당]
 * 댓글 기능 Service
 *
 * 역할
 * - Controller와 DAO 사이에서 댓글 기능 처리
 * - 댓글 등록
 * - 댓글 삭제
 */
public class CommentService {

    private CommentDAO commentDAO = new CommentDAO();

    /*
     * 댓글 등록
     */
    public int addComment(int musicId, int userId, String writer, String content) {
        return commentDAO.addComment(musicId, userId, writer, content);
    }

    /*
     * 댓글 삭제
     */
    public boolean deleteComment(int commentId, int userId) {
        return commentDAO.deleteComment(commentId, userId);
    }

    /*
     * 특정 음악의 댓글 목록 조회
     */
    public List<CommentVO> getCommentList(int musicId) {
        return commentDAO.getCommentList(musicId);
    }
}
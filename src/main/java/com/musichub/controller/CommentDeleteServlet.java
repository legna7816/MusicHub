package com.musichub.controller;

import java.io.IOException;

import com.musichub.service.CommentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*
 * [이서윤 담당]
 * 댓글 삭제 Ajax Controller
 *
 * 요청 경로
 * - /comment/delete
 *
 * 반환값
 * - DELETED
 * - LOGIN_REQUIRED
 * - ERROR
 */
@WebServlet("/comment/delete")
public class CommentDeleteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private CommentService commentService = new CommentService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        HttpSession session = request.getSession(false);

        if(session == null || session.getAttribute("loginUserId") == null){
            response.getWriter().print("LOGIN_REQUIRED");
            return;
        }

        String commentIdParam = request.getParameter("comment_id");

        if(commentIdParam == null || commentIdParam.trim().isEmpty()){
            response.getWriter().print("ERROR");
            return;
        }

        int userId = (Integer)session.getAttribute("loginUserId");
        int commentId = Integer.parseInt(commentIdParam);

        boolean result =
                commentService.deleteComment(commentId, userId);

        if(result){
            response.getWriter().print("DELETED");
        }else{
            response.getWriter().print("ERROR");
        }
    }
}
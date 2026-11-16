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
 * 댓글 등록 Ajax Controller
 *
 * 요청 경로
 * - /comment/add
 *
 * 반환값
 * - SUCCESS|comment_id|writer
 * - LOGIN_REQUIRED
 * - ERROR
 */
@WebServlet("/comment/add")
public class CommentAddServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private CommentService commentService = new CommentService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        HttpSession session = request.getSession(false);

        if(session == null || session.getAttribute("loginUserId") == null){
            response.getWriter().print("LOGIN_REQUIRED");
            return;
        }

        int userId = (Integer)session.getAttribute("loginUserId");

        String writer = (String)session.getAttribute("loginNickname");
        if(writer == null){
            writer = "사용자";
        }

        String musicIdParam = request.getParameter("music_id");
        String content = request.getParameter("content");

        if(musicIdParam == null || musicIdParam.trim().isEmpty()
                || content == null || content.trim().isEmpty()){

            response.getWriter().print("ERROR");
            return;
        }

        int musicId = Integer.parseInt(musicIdParam);

        int commentId =
                commentService.addComment(musicId, userId, writer, content);

        if(commentId > 0){
            response.getWriter().print("SUCCESS|" + commentId + "|" + writer);
        }else{
            response.getWriter().print("ERROR");
        }
    }
}
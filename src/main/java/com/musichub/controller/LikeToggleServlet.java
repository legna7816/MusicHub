package com.musichub.controller;

import java.io.IOException;

import com.musichub.service.LikeService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*
 * [이서윤 담당]
 * 좋아요 토글 Ajax Controller
 *
 * 요청 경로
 * - /like/toggle
 *
 * 역할
 * - 좋아요가 이미 있으면 취소
 * - 좋아요가 없으면 추가
 * - 변경 후 좋아요 수 반환
 *
 * 반환값
 * - LIKED|좋아요수
 * - UNLIKED|좋아요수
 * - LOGIN_REQUIRED
 * - ERROR
 */
@WebServlet("/like/toggle")
public class LikeToggleServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private LikeService likeService = new LikeService();

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

        String musicIdParam = request.getParameter("music_id");

        if(musicIdParam == null || musicIdParam.trim().isEmpty()){
            response.getWriter().print("ERROR");
            return;
        }

        int userId = (Integer)session.getAttribute("loginUserId");
        int musicId = Integer.parseInt(musicIdParam);

        String status = likeService.toggleLike(musicId, userId);
        int likeCount = likeService.getLikeCount(musicId);

        response.getWriter().print(status + "|" + likeCount);
    }
}
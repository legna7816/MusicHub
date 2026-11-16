package com.musichub.controller;

import java.io.IOException;

import com.musichub.service.MusicService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 음악 삭제 Servlet
 * 담당: 승민
 * POST /music/delete → 소프트 삭제 처리 후 목록으로 이동
 *
 * [추가 이유] detail.jsp에서 /music/delete를 호출하는데 해당 Servlet이 없어 404 오류 발생
 * → 이 파일을 추가해야 삭제 버튼이 정상 동작함
 *
 * [소프트 삭제란?]
 * 실제 DB에서 지우지 않고 is_deleted = 1 로만 변경
 * → 다른 테이블(좋아요/댓글)과의 관계 유지 + 데이터 복구 가능
 */
@WebServlet("/music/delete")
public class MusicDeleteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final MusicService musicService = new MusicService();

    /** POST: 음악 삭제 처리 (GET은 허용 안 함) */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 로그인 체크
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUserId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 삭제 요청 파라미터 수신
        String musicIdParam = request.getParameter("musicId");
        if (musicIdParam == null || musicIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/music/list");
            return;
        }

        int musicId = Integer.parseInt(musicIdParam.trim());
        int userId  = (Integer) session.getAttribute("loginUserId");

        // 삭제 처리 (본인 음악만 삭제 가능 - DAO에서 user_id 조건으로 체크)
        musicService.delete(musicId, userId);

        // 삭제 후 목록으로 이동
        response.sendRedirect(request.getContextPath() + "/music/list");
    }
}

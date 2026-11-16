package com.musichub.controller;

import java.io.IOException;

import com.musichub.model.PlaylistVO;
import com.musichub.service.PlaylistService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*
 * =========================================
 * [이서윤 담당 - 플레이리스트 확장]
 *
 * 플레이리스트 상세 페이지 Controller
 *
 * 요청 경로
 * - /playlist/detail
 *
 * 역할
 * - playlist_id 파라미터 확인
 * - 로그인 사용자 확인
 * - 해당 사용자의 플레이리스트 상세 정보 조회
 * - playlistDetail.jsp로 데이터 전달
 *
 * 주의
 * - DB 조회는 DAO에서 처리
 * - JSP는 화면 출력만 담당
 * =========================================
 */
@WebServlet("/playlist/detail")
public class PlaylistDetailServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private PlaylistService playlistService =
            new PlaylistService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if(session == null || session.getAttribute("loginUserId") == null){
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String playlistIdParam =
                request.getParameter("playlist_id");

        if(playlistIdParam == null || playlistIdParam.trim().equals("")){
            response.sendRedirect(request.getContextPath() + "/playlist");
            return;
        }

        int userId =
                (Integer)session.getAttribute("loginUserId");

        int playlistId =
                Integer.parseInt(playlistIdParam);

        /*
         * [이서윤 담당 - 플레이리스트 확장]
         * 플레이리스트 상세 정보 조회
         *
         * 포함 데이터
         * - 플레이리스트 이름
         * - 대표 썸네일 목록
         * - 플레이리스트에 추가된 음악 목록
         */
        PlaylistVO playlist =
                playlistService.getPlaylistDetail(userId, playlistId);

        if(playlist == null){
            response.sendRedirect(request.getContextPath() + "/playlist");
            return;
        }

        request.setAttribute("playlist", playlist);

        request.getRequestDispatcher("/WEB-INF/views/playlistDetail.jsp")
               .forward(request, response);
    }
}
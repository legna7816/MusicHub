package com.musichub.controller;

import java.io.IOException;

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
 * 선택한 플레이리스트에 음악 저장 Controller
 *
 * 요청 경로
 * - /playlist/select/add
 *
 * 역할
 * - 음악 상세 페이지에서 사용자가 선택한 플레이리스트에 음악 저장
 *
 * 반환값
 * - SAVED
 * - ALREADY
 * - LOGIN_REQUIRED
 * - ERROR
 * =========================================
 */
@WebServlet("/playlist/select/add")
public class PlaylistSelectAddServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private PlaylistService playlistService =
            new PlaylistService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        HttpSession session =
                request.getSession(false);

        if(session == null || session.getAttribute("loginUserId") == null){
            response.getWriter().write("LOGIN_REQUIRED");
            return;
        }

        int userId =
                (Integer)session.getAttribute("loginUserId");

        int playlistId =
                Integer.parseInt(request.getParameter("playlist_id"));

        int musicId =
                Integer.parseInt(request.getParameter("music_id"));

        String result =
                playlistService.addMusicToSelectedPlaylist(userId, playlistId, musicId);

        response.getWriter().write(result);
    }
}
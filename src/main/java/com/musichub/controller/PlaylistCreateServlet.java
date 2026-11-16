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
 * 새 플레이리스트 생성 Controller
 *
 * 요청 경로
 * - /playlist/create
 *
 * 역할
 * - playlist.jsp의 생성 모달에서 입력한 이름을 받아 새 플레이리스트 생성
 * - playlists 테이블에 user_id, name 저장
 *
 * 반환값
 * - SUCCESS
 * - LOGIN_REQUIRED
 * - EMPTY_NAME
 * - ERROR
 * =========================================
 */
@WebServlet("/playlist/create")
public class PlaylistCreateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private PlaylistService playlistService =
            new PlaylistService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        HttpSession session = request.getSession(false);

        if(session == null || session.getAttribute("loginUserId") == null){
            response.getWriter().write("LOGIN_REQUIRED");
            return;
        }

        int userId =
                (Integer)session.getAttribute("loginUserId");

        String playlistName =
                request.getParameter("playlist_name");

        if(playlistName == null || playlistName.trim().equals("")){
            response.getWriter().write("EMPTY_NAME");
            return;
        }

        /*
         * [이서윤 담당 - 플레이리스트 확장]
         * 앞뒤 공백 제거 후 저장
         */
        playlistName = playlistName.trim();

        boolean result =
                playlistService.createPlaylist(userId, playlistName);

        if(result){
            response.getWriter().write("SUCCESS");
        }else{
            response.getWriter().write("ERROR");
        }
    }
}
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
 * 플레이리스트 상세 페이지 음악 제거 Controller
 *
 * 요청 경로
 * - /playlist/detail/remove
 *
 * 역할
 * - 특정 플레이리스트에서 특정 음악만 제거
 * - 음악 자체를 삭제하지 않고 playlist_musics 매핑만 삭제
 *
 * 반환값
 * - SUCCESS
 * - LOGIN_REQUIRED
 * - ERROR
 * =========================================
 */
@WebServlet("/playlist/detail/remove")
public class PlaylistDetailRemoveServlet extends HttpServlet {

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

        int userId = (Integer)session.getAttribute("loginUserId");

        int playlistId = Integer.parseInt(request.getParameter("playlist_id"));
        int musicId = Integer.parseInt(request.getParameter("music_id"));

        boolean result =
                playlistService.removeMusicFromSpecificPlaylist(userId, playlistId, musicId);

        if(result){
            response.getWriter().write("SUCCESS");
        }else{
            response.getWriter().write("ERROR");
        }
    }
}
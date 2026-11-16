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
 * [이서윤 담당]
 * 플레이리스트 음악 삭제 Ajax Controller
 *
 * 요청 경로
 * - /playlist/remove
 *
 * 역할
 * - 플레이리스트 화면에서 X 버튼 클릭 시 호출
 * - playlist_musics 테이블에서 해당 음악만 제거
 */
@WebServlet("/playlist/remove")
public class PlaylistRemoveServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private PlaylistService playlistService = new PlaylistService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        HttpSession session = request.getSession(false);

        /*
         * PlaylistServlet에서 테스트용으로 loginUserId를 세션에 넣고 있으므로
         * 삭제 Servlet에서도 같은 세션 이름을 사용해야 한다.
         */
        if(session == null || session.getAttribute("loginUserId") == null){
            response.getWriter().print("LOGIN_REQUIRED");
            return;
        }

        String musicIdParam = request.getParameter("music_id");

        if(musicIdParam == null || musicIdParam.trim().equals("")){
            response.getWriter().print("FAIL");
            return;
        }

        int userId = (Integer)session.getAttribute("loginUserId");
        int musicId = Integer.parseInt(musicIdParam);

        boolean result =
                playlistService.removeMusicFromPlaylist(userId, musicId);

        if(result){
            response.getWriter().print("SUCCESS");
        }else{
            response.getWriter().print("FAIL");
        }
    }
}
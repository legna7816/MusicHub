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
 * 플레이리스트 음악 저장 Ajax Controller
 *
 * 요청 경로
 * - /playlist/add
 *
 * 역할
 * - 음악 상세 페이지에서 저장 버튼 클릭 시 호출
 * - 사용자의 플레이리스트가 없으면 생성
 * - 이미 저장된 음악이면 ALREADY 반환
 */
@WebServlet("/playlist/add")
public class PlaylistAddServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private PlaylistService playlistService = new PlaylistService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");

        HttpSession session = request.getSession(false);

        /*
         * PlaylistServlet과 같은 테스트용 세션 이름 사용
         * 실제 로그인 통합 후에는 LoginServlet의 세션 키에 맞추면 된다.
         */
        if(session == null || session.getAttribute("loginUserId") == null){
            response.getWriter().print("LOGIN_REQUIRED");
            return;
        }

        String musicIdParam = request.getParameter("music_id");

        if(musicIdParam == null || musicIdParam.trim().equals("")){
            response.getWriter().print("ERROR");
            return;
        }

        int userId = (Integer)session.getAttribute("loginUserId");
        int musicId = Integer.parseInt(musicIdParam);

        String result =
                playlistService.addMusicToPlaylist(userId, musicId);

        response.getWriter().print(result);
    }
}
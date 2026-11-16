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
 * 플레이리스트 이름 수정 Servlet
 *
 * 요청 URL
 * - /playlist/update
 *
 * 반환값
 * - SUCCESS
 * - EMPTY_NAME
 * - LOGIN_REQUIRED
 * - ERROR
 * =========================================
 */
@WebServlet("/playlist/update")
public class PlaylistUpdateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private PlaylistService playlistService = new PlaylistService();

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

        String playlistIdParam = request.getParameter("playlist_id");
        String playlistName = request.getParameter("playlist_name");

        if(playlistName == null || playlistName.trim().isEmpty()){
            response.getWriter().write("EMPTY_NAME");
            return;
        }

        try{
            int playlistId = Integer.parseInt(playlistIdParam);

            boolean result =
                playlistService.updatePlaylistName(userId, playlistId, playlistName.trim());

            if(result){
                response.getWriter().write("SUCCESS");
            }else{
                response.getWriter().write("ERROR");
            }

        }catch(Exception e){
            e.printStackTrace();
            response.getWriter().write("ERROR");
        }
    }
}
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
 * 플레이리스트 삭제 Servlet
 *
 * 요청 URL
 * - /playlist/delete
 *
 * 반환값
 * - SUCCESS
 * - LOGIN_REQUIRED
 * - ERROR
 * =========================================
 */
@WebServlet("/playlist/delete")
public class PlaylistDeleteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private PlaylistService playlistService =
            new PlaylistService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        if(session == null ||
           session.getAttribute("loginUserId") == null){

            response.getWriter()
                    .write("LOGIN_REQUIRED");
            return;
        }

        int userId =
            (Integer)session.getAttribute("loginUserId");

        int playlistId =
            Integer.parseInt(
                request.getParameter("playlist_id")
            );

        boolean result =
            playlistService.deletePlaylist(
                userId,
                playlistId
            );

        if(result){
            response.getWriter().write("SUCCESS");
        }else{
            response.getWriter().write("ERROR");
        }
    }
}
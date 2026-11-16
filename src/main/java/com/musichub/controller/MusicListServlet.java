package com.musichub.controller;

import java.io.IOException;
import java.util.List;

import com.musichub.model.MusicVO;
import com.musichub.service.MusicListService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*
 * [이서윤 담당]
 * 음악 목록 Controller
 *
 * 요청 경로
 * - /music/list
 *
 * 역할
 * - 검색어 처리
 * - 장르 필터 처리
 * - 정렬 처리
 * - 좋아요 음악 필터 처리
 * - musicList.jsp로 데이터 전달
 */
@WebServlet("/music/list")
public class MusicListServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private MusicListService musicListService =
            new MusicListService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String keyword =
                request.getParameter("keyword");

        if(keyword == null){
            keyword = "";
        }

        String filter =
                request.getParameter("filter");

        if(filter == null){
            filter = "all";
        }

        String sort =
                request.getParameter("sort");

        if(sort == null){
            sort = "latest";
        }

        String[] selectedGenres =
                request.getParameterValues("genre");

        if(selectedGenres == null){
            selectedGenres = new String[0];
        }

        HttpSession session =
                request.getSession(false);

        int userId = 0;

        if(session != null &&
           session.getAttribute("loginUserId") != null){

            userId =
                (Integer)session.getAttribute("loginUserId");
        }

        List<MusicVO> musicList =
                musicListService.getMusicList(
                        keyword,
                        filter,
                        selectedGenres,
                        sort,
                        userId
                );

        request.setAttribute("musicList", musicList);

        request.setAttribute("keyword", keyword);
        request.setAttribute("filter", filter);
        request.setAttribute("sort", sort);
        request.setAttribute("selectedGenres", selectedGenres);

        request.getRequestDispatcher(
                "/WEB-INF/views/musicList.jsp"
        ).forward(request, response);
    }
}
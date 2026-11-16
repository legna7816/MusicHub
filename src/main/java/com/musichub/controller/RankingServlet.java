package com.musichub.controller;

import java.io.IOException;
import java.util.List;

import com.musichub.model.MusicVO;
import com.musichub.service.RankingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Top5 랭킹 페이지 Servlet
 * 담당: 승민
 * GET /music/ranking → 조회수 기준 Top5 음악 목록 조회 후 ranking.jsp 표시
 * 로그인 없이 누구나 접근 가능
 *
 * [조원 연동 메모]
 * /music/detail 경로는 이서윤 조원의 MusicDetailServlet이 이미 담당
 * 조회수 증가(increaseViewCount)는 이서윤 조원 MusicDetailServlet에 아래 코드 추가 필요:
 *   RankingService rankingService = new RankingService();
 *   rankingService.increaseViewCount(musicId, request.getRemoteAddr());
 */
@WebServlet("/music/ranking")
public class RankingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final RankingService rankingService = new RankingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Top5 조회 후 JSP로 전달
        List<MusicVO> top5List = rankingService.getTop5();
        request.setAttribute("top5List", top5List);
        request.getRequestDispatcher("/WEB-INF/views/music/ranking.jsp").forward(request, response);
    }
}

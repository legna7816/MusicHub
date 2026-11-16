package com.musichub.controller;

import java.io.IOException;
import java.util.List;

import com.musichub.model.BoardVO;
import com.musichub.model.MusicVO;
import com.musichub.service.BoardService;
import com.musichub.service.MusicService;
import com.musichub.service.RankingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * [추가 - 승민] 메인(홈) 페이지 Controller
 *
 * 요청 경로: /home  (및 컨텍스트 루트에서 연결)
 *
 * 역할
 * - Top5 랭킹 데이터 조회 (조회수 기준)
 * - 최신 음악 4개 조회 (최신 등록순)
 * - index.jsp(메인 대시보드)로 전달하여 화면에 출력
 *
 * 메인 화면을 단순 안내가 아닌 "대시보드형"으로 구성하기 위해
 * 랭킹/최신음악 데이터를 미리 담아 전달한다.
 */
@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final RankingService rankingService = new RankingService();
    private final MusicService   musicService   = new MusicService();
    private final BoardService   boardService   = new BoardService(); // [추가] 최신 게시글용

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Top5 랭킹 (조회수 순)
        List<MusicVO> top5List = rankingService.getTop5();

        // 최신 음악 목록 (최신순 전체) → 앞에서 4개만 사용
        List<MusicVO> allList = musicService.getList();
        List<MusicVO> latestList =
            allList.size() > 4 ? allList.subList(0, 4) : allList;

        // [추가] 최신 게시글 5개 (메인 게시판 미리보기용)
        List<BoardVO> latestBoardList = boardService.getLatest(5);

        request.setAttribute("top5List",        top5List);
        request.setAttribute("latestList",      latestList);
        request.setAttribute("latestBoardList", latestBoardList);

        // 메인 대시보드 JSP로 전달
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}

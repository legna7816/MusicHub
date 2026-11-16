package com.musichub.controller;

import java.io.IOException;
import java.util.List;

import com.musichub.model.MusicVO;
import com.musichub.service.RecommendService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*
 * [이서윤 담당]
 * 사용자 추천 페이지 Controller
 *
 * 요청 경로
 * - /recommend
 *
 * 역할
 * - 로그인 여부 확인
 * - 추천 음악 목록 3종 조회
 * - recommend.jsp로 데이터 전달
 */
@WebServlet("/recommend")
public class RecommendServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private RecommendService recommendService = new RecommendService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        /*
         * 팀원 로그인 구조 기준
         * LoginServlet에서 loginUserId, loginNickname을 세션에 저장함
         */
        HttpSession session = request.getSession(false);

        if(session == null) {
            session = request.getSession(true);
        }
        /*
         * [머지 수정] 테스트용 임시 세션 제거
         * 로그인하지 않은 사용자는 로그인 페이지로 이동 (실제 LoginServlet 연동)
         */
        if(session.getAttribute("loginUserId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = (Integer)session.getAttribute("loginUserId");

        String nickname = (String)session.getAttribute("loginNickname");
        if(nickname == null) {
            nickname = "사용자";
        }

        /*
         * 추천 목록 조회
         */
        List<MusicVO> artistList =
                recommendService.getArtistRecommendList(userId);

        List<MusicVO> genreList =
                recommendService.getGenreRecommendList(userId);

        List<MusicVO> popularList =
                recommendService.getPopularRecommendList(userId);

        /*
         * JSP에서 사용할 데이터 저장
         */
        request.setAttribute("nickname", nickname);
        request.setAttribute("artistList", artistList);
        request.setAttribute("genreList", genreList);
        request.setAttribute("popularList", popularList);

        request.getRequestDispatcher("/WEB-INF/views/recommend.jsp")
               .forward(request, response);
    }
}
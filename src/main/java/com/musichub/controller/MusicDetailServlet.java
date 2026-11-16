package com.musichub.controller;

import java.io.IOException;
import java.util.List;

import com.musichub.model.CommentVO;
import com.musichub.model.MusicVO;
import com.musichub.model.PlaylistVO;
import com.musichub.service.CommentService;
import com.musichub.service.MusicDetailService;
import com.musichub.service.PlaylistService;
import com.musichub.service.RankingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*
 * [통합본] 음악 상세 페이지 Controller
 * 원본: 이서윤 / 통합 수정: 승민
 *
 * 요청 경로: /music/detail?music_id=N
 *
 * [머지 시 수정 사항]
 * 1. 테스트용 임시 세션(loginUserId=1 강제 주입) 제거
 *    → 비로그인 사용자는 userId=0으로 처리 (상세 보기는 누구나 가능)
 * 2. 승민 랭킹 기능 연동: RankingService.increaseViewCount() 호출 추가
 *    → 상세 페이지 진입 시 중복 IP 방지 조회수 증가 (랭킹 반영)
 */
@WebServlet("/music/detail")
public class MusicDetailServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private MusicDetailService musicDetailService = new MusicDetailService();
    private CommentService     commentService     = new CommentService();
    private RankingService     rankingService     = new RankingService(); // [머지 추가] 조회수/랭킹
    /*
     * [이서윤 담당 - 플레이리스트 확장]
     * 음악 상세 페이지에서 플레이리스트 선택 모달에 출력할
     * 사용자의 플레이리스트 목록 조회용 Service
     */
    private PlaylistService playlistService = new PlaylistService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // ---- 1. music_id 파라미터 검증 ----
        String musicIdParam = request.getParameter("music_id");
        if (musicIdParam == null || musicIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/music/list");
            return;
        }

        int musicId;
        try {
            musicId = Integer.parseInt(musicIdParam.trim());
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/music/list");
            return;
        }

        // ---- 2. 로그인 사용자 확인 (비로그인 = 0, 상세 보기는 누구나 가능) ----
        HttpSession session = request.getSession(false);
        int userId = 0;
        if (session != null && session.getAttribute("loginUserId") != null) {
            userId = (Integer) session.getAttribute("loginUserId");
        }

        // ---- 3. 음악 상세 조회 ----
        MusicVO music = musicDetailService.getMusicDetail(musicId);
        if (music == null) {
            response.sendRedirect(request.getContextPath() + "/music/list");
            return;
        }

        // ---- 4. [승민 랭킹 연동] 조회수 증가 (중복 IP는 하루 1회만 인정) ----
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        rankingService.increaseViewCount(musicId, ipAddress);

        // ---- 5. 좋아요/플레이리스트/댓글 정보 (이서윤 기능) ----
        int likeCount = musicDetailService.getLikeCount(musicId);
        boolean liked = userId > 0 && musicDetailService.isLiked(musicId, userId);
        boolean playlistSaved = userId > 0 && musicDetailService.isPlaylistSaved(musicId, userId);
        List<CommentVO> commentList = commentService.getCommentList(musicId);
        /*
         * =========================================
         * [이서윤 담당 - 플레이리스트 확장]
         *
         * 음악 상세 페이지의 플레이리스트 선택 모달에서 사용할
         * 사용자의 플레이리스트 목록 조회
         *
         * 역할
         * - "+ 플레이리스트 저장" 버튼 클릭 시
         * - 사용자가 가진 플레이리스트 목록을 모달에 출력하기 위함
         * =========================================
         */
        List<PlaylistVO> playlistList = playlistService.getPlaylistList(userId);

        // ---- 6. JSP로 전달 ----
        request.setAttribute("music",         music);
        request.setAttribute("likeCount",     likeCount);
        request.setAttribute("liked",         liked);
        request.setAttribute("playlistSaved", playlistSaved);
        request.setAttribute("commentList",   commentList);
        request.setAttribute("playlistList", playlistList);

        request.getRequestDispatcher("/WEB-INF/views/musicDetail.jsp")
               .forward(request, response);
    }
}

package com.musichub.controller;

import java.io.IOException;
import java.util.List;

import com.musichub.model.PlaylistVO;
import com.musichub.service.PlaylistService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*
 * [이서윤 담당]
 * 플레이리스트 목록 페이지 Controller
 *
 * 요청 경로
 * - /playlist
 *
 * 역할
 * - 로그인 여부 확인
 * - 사용자가 저장한 음악 목록 조회
 * - playlist.jsp로 데이터 전달
 *
 * =========================================
 * [이서윤 담당 - 플레이리스트 확장]
 *
 * 수정 내용
 * - 기존에는 사용자가 저장한 음악 전체 목록을 조회했음
 * - 변경 후에는 사용자가 생성한 플레이리스트 목록을 조회함
 *
 * 변경 전
 * - List<MusicVO> playlistMusicList
 *
 * 변경 후
 * - List<PlaylistVO> playlistList
 *
 * 이유
 * - 플레이리스트별 이름을 가진 카드 목록 화면을 만들기 위함
 * - DB 구조는 변경하지 않고 playlists 테이블의 name 컬럼을 사용함
 * =========================================
 */
@WebServlet("/playlist")
public class PlaylistServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private PlaylistService playlistService = new PlaylistService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if(session == null) {
            session = request.getSession(true);
        }

        /*
         * [테스트용 임시 세션]
         * 로그인 기능 통합 전 단독 테스트용.
         * 실제 통합 시에는 아래 임시 세션 코드를 제거하고
         * LoginServlet에서 저장한 loginUserId 값을 사용한다.
         */
        // [머지 수정] 테스트용 임시 세션 제거 - 비로그인 시 로그인 페이지로
        if(session.getAttribute("loginUserId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = (Integer)session.getAttribute("loginUserId");

        /*
         * =========================================
         * [이서윤 담당 - 플레이리스트 확장]
         *
         * 사용자의 플레이리스트 목록 조회
         *
         * 기존 코드
         * - playlistService.getPlaylistMusicList(userId)
         * - 사용자가 저장한 음악을 한 번에 전부 조회
         *
         * 변경 코드
         * - playlistService.getPlaylistList(userId)
         * - 사용자가 만든 플레이리스트 목록을 조회
         * =========================================
         */
        List<PlaylistVO> playlistList =
                playlistService.getPlaylistList(userId);

        /*
         * [이서윤 담당 - 플레이리스트 확장]
         * playlist.jsp에서 플레이리스트 카드 목록을 출력할 수 있도록 request에 저장
         */
        request.setAttribute("playlistList", playlistList);

        request.getRequestDispatcher("/WEB-INF/views/playlist.jsp")
               .forward(request, response);
    }
}
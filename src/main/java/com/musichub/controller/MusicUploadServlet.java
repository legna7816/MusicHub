package com.musichub.controller;

import java.io.IOException;

import com.musichub.service.MusicService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 음악 등록 Servlet
 * 담당: 승민
 * GET  /music/upload → 등록 폼 페이지
 * POST /music/upload → 파일 업로드 + DB 저장
 *
 * [버그 수정] 세션키를 조원 코드와 통일
 * 조원 LoginServlet 기준: session.setAttribute("loginUserId", int)
 * → loginUser(UserVO) 참조 제거, loginUserId(int) 사용으로 변경
 */
@WebServlet("/music/upload")
@MultipartConfig(
    maxFileSize    = 50 * 1024 * 1024, // 음악 파일 최대 50MB
    maxRequestSize = 60 * 1024 * 1024  // 요청 전체 최대 60MB (음악+썸네일 합산)
)
public class MusicUploadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final MusicService musicService = new MusicService();

    /** GET: 로그인 체크 후 등록 폼 표시 */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        // 로그인 안 한 경우 로그인 페이지로
        if (session == null || session.getAttribute("loginUserId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // [추가] 현재 등록 개수와 최대 개수를 폼에 전달 ("n/3개" 표시용)
        int userId = (Integer) session.getAttribute("loginUserId");
        request.setAttribute("uploadCount", musicService.getUploadCount(userId));
        request.setAttribute("maxUpload",   MusicService.MAX_UPLOAD_PER_USER);

        request.getRequestDispatcher("/WEB-INF/views/music/upload.jsp").forward(request, response);
    }

    /** POST: 파일 저장 + DB 등록 처리 */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUserId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // [수정] loginUserId(int) 사용 - 조원 LoginServlet이 저장하는 세션키
        int userId = (Integer) session.getAttribute("loginUserId");

        String title  = request.getParameter("title");
        String artist = request.getParameter("artist");
        String genre  = request.getParameter("genre");

        Part musicPart     = request.getPart("musicFile");     // 음악 파일 (필수)
        Part thumbnailPart = request.getPart("thumbnailFile"); // 썸네일 (선택)

        // 서버 실제 저장 경로
        String savePath = getServletContext().getRealPath("/") + "resources/uploads/";

        try {
            int result = musicService.register(userId, title, artist, genre,
                                                musicPart, thumbnailPart, savePath);

            // [추가] 등록 결과 코드에 따라 분기 처리
            if (result > 0) {
                // 성공 (양수 = 생성된 음악 id) → 목록으로
                response.sendRedirect(request.getContextPath() + "/music/list");
            } else {
                // 실패 사유별 메시지 설정
                String errorMsg;
                switch (result) {
                    case MusicService.RESULT_DUPLICATE:
                        // 제목+아티스트 모두 동일한 곡이 이미 존재
                        errorMsg = "이미 같은 제목과 아티스트의 곡이 등록되어 있습니다. "
                                 + "제목이나 아티스트 중 하나는 달라야 합니다.";
                        break;
                    case MusicService.RESULT_LIMIT_EXCEEDED:
                        // 등록 개수 3개 초과
                        errorMsg = "음악은 최대 " + MusicService.MAX_UPLOAD_PER_USER
                                 + "개까지만 등록할 수 있습니다. "
                                 + "기존 곡을 삭제한 후 다시 시도해주세요.";
                        break;
                    default:
                        errorMsg = "음악 등록에 실패했습니다. 다시 시도해주세요.";
                }
                request.setAttribute("errorMsg", errorMsg);
                // [버그수정] 폼으로 돌아갈 때도 등록 현황을 다시 넘겨야 "n/3개"가 정상 표시됨
                // (안 넘기면 uploadCount가 null → 0으로 표시되는 버그)
                request.setAttribute("uploadCount", musicService.getUploadCount(userId));
                request.setAttribute("maxUpload",   MusicService.MAX_UPLOAD_PER_USER);
                request.getRequestDispatcher("/WEB-INF/views/music/upload.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMsg", "파일 업로드 중 오류가 발생했습니다.");
            // [버그수정] 예외 시에도 등록 현황 다시 전달
            request.setAttribute("uploadCount", musicService.getUploadCount(userId));
            request.setAttribute("maxUpload",   MusicService.MAX_UPLOAD_PER_USER);
            request.getRequestDispatcher("/WEB-INF/views/music/upload.jsp").forward(request, response);
        }
    }
}

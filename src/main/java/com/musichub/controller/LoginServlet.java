package com.musichub.controller;

import java.io.IOException;

import com.musichub.model.UserVO;
import com.musichub.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if ("ok".equals(req.getParameter("signup"))) {
            req.setAttribute("notice", "회원가입이 완료되었습니다. 로그인해 주세요.");
        }
        req.getRequestDispatcher("/WEB-INF/views/login.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String loginId  = req.getParameter("loginId");
        String password = req.getParameter("password");

        UserVO user = userService.authenticate(loginId, password);

        if (user == null) {
            req.setAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            req.setAttribute("loginId", loginId);
            req.getRequestDispatcher("/WEB-INF/views/login.jsp")
               .forward(req, resp);
            return;
        }

        HttpSession oldSession = req.getSession(false);
        if (oldSession != null) {
			oldSession.invalidate();
		}
        HttpSession session = req.getSession(true);

        session.setAttribute("loginUserId",   user.getUserId());
        session.setAttribute("loginNickname", user.getNickname());
        session.setMaxInactiveInterval(30 * 60);

        // [버그수정] /(루트)는 index.jsp를 직접 띄워 HomeServlet을 거치지 않음
        // → Top5/최신음악 데이터가 없어 빈 페이지처럼 보임
        // → /home(HomeServlet)으로 보내서 데이터를 채운 메인을 표시
        resp.sendRedirect(req.getContextPath() + "/home");
    }
}

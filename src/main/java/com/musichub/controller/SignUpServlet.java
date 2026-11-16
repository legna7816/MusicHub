package com.musichub.controller;

import java.io.IOException;

import com.musichub.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/signup")
public class SignUpServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/signup.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String email           = req.getParameter("email");
        String username        = req.getParameter("username");
        String password        = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");
        String nickname        = req.getParameter("nickname");

        if (password == null || !password.equals(confirmPassword)) {
            req.setAttribute("error", "비밀번호가 일치하지 않습니다.");
            req.setAttribute("email", email);
            req.setAttribute("username", username);
            req.setAttribute("nickname", nickname);
            req.getRequestDispatcher("/WEB-INF/views/signup.jsp")
               .forward(req, resp);
            return;
        }

        UserService.SignUpResult result =
                userService.signUp(email, username, password, nickname);

        switch (result) {
            case SUCCESS:
                resp.sendRedirect(req.getContextPath() + "/login?signup=ok");
                return;

            case DUPLICATE_EMAIL:
                req.setAttribute("error", "이미 사용 중인 이메일입니다.");
                break;
            case DUPLICATE_USERNAME:
                req.setAttribute("error", "이미 사용 중인 아이디입니다.");
                break;
            case INVALID_INPUT:
                req.setAttribute("error",
                        "입력값을 확인하세요. (아이디 4~20자 영문/숫자, 비밀번호 8자 이상)");
                break;
            default:
                req.setAttribute("error",
                        "일시적인 오류가 발생했습니다. 다시 시도해 주세요.");
        }

        req.setAttribute("email", email);
        req.setAttribute("username", username);
        req.setAttribute("nickname", nickname);
        req.getRequestDispatcher("/WEB-INF/views/signup.jsp")
           .forward(req, resp);
    }
}

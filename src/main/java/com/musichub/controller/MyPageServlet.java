package com.musichub.controller;

import java.io.IOException;

import com.musichub.dao.UserDAO;
import com.musichub.model.UserVO;
import com.musichub.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/mypage")
public class MyPageServlet extends HttpServlet {

    private final UserService userService = new UserService();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int userId = currentUserId(req);
        UserVO user = userDAO.findById(userId);

        req.setAttribute("user", user);
        req.getRequestDispatcher("/WEB-INF/views/mypage.jsp")
           .forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        int userId = currentUserId(req);
        String action = req.getParameter("action");

        if ("profile".equals(action)) {
            String nickname = req.getParameter("nickname");
            boolean ok = userDAO.updateProfile(userId, nickname);
            if (ok) {
                session.setAttribute("loginNickname", nickname);
            }
            req.setAttribute("msg", ok ? "정보가 수정되었습니다."
                                       : "수정에 실패했습니다.");

        } else if ("password".equals(action)) {
            String current = req.getParameter("currentPassword");
            String next    = req.getParameter("newPassword");

            UserService.PasswordResult pr =
                    userService.changePassword(userId, current, next);

            switch (pr) {
                case SUCCESS:
                    req.setAttribute("msg", "비밀번호가 변경되었습니다."); break;
                case WRONG_CURRENT:
                    req.setAttribute("msg", "현재 비밀번호가 올바르지 않습니다."); break;
                case INVALID_NEW:
                    req.setAttribute("msg", "새 비밀번호는 8자 이상이어야 합니다."); break;
                default:
                    req.setAttribute("msg", "변경에 실패했습니다.");
            }

        } else if ("withdraw".equals(action)) {
            boolean ok = userDAO.softDelete(userId);
            if (ok) {
                session.invalidate();
                resp.sendRedirect(req.getContextPath() + "/?withdraw=ok");
                return;
            }
            req.setAttribute("msg", "탈퇴 처리에 실패했습니다.");
        }

        req.setAttribute("user", userDAO.findById(userId));
        req.getRequestDispatcher("/WEB-INF/views/mypage.jsp")
           .forward(req, resp);
    }

    private int currentUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return (int) session.getAttribute("loginUserId");
    }
}

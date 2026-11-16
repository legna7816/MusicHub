package com.musichub.controller;

import java.io.IOException;

import com.musichub.service.BoardService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/board/write")
public class BoardWriteServlet extends HttpServlet {

	private final BoardService boardService = new BoardService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setAttribute("mode", "write");
		req.getRequestDispatcher("/WEB-INF/views/board/form.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");

		int userId = currentUserId(req);
		String title = req.getParameter("title");
		String content = req.getParameter("content");

		boolean ok = boardService.write(userId, title, content);

		if (ok) {
		    resp.sendRedirect(req.getContextPath() + "/board/list");
		} else {
		    String error = (title == null || title.trim().isEmpty()
		                 || content == null || content.trim().isEmpty())
		                 ? "제목과 내용을 모두 입력하세요."
		                 : "저장 중 오류가 발생했습니다. 관리자에게 문의하세요.";
		    req.setAttribute("error", error);
		    req.setAttribute("mode", "write");
		    req.setAttribute("title", title);
		    req.setAttribute("content", content);
		    req.getRequestDispatcher("/WEB-INF/views/board/form.jsp").forward(req, resp);
		}
	}

	private int currentUserId(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		return (int) session.getAttribute("loginUserId");
	}
}

package com.musichub.controller;

import java.io.IOException;

import com.musichub.model.BoardVO;
import com.musichub.service.BoardService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/board/edit")
public class BoardEditServlet extends HttpServlet {

	private final BoardService boardService = new BoardService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		int boardId;
		try {
			boardId = Integer.parseInt(req.getParameter("id"));
		} catch (NumberFormatException e) {
			resp.sendRedirect(req.getContextPath() + "/board/list");
			return;
		}

		BoardVO board = boardService.getForEdit(boardId);
		int loginUserId = currentUserId(req);

		if (board == null || board.getUserId() != loginUserId) {
			resp.sendRedirect(req.getContextPath() + "/board/list");
			return;
		}

		req.setAttribute("mode", "edit");
		req.setAttribute("board", board);
		req.getRequestDispatcher("/WEB-INF/views/board/form.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");

		int boardId = Integer.parseInt(req.getParameter("id"));
		int loginUserId = currentUserId(req);
		String title = req.getParameter("title");
		String content = req.getParameter("content");

		BoardService.Result result = boardService.update(boardId, loginUserId, title, content);

		if (result == BoardService.Result.SUCCESS) {
			resp.sendRedirect(req.getContextPath() + "/board/view?id=" + boardId);
		} else {
			resp.sendRedirect(req.getContextPath() + "/board/list");
		}
	}

	private int currentUserId(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		return (int) session.getAttribute("loginUserId");
	}
}

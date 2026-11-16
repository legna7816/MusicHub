package com.musichub.controller;

import java.io.IOException;

import com.musichub.model.BoardVO;
import com.musichub.service.BoardCommentService;
import com.musichub.service.BoardService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/board/view")
public class BoardViewServlet extends HttpServlet {

	private final BoardService boardService = new BoardService();
	private final BoardCommentService commentService = new BoardCommentService();

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

		BoardVO board = boardService.getDetail(boardId);
		if(board == null) {
			resp.sendRedirect(req.getContextPath() + "/board/list");
			return;
		}

		HttpSession session = req.getSession(false);
		Integer loginUserId = (session == null) ? null : (Integer) session.getAttribute("loginUserId");
		req.setAttribute("board", board);
		req.setAttribute("comments", commentService.getList(boardId));
		req.setAttribute("loginUserId", loginUserId);
		req.getRequestDispatcher("/WEB-INF/views/board/view.jsp").forward(req, resp);
	}
}

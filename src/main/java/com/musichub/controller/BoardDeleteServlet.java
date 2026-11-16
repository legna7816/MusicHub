package com.musichub.controller;

import java.io.IOException;

import com.musichub.service.BoardService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/board/delete")
public class BoardDeleteServlet extends HttpServlet {

	private final BoardService boardService = new BoardService();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		int boardId = Integer.parseInt(req.getParameter("id"));
		int loginUserId = currentUserId(req);

		boardService.delete(boardId, loginUserId);

		resp.sendRedirect(req.getContextPath() + "/board/list");
	}

	private int currentUserId(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		return (int) session.getAttribute("loginUserId");
	}
}

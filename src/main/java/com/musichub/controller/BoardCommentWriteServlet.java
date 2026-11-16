package com.musichub.controller;

import java.io.IOException;

import com.musichub.service.BoardCommentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/board/comment/write")
public class BoardCommentWriteServlet extends HttpServlet {

	private final BoardCommentService commentService = new BoardCommentService();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		int boardId;
		try {
			boardId = Integer.parseInt(req.getParameter("boardId"));
		} catch (NumberFormatException e) {
			resp.sendRedirect(req.getContextPath() + "/board/list");
			return;
		}

		HttpSession session = req.getSession(false);
		Integer loginUserId = (session == null) ? null : (Integer) session.getAttribute("loginUserId");

		// AuthFilter 가 보호하지만 방어적으로 한 번 더 확인
		if (loginUserId != null) {
			commentService.write(boardId, loginUserId, req.getParameter("content"));
		}
		resp.sendRedirect(req.getContextPath() + "/board/view?id=" + boardId);
	}
}

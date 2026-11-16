package com.musichub.controller;

import java.io.IOException;

import com.musichub.service.BoardCommentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/board/comment/delete")
public class BoardCommentDeleteServlet extends HttpServlet {

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

		try {
			int commentId = Integer.parseInt(req.getParameter("commentId"));
			if (loginUserId != null) {
				commentService.delete(commentId, loginUserId);   // 소유권은 서비스가 검증
			}
		} catch (NumberFormatException ignore) {
			// commentId 가 잘못된 경우 무시하고 상세로 돌아감
		}

		resp.sendRedirect(req.getContextPath() + "/board/view?id=" + boardId);
	}
}

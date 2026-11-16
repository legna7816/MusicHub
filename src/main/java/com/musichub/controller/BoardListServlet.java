package com.musichub.controller;

import java.io.IOException;

import com.musichub.service.BoardService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/board/list")
public class BoardListServlet extends HttpServlet {

	private final BoardService boardService = new BoardService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		int page = parsePage(req.getParameter("page"));
		String type = req.getParameter("type");
		String keyword = req.getParameter("keyword");

		BoardService.PageResult result = boardService.getPage(page, type, keyword);

		req.setAttribute("page", result);
		req.getRequestDispatcher("/WEB-INF/views/board/list.jsp").forward(req, resp);
	}

	private int parsePage(String raw) {
		if (raw == null) {
			return 1;
		}
		try {
			int p = Integer.parseInt(raw);
			return (p < 1) ? 1 : p;
		} catch (NumberFormatException e) {
			return 1;
		}
	}
}

package com.musichub.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(urlPatterns = { "/mypage", "/board/write", "/board/edit", "/board/delete",
                           "/board/comment/write", "/board/comment/delete" })
public class AuthFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		HttpSession session = req.getSession(false);
		boolean loggedIn = (session != null && session.getAttribute("loginUserId") != null);

		if(loggedIn) {
			chain.doFilter(request, response);
		} else {
			String returnUrl = req.getRequestURL().toString();
			String queryString = req.getQueryString();
			if (queryString != null) {
				returnUrl += "?" + queryString;
			}
			resp.sendRedirect(req.getContextPath() + "/login?returnUrl=" + returnUrl);
		}
	}
}

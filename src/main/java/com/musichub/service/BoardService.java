package com.musichub.service;

import java.util.List;

import com.musichub.dao.BoardDAO;
import com.musichub.model.BoardVO;

public class BoardService {

	private final BoardDAO boardDAO = new BoardDAO();

	public static final int PAGE_SIZE = 20;

	public PageResult getPage(int page) {
		return getPage(page, null, null);
	}

	/**
	 * [추가] 메인 페이지용 최신 게시글 N개 조회
	 * board_id 내림차순(최신순)으로 상위 limit개를 가져온다.
	 * @param limit 가져올 개수 (메인에서는 5개)
	 */
	public List<BoardVO> getLatest(int limit) {
		return boardDAO.selectList(0, limit, null, null);
	}

	public PageResult getPage(int page, String type, String keyword) {
		if (page < 1) {
			page = 1;
		}

		int total = boardDAO.count(type, keyword);
		int totalPages = (total == 0) ? 1 : (int) Math.ceil((double) total / PAGE_SIZE);
		if(page > totalPages) {
			page = totalPages;
		}

		int offset = (page - 1) * PAGE_SIZE;
		List<BoardVO> list = boardDAO.selectList(offset, PAGE_SIZE, type, keyword);

		return new PageResult(list, page, totalPages, total);
	}

	public boolean write(int userId, String title, String content) {
		if (isBlank(title) || isBlank(content)) {
			return false;
		}
		BoardVO board = new BoardVO(userId, title.trim(), content.trim());
		return boardDAO.insert(board) > 0;
	}

	public BoardVO getDetail(int boardId) {
		boardDAO.increaseViewCount(boardId);
		return boardDAO.selectById(boardId);
	}

	public BoardVO getForEdit(int boardId) {
		return boardDAO.selectById(boardId);
	}

	public enum Result { SUCCESS, NOT_FOUND, NOT_OWNER, INVALID, DB_ERROR }

	public Result update(int boardId, int loginUserId, String title, String content) {
		BoardVO board = boardDAO.selectById(boardId);
		if (board == null) {
			return Result.NOT_FOUND;
		}
		if (board.getUserId() != loginUserId) {
			return Result.NOT_OWNER;
		}
		if (isBlank(title) || isBlank(content)) {
			return Result.INVALID;
		}

		boolean ok = boardDAO.update(boardId, title.trim(), content.trim());
		return ok ? Result.SUCCESS : Result.DB_ERROR;
	}

	public Result delete(int boardId, int loginUserId) {
		BoardVO board = boardDAO.selectById(boardId);
		if (board == null) {
			return Result.NOT_FOUND;
		}
		if (board.getUserId() != loginUserId) {
			return Result.NOT_OWNER;
		}

		boolean ok = boardDAO.delete(boardId);
		return ok ? Result.SUCCESS : Result.DB_ERROR;
	}

	public boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}

	public static class PageResult {
		private final List<BoardVO> list;
		private final int currentPage;
		private final int totalPages;
		private final int totalCount;

		public PageResult(List<BoardVO> list, int currentPage, int totalPages, int totalCount) {
			this.list = list;
			this.currentPage = currentPage;
			this.totalPages = totalPages;
			this.totalCount = totalCount;
		}

		public List<BoardVO> getList() {
			return list;
		}

		public int getCurrentPage() {
			return currentPage;
		}

		public int getTotalPages() {
			return totalPages;
		}

		public int getTotalCount() {
			return totalCount;
		}

		// 현재 페이지 첫 글에 붙일 표시 번호 (board_id 와 분리된 연속 순번)
		// 예) total 21, 20개/페이지 → 1페이지 21, 2페이지 1
		public int getStartNumber() {
			return totalCount - (currentPage - 1) * PAGE_SIZE;
		}

		// 페이지네이션 블록(한 번에 보여줄 페이지 번호 묶음) 크기
		private static final int BLOCK_SIZE = 10;

		// 현재 페이지가 속한 블록의 시작/끝 페이지 번호
		public int getBlockStart() {
			return ((currentPage - 1) / BLOCK_SIZE) * BLOCK_SIZE + 1;
		}

		public int getBlockEnd() {
			return Math.min(getBlockStart() + BLOCK_SIZE - 1, totalPages);
		}

		// 이전/다음 블록 존재 여부 + 이동할 페이지 번호
		public boolean isHasPrevBlock() {
			return getBlockStart() > 1;
		}

		public boolean isHasNextBlock() {
			return getBlockEnd() < totalPages;
		}

		public int getPrevBlockPage() {
			return getBlockStart() - 1;
		}

		public int getNextBlockPage() {
			return getBlockEnd() + 1;
		}

		public boolean isHasPrev() {
			return currentPage > 1;
		}

		public boolean isHasNext() {
			return currentPage < totalPages;
		}
	}
}

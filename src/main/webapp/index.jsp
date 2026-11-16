<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.musichub.model.MusicVO" %>
<%@ page import="com.musichub.model.BoardVO" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%--
    ============================================================
     index.jsp - MusicHub 메인 대시보드
    ============================================================
     [디자인 - 승민]
     - 헤더와 동일한 다크네이비(#1a1a2e) + 핑크(#e05c97) 톤으로 통일
     - 각 기능 카드가 잘 구분되도록 카드 헤더에 색상 띠 적용
     - Top5 랭킹 / 최신 음악 / 자유게시판(최신 5개)
     - 게시글 클릭 시 해당 글(/board/view?id=N)로 바로 이동
     - 게시판 '바로가기'는 카드 우측 상단으로 이동

     [다국어 - 이서윤]  한/영 전환, message_ko/en.properties

     [데이터] HomeServlet(/home)에서 top5List, latestList, latestBoardList 전달
              직접 접근 시에도 동작하도록 null 방어 + 직접 조회
    ============================================================
--%>

<c:if test="${not empty param.lang}">
    <c:set var="lang" value="${param.lang}" scope="session" />
</c:if>
<c:if test="${empty sessionScope.lang}">
    <c:set var="lang" value="ko" scope="session" />
</c:if>
<fmt:setLocale value="${sessionScope.lang}" scope="request" />
<fmt:setBundle basename="bundle.message" scope="request" />

<%
    List<MusicVO> top5List        = (List<MusicVO>) request.getAttribute("top5List");
    List<MusicVO> latestList      = (List<MusicVO>) request.getAttribute("latestList");
    List<BoardVO> latestBoardList = (List<BoardVO>) request.getAttribute("latestBoardList");

    // HomeServlet 안 거친 경우(/, index.jsp 직접) 직접 조회 → 항상 최신 데이터
    if (top5List == null) {
        top5List = new com.musichub.service.RankingService().getTop5();
    }
    if (latestList == null) {
        java.util.List<MusicVO> allList = new com.musichub.service.MusicService().getList();
        latestList = allList.size() > 4 ? allList.subList(0, 4) : allList;
    }
    if (latestBoardList == null) {
        latestBoardList = new com.musichub.service.BoardService().getLatest(5);
    }
%>

<!DOCTYPE html>
<html lang="${sessionScope.lang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="main.page.title" /></title>
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <style>
        /* ===== 헤더와 통일된 색상 팔레트 ===== */
        :root {
            --navy:    #1a1a2e;  /* 메인 배경/헤더 */
            --navy2:   #24243e;  /* 카드 헤더 띠 */
            --pink:    #e05c97;  /* 강조 */
            --pink-dk: #c94b82;
            --bg:      #16162a;  /* 페이지 배경 */
            --card:    #1f1f38;  /* 카드 배경 */
            --line:    #2e2e4d;
            --text:    #e8e8f0;
            --sub:     #9a9ab5;
        }
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body {
            font-family: "Malgun Gothic", sans-serif;
            background: var(--bg);
            color: var(--text);
        }
        a { text-decoration: none; color: inherit; }

        /* ===== 상단 네비게이션 (헤더와 동일 톤) ===== */
        .navbar {
            background: var(--navy); display: flex; align-items: center;
            padding: 0 24px; height: 56px;
            border-bottom: 1px solid var(--line);
        }
        .navbar .logo { font-size: 18px; font-weight: 700; color: var(--pink); margin-right: 32px; }
        .navbar .logo i { margin-right: 6px; }
        .navbar .menu { display: flex; gap: 22px; font-size: 14px; }
        .navbar .menu a { color: #ddd; }
        .navbar .menu a:hover { color: var(--pink); }
        .navbar .right { margin-left: auto; display: flex; align-items: center; gap: 14px; font-size: 13px; }
        .lang-toggle a { color: #888; font-weight: 600; }
        .lang-toggle a.active { color: #fff; }
        .lang-toggle span { color: #555; margin: 0 2px; }
        .btn-upload { background: var(--pink); color: #fff; padding: 7px 14px; border-radius: 20px; font-weight: 600; }
        .btn-upload:hover { background: var(--pink-dk); }
        .navbar .right a { color: #ddd; }
        .navbar .right .user { color: #fff; }

        .wrap { max-width: 1000px; margin: 24px auto; padding: 0 16px; }

        /* ===== 히어로 배너 ===== */
        .hero {
            background: linear-gradient(135deg, var(--navy) 0%, #2d2d4d 100%);
            border: 1px solid var(--line);
            border-radius: 16px; padding: 48px 20px; text-align: center; margin-bottom: 24px;
        }
        .hero h1 { font-size: 32px; margin-bottom: 10px; color: #fff; }
        .hero p { color: var(--sub); margin-bottom: 22px; font-size: 15px; }
        .hero .btn-hero {
            display: inline-block; background: var(--pink); color: #fff;
            padding: 13px 28px; border-radius: 24px; font-weight: 600; font-size: 15px;
        }
        .hero .btn-hero:hover { background: var(--pink-dk); }

        /* ===== 카드 공통 (기능별 구분 강조) ===== */
        .grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 24px; }
        @media (max-width: 800px) { .grid-2 { grid-template-columns: 1fr; } }
        .card {
            background: var(--card); border: 1px solid var(--line);
            border-radius: 16px; overflow: hidden;
        }
        /* 카드 헤더에 색상 띠 → 기능별 칸이 한눈에 구분됨 */
        .card-head {
            display: flex; align-items: center; justify-content: space-between;
            background: var(--navy2); padding: 14px 18px;
            border-bottom: 2px solid var(--pink);
        }
        .card-head .title { font-size: 16px; font-weight: 700; color: #fff; }
        .card-head .title i { color: var(--pink); margin-right: 8px; }
        .card-head .go {
            font-size: 12px; color: #fff; background: var(--pink);
            padding: 5px 12px; border-radius: 14px; font-weight: 600;
        }
        .card-head .go:hover { background: var(--pink-dk); }
        .card-body { padding: 16px 18px; }

        /* ===== Top5 랭킹 ===== */
        .rank-item { display: flex; align-items: center; gap: 12px; padding: 11px 4px; border-bottom: 1px solid var(--line); }
        .rank-item:last-child { border-bottom: none; }
        .rank-num { font-size: 17px; font-weight: 700; color: var(--pink); width: 20px; text-align: center; }
        .rank-info { flex: 1; min-width: 0; }
        .rank-info .t { font-size: 14px; font-weight: 600; color: var(--text); }
        .rank-info .a { font-size: 12px; color: var(--sub); }
        .rank-views { font-size: 12px; color: var(--sub); }
        .rank-play { width: 28px; height: 28px; border-radius: 50%; background: var(--pink); color: #fff;
            display: flex; align-items: center; justify-content: center; font-size: 11px; }

        /* ===== 최신 음악 ===== */
        .music-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; }
        @media (max-width: 800px) { .music-grid { grid-template-columns: repeat(2, 1fr); } }
        .music-cell { text-align: center; }
        .music-cover { background: var(--navy); border-radius: 10px; height: 92px;
            display: flex; align-items: center; justify-content: center; color: var(--pink); font-size: 30px; margin-bottom: 8px; }
        .music-cover img { width: 100%; height: 100%; object-fit: cover; border-radius: 10px; }
        .music-cell .mt { font-size: 13px; font-weight: 600; color: var(--text); }
        .music-cell .ma { font-size: 11px; color: var(--sub); }

        /* ===== 게시판 미리보기 (최신 5개) ===== */
        .board-list { display: flex; flex-direction: column; }
        .board-row {
            display: flex; align-items: center; gap: 12px;
            padding: 12px 4px; border-bottom: 1px solid var(--line);
        }
        .board-row:last-child { border-bottom: none; }
        .board-row:hover { background: #26264a; }
        .board-row .bt { flex: 1; font-size: 14px; color: var(--text);
            white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
        .board-row .bc { font-size: 12px; color: var(--pink); }
        .board-row .bm { font-size: 12px; color: var(--sub); white-space: nowrap; }

        .empty { text-align: center; color: var(--sub); padding: 28px 0; font-size: 13px; }
        .footer { text-align: center; color: var(--sub); font-size: 12px; padding: 30px 0; }
    </style>
</head>

<body>

<%-- ===== 상단 네비게이션 ===== --%>
<div class="navbar">
    <a href="${pageContext.request.contextPath}/home" class="logo"><i class="fa-solid fa-music"></i>MusicHub</a>
    <div class="menu">
        <a href="${pageContext.request.contextPath}/mypage"><fmt:message key="menu.mypage" /></a>
        <a href="${pageContext.request.contextPath}/music/list"><fmt:message key="menu.music.list" /></a>
        <a href="${pageContext.request.contextPath}/music/ranking"><fmt:message key="menu.ranking" /></a>
        <a href="${pageContext.request.contextPath}/board/list"><fmt:message key="menu.board" /></a>
    </div>
    <div class="right">
        <span class="lang-toggle">
            <a href="#" onclick="mhSetLang('ko');return false;" class="${sessionScope.lang == 'ko' ? 'active' : ''}">KO</a>
            <span>|</span>
            <a href="#" onclick="mhSetLang('en');return false;" class="${sessionScope.lang == 'en' ? 'active' : ''}">EN</a>
        </span>
        <c:choose>
            <c:when test="${not empty sessionScope.loginUserId}">
                <a href="${pageContext.request.contextPath}/music/upload" class="btn-upload">
                    <i class="fa-solid fa-music"></i> <fmt:message key="menu.music.upload" /></a>
                <a href="${pageContext.request.contextPath}/mypage" class="user">${sessionScope.loginNickname}</a>
                <a href="${pageContext.request.contextPath}/logout"><fmt:message key="btn.logout" /></a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login"><fmt:message key="btn.login" /></a>
                <a href="${pageContext.request.contextPath}/signup" class="btn-upload"><fmt:message key="btn.signup" /></a>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<div class="wrap">

    <%-- ===== 히어로 ===== --%>
    <div class="hero">
        <h1>MusicHub</h1>
        <p><fmt:message key="main.guest.desc" /></p>
        <c:choose>
            <c:when test="${not empty sessionScope.loginUserId}">
                <a href="${pageContext.request.contextPath}/music/upload" class="btn-hero">
                    <i class="fa-solid fa-music"></i> <fmt:message key="menu.music.upload" /></a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login" class="btn-hero">
                    <i class="fa-solid fa-music"></i> <fmt:message key="menu.music.upload" /></a>
            </c:otherwise>
        </c:choose>
    </div>

    <%-- ===== Top5 + 최신음악 ===== --%>
    <div class="grid-2">

        <%-- Top5 랭킹 --%>
        <div class="card">
            <div class="card-head">
                <span class="title"><i class="fa-solid fa-trophy"></i><fmt:message key="menu.ranking" /></span>
                <a href="${pageContext.request.contextPath}/music/ranking" class="go"><fmt:message key="main.more" /></a>
            </div>
            <div class="card-body">
<%
            if (top5List != null && !top5List.isEmpty()) {
                int rank = 1;
                for (MusicVO m : top5List) {
%>
                <a href="${pageContext.request.contextPath}/music/detail?music_id=<%= m.getId() %>" class="rank-item">
                    <span class="rank-num"><%= rank %></span>
                    <div class="rank-info">
                        <div class="t"><%= m.getTitle() %></div>
                        <div class="a"><%= m.getArtist() %></div>
                    </div>
                    <span class="rank-play"><i class="fa-solid fa-play"></i></span>
                    <span class="rank-views"><i class="fa-regular fa-eye"></i> <%= m.getViewCount() %></span>
                </a>
<%
                    rank++;
                }
            } else {
%>
                <div class="empty"><fmt:message key="ranking.empty" /></div>
<%          } %>
            </div>
        </div>

        <%-- 최신 음악 --%>
        <div class="card">
            <div class="card-head">
                <span class="title"><i class="fa-solid fa-music"></i><fmt:message key="menu.music.list" /></span>
                <a href="${pageContext.request.contextPath}/music/list" class="go"><fmt:message key="main.more" /></a>
            </div>
            <div class="card-body">
<%
            if (latestList != null && !latestList.isEmpty()) {
%>
                <div class="music-grid">
<%
                for (MusicVO m : latestList) {
                    String thumb = m.getThumbnailPath();
%>
                    <a href="${pageContext.request.contextPath}/music/detail?music_id=<%= m.getId() %>" class="music-cell">
                        <div class="music-cover">
<%                      if (thumb != null && !thumb.trim().isEmpty()) { %>
                            <img src="${pageContext.request.contextPath}/<%= thumb %>"
                                 onerror="this.style.display='none'; this.parentElement.innerHTML='&#9834;';">
<%                      } else { %>
                            &#9834;
<%                      } %>
                        </div>
                        <div class="mt"><%= m.getTitle() %></div>
                        <div class="ma"><%= m.getArtist() %></div>
                    </a>
<%              } %>
                </div>
<%          } else { %>
                <div class="empty"><fmt:message key="ranking.empty" /></div>
<%          } %>
            </div>
        </div>

    </div>

    <%-- ===== 자유게시판 (최신 5개) - 바로가기는 카드 우측 상단 ===== --%>
    <div class="card">
        <div class="card-head">
            <span class="title"><i class="fa-solid fa-clipboard"></i><fmt:message key="menu.board" /></span>
            <%-- [이동] 게시판 바로가기를 카드 우측 상단으로 --%>
            <a href="${pageContext.request.contextPath}/board/list" class="go"><fmt:message key="main.board.goto" /></a>
        </div>
        <div class="card-body">
<%
        if (latestBoardList != null && !latestBoardList.isEmpty()) {
%>
            <div class="board-list">
<%
            for (BoardVO b : latestBoardList) {
%>
                <%-- 게시글 클릭 시 해당 글로 바로 이동 (/board/view?id=N) --%>
                <a href="${pageContext.request.contextPath}/board/view?id=<%= b.getBoardId() %>" class="board-row">
                    <span class="bt"><%= b.getTitle() %></span>
                    <span class="bc"><i class="fa-regular fa-comment"></i> <%= b.getCommentCount() %></span>
                    <span class="bm"><%= b.getWriterNickname() %> · <i class="fa-regular fa-eye"></i> <%= b.getViewCount() %></span>
                </a>
<%
            }
%>
            </div>
<%
        } else {
%>
            <div class="empty"><fmt:message key="board.no.list" /></div>
<%
        }
%>
        </div>
    </div>

</div>

<div class="footer">© <%= java.time.Year.now() %> MusicHub — <fmt:message key="main.footer" /></div>

<script>
// 한/영 전환: 현재 URL 파라미터 유지하며 lang만 교체
function mhSetLang(lang) {
    var url = new URL(window.location.href);
    url.searchParams.set('lang', lang);
    window.location.href = url.toString();
}
</script>
</body>
</html>

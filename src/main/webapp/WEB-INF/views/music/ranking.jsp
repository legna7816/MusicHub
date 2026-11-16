<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ page import="java.util.List" %>
<%@ page import="com.musichub.model.MusicVO" %>
<%--
    ranking.jsp - Top5 랭킹 페이지
    담당: 승민
    역할: 조회수 기준 1~5위 음악 목록 표시 / 1~3위 금·은·동 배지 강조
    데이터: RankingServlet → top5List(List<MusicVO>)
--%>
<%
    List<MusicVO> top5List = (List<MusicVO>) request.getAttribute("top5List");
%>
<%@ include file="/WEB-INF/views/common/i18n-init.jspf" %>
<!DOCTYPE html>
<html lang="${sessionScope.lang}">
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="menu.ranking" /> - MusicHub</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/resources/css/music.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="music-wrapper">

    <h1 class="music-page-title">🏆 <fmt:message key="menu.ranking" /></h1>
    <p class="ranking-subtitle"><fmt:message key="ranking.subtitle" /></p>

    <div class="ranking-list">
    <%
    if (top5List != null && !top5List.isEmpty()) {
        int rank = 1;
        for (MusicVO music : top5List) {
    %>
        <%-- 음악 한 행 - 클릭 시 상세 페이지 이동 --%>
        <a href="<%= request.getContextPath() %>/music/detail?music_id=<%= music.getId() %>"
           class="ranking-row">

            <%-- 순위 배지 (rank-1=금, rank-2=은, rank-3=동, 나머지=회색) --%>
            <div class="ranking-badge rank-<%= rank %>"><%= rank %></div>

            <%-- 썸네일 (없으면 기본 음표) --%>
            <div class="ranking-thumb">
                <% if (music.getThumbnailPath() != null && !music.getThumbnailPath().trim().isEmpty()) { %>
                    <img src="<%= request.getContextPath() %>/<%= music.getThumbnailPath() %>"
                         alt="thumbnail"
                         onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">
                    <span class="default-note" style="display:none;">♫</span>
                <% } else { %>
                    <span class="default-note">♫</span>
                <% } %>
            </div>

            <div class="ranking-info">
                <div class="ranking-title"><%= music.getTitle() %></div>
                <div class="ranking-meta"><%= music.getArtist() %> · <%= music.getGenre() %></div>
            </div>

            <div class="ranking-views">👁 <%= music.getViewCount() %></div>
        </a>
    <%
            rank++;
        }
    } else {
    %>
        <%-- 등록된 음악이 없을 때 --%>
        <div class="empty-result">
            <div class="empty-icon">🎵</div>
            <div class="empty-title"><fmt:message key="ranking.empty" /></div>
            <div class="empty-subtitle"><fmt:message key="ranking.empty.sub" /></div>
        </div>
    <% } %>
    </div>

    <div class="ranking-footer">
        <a href="<%= request.getContextPath() %>/music/list" class="mh-btn-gray"><fmt:message key="music.list.title" /></a>
        <%-- 로그인한 사용자에게만 등록 버튼 표시 --%>
        <% if (session.getAttribute("loginUserId") != null) { %>
            <a href="<%= request.getContextPath() %>/music/upload" class="mh-btn-pink"><fmt:message key="menu.music.upload" /></a>
        <% } %>
    </div>

</div>
</body>
</html>

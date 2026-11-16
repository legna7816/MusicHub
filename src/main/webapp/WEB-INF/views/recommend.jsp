<%@ page language="java"
contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<%@ page import="java.util.List" %>
<%@ page import="com.musichub.model.MusicVO" %>

<%
/*
 * [이서윤 담당]
 * <fmt:message key="recommend.title" /> 페이지 View
 *
 * 역할
 * - RecommendServlet에서 전달받은 추천 음악 목록 출력
 * - 아티스트 기반 추천 목록 출력
 * - 장르 기반 추천 목록 출력
 * - 인기 음악 추천 목록 출력
 *
 * 주의
 * - DB 조회와 추천 로직은 JSP에서 직접 처리하지 않음
 * - 추천 데이터 조회는 Servlet → Service → DAO 구조에서 처리
 */

String nickname = (String)request.getAttribute("nickname");

List<MusicVO> artistList =
    (List<MusicVO>)request.getAttribute("artistList");

List<MusicVO> genreList =
    (List<MusicVO>)request.getAttribute("genreList");

List<MusicVO> popularList =
    (List<MusicVO>)request.getAttribute("popularList");
%>
<%@ include file="/WEB-INF/views/common/i18n-init.jspf" %>

<!DOCTYPE html>
<html lang="${sessionScope.lang}">
<head>
<meta charset="UTF-8">
<title><fmt:message key="recommend.title" /></title>

<%-- [이서윤 담당] 음악 기능 전용 CSS --%>
<link rel="stylesheet"
      href="<%= request.getContextPath() %>/resources/css/recommend.css?v=4">
</head>

<body>
<jsp:include page="/WEB-INF/views/common/header.jsp" />


<%-- <%@ include file="/header.jsp" %> --%>

<div class="recommend-wrapper recommend-page">

    <h1 class="recommend-page-title"><fmt:message key="recommend.subtitle" /></h1>

    <p class="recommend-page-subtitle">
        <fmt:message key="recommend.personalized"><fmt:param value="<%= nickname %>"/></fmt:message>
    </p>

    <%-- ================================
         [이서윤 담당]
         1. 좋아하는 아티스트 기반 추천

         추천 기준
         - 사용자가 좋아요한 음악의 아티스트
         - 사용자가 플레이리스트에 저장한 음악의 아티스트
         - 이미 좋아요/저장한 음악은 DAO에서 제외
    ================================= --%>
    <div class="recommend-section">
        <h2 class="recommend-section-title"><fmt:message key="recommend.artist.title" /></h2>
        <p class="recommend-section-desc">
            <fmt:message key="recommend.artist.desc" />
        </p>

        <div class="recommend-card-row">

<%
if(artistList != null && !artistList.isEmpty()){
    for(MusicVO music : artistList){
%>

            <a href="<%= request.getContextPath() %>/music/detail?music_id=<%= music.getMusicId() %>"
               class="recommend-card-link">

                <div class="recommend-card">
                    <div class="recommend-album-box">

<%
String thumbnailPath = music.getThumbnailPath();

/*
 * 썸네일 출력
 *
 * - thumbnail_path가 있으면 이미지 출력
 * - 이미지가 없거나 로딩 실패 시 기본 음표 아이콘 출력
 */
if(thumbnailPath != null && !thumbnailPath.trim().equals("")){
%>

                        <img src="<%= request.getContextPath() %>/<%= thumbnailPath %>"
                             class="recommend-thumbnail"
                             onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">

                        <span class="recommend-default-note" style="display:none;">
                            ♫
                        </span>

<%
}else{
%>

                        <span class="recommend-default-note">
                            ♫
                        </span>

<%
}
%>

                    </div>

                    <div class="recommend-card-info">
                        <b><%= music.getTitle() %></b>
                        <span>
                            <%= music.getArtist() %>
                            ·
                            <%= music.getGenre() %>
                        </span>
                    </div>
                </div>
            </a>

<%
    }
}else{
%>

            <%-- 추천 결과가 없을 때 표시 --%>
            <p class="recommend-empty-text">
                <fmt:message key="recommend.artist.empty" />
            </p>

<%
}
%>

        </div>
    </div>


    <%-- ================================
         [이서윤 담당]
         2. 좋아하는 장르 기반 추천

         추천 기준
         - 사용자가 좋아요한 음악의 장르
         - 사용자가 플레이리스트에 저장한 음악의 장르
         - 이미 좋아요/저장한 음악은 DAO에서 제외
    ================================= --%>
    <div class="recommend-section">
        <h2 class="recommend-section-title"><fmt:message key="recommend.genre.title" /></h2>
        <p class="recommend-section-desc">
            <fmt:message key="recommend.genre.desc" />
        </p>

        <div class="recommend-card-row">

<%
if(genreList != null && !genreList.isEmpty()){
    for(MusicVO music : genreList){
%>

            <a href="<%= request.getContextPath() %>/music/detail?music_id=<%= music.getMusicId() %>"
               class="recommend-card-link">

                <div class="recommend-card">
                    <div class="recommend-album-box">

<%
String thumbnailPath = music.getThumbnailPath();

/*
 * 썸네일 출력
 *
 * - thumbnail_path가 있으면 이미지 출력
 * - 이미지가 없거나 로딩 실패 시 기본 음표 아이콘 출력
 */
if(thumbnailPath != null && !thumbnailPath.trim().equals("")){
%>

                        <img src="<%= request.getContextPath() %>/<%= thumbnailPath %>"
                             class="recommend-thumbnail"
                             onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">

                        <span class="recommend-default-note" style="display:none;">
                            ♫
                        </span>

<%
}else{
%>

                        <span class="recommend-default-note">
                            ♫
                        </span>

<%
}
%>

                    </div>

                    <div class="recommend-card-info">
                        <b><%= music.getTitle() %></b>
                        <span>
                            <%= music.getArtist() %>
                            ·
                            <%= music.getGenre() %>
                        </span>
                    </div>
                </div>
            </a>

<%
    }
}else{
%>

            <%-- 추천 결과가 없을 때 표시 --%>
            <p class="recommend-empty-text">
                <fmt:message key="recommend.genre.empty" />
            </p>

<%
}
%>

        </div>
    </div>


    <%-- ================================
         [이서윤 담당]
         3. 인기 음악 추천

         추천 기준
         - 전체 좋아요 수
         - 전체 댓글 수
         - 재생 수
         - 이미 좋아요/저장한 음악은 DAO에서 제외
    ================================= --%>
    <div class="recommend-section">
        <h2 class="recommend-section-title"><fmt:message key="recommend.popular.title" /></h2>
        <p class="recommend-section-desc">
            <fmt:message key="recommend.popular.desc" />
        </p>

        <div class="recommend-card-row">

<%
if(popularList != null && !popularList.isEmpty()){
    for(MusicVO music : popularList){
%>

            <a href="<%= request.getContextPath() %>/music/detail?music_id=<%= music.getMusicId() %>"
               class="recommend-card-link">

                <div class="recommend-card">
                    <div class="recommend-album-box">

<%
String thumbnailPath = music.getThumbnailPath();

/*
 * 썸네일 출력
 *
 * - thumbnail_path가 있으면 이미지 출력
 * - 이미지가 없거나 로딩 실패 시 기본 음표 아이콘 출력
 */
if(thumbnailPath != null && !thumbnailPath.trim().equals("")){
%>

                        <img src="<%= request.getContextPath() %>/<%= thumbnailPath %>"
                             class="recommend-thumbnail"
                             onerror="this.style.display='none'; this.nextElementSibling.style.display='flex';">

                        <span class="recommend-default-note" style="display:none;">
                            ♫
                        </span>

<%
}else{
%>

                        <span class="recommend-default-note">
                            ♫
                        </span>

<%
}
%>

                    </div>

                    <div class="recommend-card-info">
                        <b><%= music.getTitle() %></b>
                        <span>
                            <%= music.getArtist() %>
                            ·
                            <%= music.getGenre() %>
                        </span>
                    </div>
                </div>
            </a>

<%
    }
}else{
%>

            <%-- 추천 결과가 없을 때 표시 --%>
            <p class="recommend-empty-text">
                <fmt:message key="recommend.popular.empty" />
            </p>

<%
}
%>

        </div>
    </div>

</div>

<%-- <%@ include file="/footer.jsp" %> --%>

</body>
</html>
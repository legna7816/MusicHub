<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%--
    upload.jsp - 음악 등록 폼
    담당: 승민
    역할: 제목/아티스트/장르/음악파일/썸네일 입력 후 POST /music/upload 전송
    [수정] 세션키 loginUserId 로 통일 (조원 LoginServlet 기준)
--%>
<%
    // [수정] loginUserId(int) 기준으로 로그인 체크
    if (session.getAttribute("loginUserId") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    String errorMsg = (String) request.getAttribute("errorMsg");

    // [추가] 현재 등록 개수 / 최대 개수 (Servlet에서 전달)
    Integer uploadCount = (Integer) request.getAttribute("uploadCount");
    Integer maxUpload   = (Integer) request.getAttribute("maxUpload");
    if (uploadCount == null) uploadCount = 0;
    if (maxUpload   == null) maxUpload   = 3;
    boolean limitReached = uploadCount >= maxUpload; // 등록 한도 도달 여부
%>
<%@ include file="/WEB-INF/views/common/i18n-init.jspf" %>
<!DOCTYPE html>
<html lang="${sessionScope.lang}">
<head>
    <meta charset="UTF-8">
    <title><fmt:message key="menu.music.upload" /> - MusicHub</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/resources/css/music.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="music-wrapper">

    <h1 class="music-page-title">🎵 <fmt:message key="menu.music.upload" /></h1>

    <%-- [추가] 현재 등록 개수 표시 (예: 등록 현황 2 / 3개) --%>
    <p style="color:#888; font-size:0.9rem; margin-bottom:1rem;">
        <fmt:message key="upload.status" /> <strong style="color:#e05c97;"><%= uploadCount %></strong> / <%= maxUpload %>
    </p>

    <%-- 등록 실패 시 오류 메시지 --%>
    <% if (errorMsg != null) { %>
        <div class="error-msg"><%= errorMsg %></div>
    <% } %>

    <%-- [추가] 등록 한도 도달 시 폼 대신 안내 표시 --%>
    <% if (limitReached) { %>
        <div class="error-msg">
            <fmt:message key="upload.limit.msg"><fmt:param value="<%= maxUpload %>"/></fmt:message>
            <fmt:message key="upload.limit.sub" />
        </div>
        <a href="<%= request.getContextPath() %>/music/list" class="mh-btn-gray"><fmt:message key="upload.back.list" /></a>
    <% } else { %>

    <%-- enctype="multipart/form-data" : 파일 업로드 필수 설정 --%>
    <form action="<%= request.getContextPath() %>/music/upload"
          method="post" enctype="multipart/form-data" class="music-form">

        <div class="form-group">
            <label for="title"><fmt:message key="upload.title.label" /> *</label>
            <input type="text" id="title" name="title" placeholder="<fmt:message key="upload.title.ph" />" required>
        </div>

        <div class="form-group">
            <label for="artist"><fmt:message key="upload.artist.label" /> *</label>
            <input type="text" id="artist" name="artist" placeholder="<fmt:message key="upload.artist.ph" />" required>
        </div>

        <%-- 이서윤 조원 검색 필터 장르값과 통일 --%>
        <div class="form-group">
            <label for="genre"><fmt:message key="upload.genre.label" /> *</label>
            <select id="genre" name="genre" required>
                <option value=""><fmt:message key="upload.genre.select" /></option>
                <option value="KPOP">KPOP</option>
                <option value="Pop">Pop</option>
                <option value="Ballad">Ballad</option>
                <option value="HipHop">HipHop</option>
                <option value="Jazz">Jazz</option>
                <option value="Classic">Classic</option>
                <option value="Rock">Rock</option>
                <option value="Other">Other</option>
            </select>
        </div>

        <%-- 음악 파일 (필수) - audio/* 로 오디오만 선택 가능 --%>
        <div class="form-group">
            <label for="musicFile"><fmt:message key="upload.file.label" /> *</label>
            <input type="file" id="musicFile" name="musicFile" accept="audio/*" required>
            <span class="file-name" id="musicFileName"><fmt:message key="upload.file.select" /></span>
        </div>

        <%-- 썸네일 (선택) --%>
        <div class="form-group">
            <label for="thumbnailFile"><fmt:message key="upload.cover" /></label>
            <input type="file" id="thumbnailFile" name="thumbnailFile" accept="image/*">
            <img id="thumbnailPreview" class="thumbnail-preview" alt="preview">
        </div>

        <div class="form-buttons">
            <button type="submit" class="mh-btn-pink"><fmt:message key="upload.submit" /></button>
            <a href="<%= request.getContextPath() %>/music/list" class="mh-btn-gray"><fmt:message key="upload.cancel" /></a>
        </div>
    </form>
    <% } %> <%-- [추가] limitReached else 블록 종료 --%>
</div>

<script>
// [추가] 한도 도달 시 폼이 없으므로 요소 존재 여부 먼저 확인
var musicFileInput = document.getElementById("musicFile");
var thumbnailInput = document.getElementById("thumbnailFile");

// 음악 파일 선택 시 파일명 표시
if (musicFileInput) {
    musicFileInput.addEventListener("change", function() {
        document.getElementById("musicFileName").textContent =
            this.files[0] ? this.files[0].name : "<fmt:message key="upload.file.select" />";
    });
}

// 썸네일 선택 시 미리보기 표시
if (thumbnailInput) {
    thumbnailInput.addEventListener("change", function() {
        if (!this.files[0]) return;
        const reader = new FileReader();
        reader.onload = function(e) {
            const p = document.getElementById("thumbnailPreview");
            p.src = e.target.result;
            p.style.display = "block";
        };
        reader.readAsDataURL(this.files[0]);
    });
}
</script>
</body>
</html>

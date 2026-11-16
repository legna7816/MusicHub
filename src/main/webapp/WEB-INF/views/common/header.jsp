<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%--
    header.jsp - 모든 페이지 공통 상단 네비게이션 바
    [담당] 승민(공통) + 이서윤(다국어)
    - 로고 클릭 시 메인(/home) 이동
    - 우측 상단 한/영(KO/EN) 전환
    - 로그인 상태별 메뉴 표시
    [사용법] 각 JSP <body> 다음에:
      <jsp:include page="/WEB-INF/views/common/header.jsp" />
--%>
<c:if test="${not empty param.lang}">
    <c:set var="lang" value="${param.lang}" scope="session" />
</c:if>
<c:if test="${empty sessionScope.lang}">
    <c:set var="lang" value="ko" scope="session" />
</c:if>
<fmt:setLocale value="${sessionScope.lang}" scope="request" />
<%-- scope="request"로 설정해야 include 이후 각 페이지 본문에서도 번들이 유지됨 --%>
<fmt:setBundle basename="bundle.message" scope="request" />

<style>
    .mh-header { background:#1a1a2e; color:#fff; display:flex; align-items:center;
        padding:0 24px; height:56px; font-family:"Malgun Gothic",sans-serif; }
    .mh-header .mh-logo { font-size:18px; font-weight:700; color:#e05c97;
        margin-right:32px; text-decoration:none; cursor:pointer; }
    .mh-header .mh-nav { display:flex; gap:22px; font-size:14px; }
    .mh-header .mh-nav a { color:#ddd; text-decoration:none; }
    .mh-header .mh-nav a:hover { color:#e05c97; }
    .mh-header .mh-right { margin-left:auto; display:flex; align-items:center;
        gap:14px; font-size:13px; }
    .mh-lang a { color:#888; font-weight:600; text-decoration:none; }
    .mh-lang a.active { color:#fff; }
    .mh-lang span { color:#555; margin:0 3px; }
    .mh-btn-up { background:#e05c97; color:#fff; padding:7px 14px;
        border-radius:20px; font-weight:600; text-decoration:none; }
    .mh-btn-up:hover { background:#c94b82; }
    .mh-header .mh-right a { color:#ddd; text-decoration:none; }
    .mh-header .mh-user { color:#fff; }
</style>

<div class="mh-header">
    <a href="${pageContext.request.contextPath}/home" class="mh-logo">🎵 MusicHub</a>
    <nav class="mh-nav">
        <a href="${pageContext.request.contextPath}/mypage"><fmt:message key="menu.mypage" /></a>
        <a href="${pageContext.request.contextPath}/music/list"><fmt:message key="menu.music.list" /></a>
        <a href="${pageContext.request.contextPath}/music/ranking"><fmt:message key="menu.ranking" /></a>
        <a href="${pageContext.request.contextPath}/board/list"><fmt:message key="menu.board" /></a>
    </nav>
    <div class="mh-right">
        <span class="mh-lang">
            <%-- [개선] 현재 URL의 기존 파라미터(music_id 등)를 유지하면서 lang만 교체 --%>
            <a href="#" onclick="mhSetLang('ko'); return false;"
               class="${sessionScope.lang == 'ko' ? 'active' : ''}">KO</a>
            <span>|</span>
            <a href="#" onclick="mhSetLang('en'); return false;"
               class="${sessionScope.lang == 'en' ? 'active' : ''}">EN</a>
        </span>
        <c:choose>
            <c:when test="${not empty sessionScope.loginUserId}">
                <a href="${pageContext.request.contextPath}/music/upload" class="mh-btn-up">
                    <fmt:message key="menu.music.upload" /></a>
                <%-- [추가] 마이페이지 링크 (닉네임 클릭 시 /mypage 이동) --%>
                <a href="${pageContext.request.contextPath}/mypage" class="mh-user">
                    ${sessionScope.loginNickname}</a>
                <a href="${pageContext.request.contextPath}/logout"><fmt:message key="btn.logout" /></a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login"><fmt:message key="btn.login" /></a>
                <a href="${pageContext.request.contextPath}/signup" class="mh-btn-up">
                    <fmt:message key="btn.signup" /></a>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script>
/*
 * [한/영 전환] 현재 페이지의 기존 파라미터를 모두 유지하면서 lang만 교체
 * 예: /music/detail?music_id=5 에서 EN 클릭
 *     → /music/detail?music_id=5&lang=en (music_id 유지됨)
 */
function mhSetLang(lang) {
    var url = new URL(window.location.href);
    url.searchParams.set('lang', lang); // lang만 교체, 나머지 파라미터 유지
    window.location.href = url.toString();
}
</script>

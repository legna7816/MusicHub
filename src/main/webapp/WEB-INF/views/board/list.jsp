<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>
<%@ include file="/WEB-INF/views/common/i18n-init.jspf" %>

<!DOCTYPE html>
<html lang="${sessionScope.lang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="board.list.title" /></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/board.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="wrap">
    <div class="head">
        <div class="head-left">
            <h1>📋<fmt:message key="board.list.title" /></h1>
        </div>
        <%-- 로그인한 사용자에게만 글쓰기 버튼 노출 --%>
        <c:if test="${not empty sessionScope.loginUserId}">
            <a class="btn" href="${pageContext.request.contextPath}/board/write"><fmt:message key="board.write" /></a>
        </c:if>
    </div>

    <table>
        <thead>
            <tr>
                <th class="num"><fmt:message key="board.num" /></th>
                <th><fmt:message key="board.title" /></th>
                <th class="writer-th" style="width:160px"><fmt:message key="board.writer" /></th>
                <th class="cnt"><fmt:message key="board.views" /></th>
                <th class="date"><fmt:message key="board.date" /></th>
            </tr>
        </thead>
        <tbody>
            <c:choose>
                <c:when test="${empty page.list}">
                    <tr><td colspan="5" class="empty"><fmt:message key="board.no.list" /></td></tr>
                </c:when>
                <c:otherwise>
                    <c:forEach var="b" items="${page.list}" varStatus="st">
                        <tr>
                            <td class="num">${page.startNumber - st.index}</td>
                            <td class="title-cell">
                                <a href="${pageContext.request.contextPath}/board/view?id=${b.boardId}">
                                   <c:out value="${b.title}"/><c:if test="${b.commentCount > 0}"><span class="cmt-badge">[${b.commentCount}]</span></c:if>
                                </a>
                            </td>
                            <td class="writer"><c:out value="${b.writerNickname}"/></td>
                            <td class="cnt">${b.viewCount}</td>
                            <td class="date">
                                <fmt:formatDate value="${b.createdAt}" pattern="yyyy-MM-dd"/>
                            </td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>

    <%-- 검색 바 (콤보박스 카테고리 + 키워드) --%>
    <form class="search-bar" method="get" action="${pageContext.request.contextPath}/board/list">
        <select name="type">
            <option value="all"          ${param.type == 'all'          ? 'selected' : ''}><fmt:message key="board.all" /></option>
            <option value="titlecontent" ${param.type == 'titlecontent' ? 'selected' : ''}><fmt:message key="board.title" />+<fmt:message key="board.content" /></option>
            <option value="title"        ${param.type == 'title'        ? 'selected' : ''}><fmt:message key="board.title" /></option>
            <option value="content"      ${param.type == 'content'      ? 'selected' : ''}><fmt:message key="board.content" /></option>
            <option value="writer"       ${param.type == 'writer'       ? 'selected' : ''}><fmt:message key="board.writer2" /></option>
            <option value="comment"      ${param.type == 'comment'      ? 'selected' : ''}><fmt:message key="board.comment" /></option>
        </select>
        <input type="text" name="keyword" value="${fn:escapeXml(param.keyword)}"
               placeholder="<fmt:message key="board.search.ph" />">
        <button type="submit"><fmt:message key="music.search" /></button>
    </form>

    <%-- 페이지네이션 (10개 블록 단위, 가운데 정렬) --%>
    <div class="pg">
        <c:if test="${page.hasPrevBlock}">
            <c:url var="firstBlockUrl" value="/board/list">
                <c:param name="type"    value="${param.type}"/>
                <c:param name="keyword" value="${param.keyword}"/>
                <c:param name="page"    value="${page.prevBlockPage}"/>
            </c:url>
            <a href="${firstBlockUrl}" title="<fmt:message key="page.prev.block" />">&laquo;</a>
        </c:if>
        <c:if test="${page.hasPrev}">
            <c:url var="prevUrl" value="/board/list">
                <c:param name="type"    value="${param.type}"/>
                <c:param name="keyword" value="${param.keyword}"/>
                <c:param name="page"    value="${page.currentPage - 1}"/>
            </c:url>
            <a href="${prevUrl}" title="<fmt:message key="page.prev" />">&lsaquo;</a>
        </c:if>

        <c:forEach var="i" begin="${page.blockStart}" end="${page.blockEnd}">
            <c:choose>
                <c:when test="${i == page.currentPage}">
                    <span class="cur">${i}</span>
                </c:when>
                <c:otherwise>
                    <c:url var="pageUrl" value="/board/list">
                        <c:param name="type"    value="${param.type}"/>
                        <c:param name="keyword" value="${param.keyword}"/>
                        <c:param name="page"    value="${i}"/>
                    </c:url>
                    <a href="${pageUrl}">${i}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>

        <c:if test="${page.hasNext}">
            <c:url var="nextUrl" value="/board/list">
                <c:param name="type"    value="${param.type}"/>
                <c:param name="keyword" value="${param.keyword}"/>
                <c:param name="page"    value="${page.currentPage + 1}"/>
            </c:url>
            <a href="${nextUrl}" title="<fmt:message key="page.next" />">&rsaquo;</a>
        </c:if>
        <c:if test="${page.hasNextBlock}">
            <c:url var="lastBlockUrl" value="/board/list">
                <c:param name="type"    value="${param.type}"/>
                <c:param name="keyword" value="${param.keyword}"/>
                <c:param name="page"    value="${page.nextBlockPage}"/>
            </c:url>
            <a href="${lastBlockUrl}" title="<fmt:message key="page.next.block" />">&raquo;</a>
        </c:if>
    </div>
</div>
</body>
</html>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/views/common/i18n-init.jspf" %>

<!DOCTYPE html>
<html lang="${sessionScope.lang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${board.title}"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/board.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="wrap">
    <div class="head">
        <div class="head-left">
            <a class="btn-home" href="${pageContext.request.contextPath}/" title="<fmt:message key="board.home" />">🏠</a>
            <h1><fmt:message key="board.list.title" /></h1>
        </div>
    </div>

    <div class="card">
        <p class="post-title"><c:out value="${board.title}"/></p>
        <div class="meta">
            <span><fmt:message key="board.writer" />: <c:out value="${board.writerNickname}"/></span>
            <span><fmt:message key="board.views" />: ${board.viewCount}</span>
            <span><fmt:message key="board.date" />: <fmt:formatDate value="${board.createdAt}" pattern="yyyy-MM-dd HH:mm"/></span>
        </div>
        <div class="content"><c:out value="${board.content}"/></div>

        <div class="actions">
            <a class="btn btn-list" href="${pageContext.request.contextPath}/board/list"><fmt:message key="common.back" /></a>
            <c:if test="${loginUserId == board.userId}">
                <a class="btn btn-edit"
                   href="${pageContext.request.contextPath}/board/edit?id=${board.boardId}"><fmt:message key="board.edit" /></a>
                <form method="post"
                      action="${pageContext.request.contextPath}/board/delete"
                      style="margin:0;"
                      onsubmit="return confirm('<fmt:message key="board.delete.confirm" />');">
                    <input type="hidden" name="id" value="${board.boardId}"/>
                    <button type="submit" class="btn btn-del"><fmt:message key="board.delete" /></button>
                </form>
            </c:if>
        </div>

        <!-- ===== 댓글 ===== -->
        <div class="comments">
            <h2><fmt:message key="board.comment" /> <span>${comments.size()}</span></h2>

            <c:forEach var="cmt" items="${comments}">
                <div class="cmt">
                    <div class="cmt-head">
                        <strong><c:out value="${cmt.writerNickname}"/></strong>
                        <span class="date">
                            <fmt:formatDate value="${cmt.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                        </span>
                        <c:if test="${loginUserId == cmt.userId}">
                            <form method="post" style="display:inline;"
                                  action="${pageContext.request.contextPath}/board/comment/delete"
                                  onsubmit="return confirm('<fmt:message key="board.comment.delete.confirm" />');">
                                <input type="hidden" name="commentId" value="${cmt.commentId}"/>
                                <input type="hidden" name="boardId" value="${board.boardId}"/>
                                <button type="submit" class="del"><fmt:message key="board.delete" /></button>
                            </form>
                        </c:if>
                    </div>
                    <div class="cmt-body"><c:out value="${cmt.content}"/></div>
                </div>
            </c:forEach>

            <c:if test="${empty comments}">
                <p class="cmt-empty"><fmt:message key="board.no.comment" /></p>
            </c:if>

            <%-- 작성 폼: 로그인 시에만 노출 --%>
            <c:if test="${not empty loginUserId}">
                <form class="cmt-form" method="post"
                      action="${pageContext.request.contextPath}/board/comment/write">
                    <input type="hidden" name="boardId" value="${board.boardId}"/>
                    <textarea name="content" rows="3" maxlength="500" required
                              placeholder="<fmt:message key="board.comment.ph" />"></textarea>
                    <div class="bar">
                        <button type="submit" class="submit"><fmt:message key="music.comment.write" /></button>
                    </div>
                </form>
            </c:if>
            <c:if test="${empty loginUserId}">
                <p class="cmt-login">
                    <a href="${pageContext.request.contextPath}/login"><fmt:message key="board.comment.login.full" /></a>
                </p>
            </c:if>
        </div>
    </div>
</div>
</body>
</html>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/views/common/i18n-init.jspf" %>

<c:set var="isEdit" value="${mode == 'edit'}" />
<fmt:message var="formTitle" key="${isEdit ? 'board.form.edit' : 'board.form.write'}" />
<!DOCTYPE html>
<html lang="${sessionScope.lang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${formTitle}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/board.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="form-card">
    <h1>${formTitle}</h1>

    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>

    <%-- 수정이면 /board/edit, 작성이면 /board/write 로 전송 --%>
    <form action="${pageContext.request.contextPath}/board/${isEdit ? 'edit' : 'write'}"
          method="post">

        <%-- 수정 모드면 어떤 글인지 id 를 hidden 으로 함께 전송 --%>
        <c:if test="${isEdit}">
            <input type="hidden" name="id" value="${board.boardId}">
        </c:if>

        <label for="title"><fmt:message key="board.title" /></label>
        <input type="text" id="title" name="title" maxlength="200" required
               value="<c:out value='${isEdit ? board.title : title}'/>">

        <label for="content"><fmt:message key="board.content" /></label>
        <textarea id="content" name="content" required><c:out
                value="${isEdit ? board.content : content}"/></textarea>

        <div class="actions">
            <a class="btn btn-gray"
               href="${pageContext.request.contextPath}/board/list"><fmt:message key="common.cancel" /></a>
            <button type="submit" class="btn">
                <fmt:message key="${isEdit ? 'board.form.submit.done' : 'board.submit'}" />
            </button>
        </div>
    </form>
</div>
</body>
</html>

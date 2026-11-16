<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/views/common/i18n-init.jspf" %>

<!DOCTYPE html>
<html lang="${sessionScope.lang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="mypage.title" /></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/user.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="mypage-box">
    <h1><fmt:message key="mypage.title" /></h1>

    <c:if test="${not empty msg}">
        <div class="msg">${msg}</div>
    </c:if>

    <p class="readonly"><fmt:message key="mypage.label.id" />: ${user.username}</p>
    <p class="readonly"><fmt:message key="mypage.label.email" />: ${user.email}</p>

    <%-- 1) 프로필 수정 --%>
    <h2><fmt:message key="mypage.info.edit" /></h2>
    <form action="${pageContext.request.contextPath}/mypage" method="post">
        <input type="hidden" name="action" value="profile">

        <label for="nickname"><fmt:message key="mypage.nickname" /></label>
        <input type="text" id="nickname" name="nickname"
               value="${user.nickname}" maxlength="50" required>

        <button type="submit" class="save"><fmt:message key="mypage.save" /></button>
    </form>

    <%-- 2) <fmt:message key="mypage.pw.change" /> --%>
    <h2><fmt:message key="mypage.pw.change" /></h2>
    <form action="${pageContext.request.contextPath}/mypage" method="post">
        <input type="hidden" name="action" value="password">

        <label for="currentPassword"><fmt:message key="mypage.pw.current" /></label>
        <input type="password" id="currentPassword" name="currentPassword" required>

        <label for="newPassword"><fmt:message key="mypage.pw.new" /></label>
        <input type="password" id="newPassword" name="newPassword"
               minlength="8" required>

        <button type="submit" class="save"><fmt:message key="mypage.pw.change" /></button>
    </form>

    <%-- 3) <fmt:message key="mypage.withdraw" /> --%>
    <h2><fmt:message key="mypage.withdraw" /></h2>
    <form action="${pageContext.request.contextPath}/mypage" method="post"
          onsubmit="return confirm('<fmt:message key="mypage.withdraw.confirm" />');">
        <input type="hidden" name="action" value="withdraw">
        <button type="submit" class="danger"><fmt:message key="mypage.withdraw" /></button>
    </form>
</div>
</body>
</html>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/views/common/i18n-init.jspf" %>

<!DOCTYPE html>
<html lang="${sessionScope.lang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="login.title" /></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/user.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="auth-box">
    <h1><fmt:message key="login.title" /></h1>

    <c:if test="${not empty notice}">
        <div class="notice">${notice}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <label for="loginId"><fmt:message key="login.id" /></label>
        <input type="text" id="loginId" name="loginId"
               value="${loginId}" required>

        <label for="password"><fmt:message key="login.password" /></label>
        <input type="password" id="password" name="password" required>

        <button type="submit"><fmt:message key="login.submit" /></button>
    </form>

    <div class="links">
        <fmt:message key="login.no.account" />
        <a href="${pageContext.request.contextPath}/signup"><fmt:message key="login.to.signup" /></a>
    </div>
</div>
</body>
</html>

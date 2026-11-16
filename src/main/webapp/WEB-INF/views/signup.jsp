<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/views/common/i18n-init.jspf" %>

<!DOCTYPE html>
<html lang="${sessionScope.lang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="signup.title" /></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/user.css">
</head>
<body>
<jsp:include page="/WEB-INF/views/common/header.jsp" />

<div class="auth-box">
    <h1><fmt:message key="signup.title" /></h1>

    <%-- 오류 메시지 (있을 때만) --%>
    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>

    <%-- action 은 서블릿 매핑(/signup), method 는 POST --%>
    <form action="${pageContext.request.contextPath}/signup" method="post">
        <label for="username"><fmt:message key="signup.username.guide" /></label>
        <input type="text" id="username" name="username"
               value="${username}" pattern="[A-Za-z0-9_]{4,20}"
               maxlength="20" required>

        <label for="email"><fmt:message key="signup.email" /></label>
        <input type="email" id="email" name="email"
               value="${email}" required>

        <label for="password"><fmt:message key="signup.password.guide" /></label>
        <input type="password" id="password" name="password"
               minlength="8" required>

        <label for="confirmPassword"><fmt:message key="signup.password.confirm" /></label>
        <input type="password" id="confirmPassword" name="confirmPassword"
               minlength="8" required>

        <label for="nickname"><fmt:message key="signup.nickname" /></label>
        <input type="text" id="nickname" name="nickname"
               value="${nickname}" maxlength="50" required>

        <button type="submit"><fmt:message key="signup.submit" /></button>
    </form>

    <div class="links">
        <fmt:message key="signup.have.account" />
        <a href="${pageContext.request.contextPath}/login"><fmt:message key="login.title" /></a>
    </div>
</div>
</body>
</html>

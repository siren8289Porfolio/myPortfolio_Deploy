<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Briefly - 회원가입</title>
    <%@ include file="/WEB-INF/views/common/nav.jsp" %>
</head>
<body>
<div class="page">
    <div class="card">
        <h1>회원가입</h1>
        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error">${error}</div>
        <% } %>
        <form class="form" method="post" action="${pageContext.request.contextPath}/signup">
            <label>이메일 <input type="email" name="email" required></label>
            <label>비밀번호 <input type="password" name="password" required></label>
            <label>이름 <input type="text" name="name" required></label>
            <button type="submit">가입</button>
        </form>
        <p class="form-footer">이미 계정이 있으신가요? <a href="${pageContext.request.contextPath}/login">로그인</a></p>
    </div>
</div>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Briefly - 오류</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="error-page">
    <div class="code">⚠️</div>
    <h1>오류가 발생했습니다</h1>
    <p class="alert alert-error">${error}</p>
    <p><a href="${pageContext.request.contextPath}/funds">홈으로</a></p>
</div>
</body>
</html>

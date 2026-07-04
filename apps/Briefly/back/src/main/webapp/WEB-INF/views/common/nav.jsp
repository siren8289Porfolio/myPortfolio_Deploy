<%@ page pageEncoding="UTF-8" %>
<%@ page import="com.briefly.auth.entity.User" %>
<%
    User navUser = (User) session.getAttribute("loginUser");
%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<div class="topbar">
    <span class="brand">Briefly</span>
    <nav>
        <a href="${pageContext.request.contextPath}/funds">투자상품</a>
<%
    if (navUser != null) {
%>
        <a href="${pageContext.request.contextPath}/applications">신청내역</a>
        <a href="${pageContext.request.contextPath}/alerts">위험알림</a>
<%
        if (User.Role.ADMIN == navUser.getRole()) {
%>
        <a href="${pageContext.request.contextPath}/admin/funds">관리자</a>
<%
        }
%>
        <span><%= navUser.getName() %>님</span>
        <form class="logout-form" method="post" action="${pageContext.request.contextPath}/logout">
            <button type="submit">로그아웃</button>
        </form>
<%
    } else {
%>
        <a href="${pageContext.request.contextPath}/login">로그인</a>
        <a href="${pageContext.request.contextPath}/signup">회원가입</a>
<%
    }
%>
    </nav>
</div>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.briefly.application.dto.ApplicationDto" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Briefly - 신청 내역</title>
    <%@ include file="/WEB-INF/views/common/nav.jsp" %>
</head>
<body>
<div class="page">
    <div class="card">
        <h1>모의가입 신청 내역</h1>
<%
    List<ApplicationDto> applications = (List<ApplicationDto>) request.getAttribute("applications");
    if (applications == null || applications.isEmpty()) {
%>
        <div class="empty-state">신청 내역이 없습니다.</div>
<%
    } else {
%>
        <ul class="item-list">
<%
        for (ApplicationDto app : applications) {
%>
            <li class="item">
                <span class="item-title">상품 #<%= app.getFundId() %></span>
                <span class="badge badge-status-<%= app.getStatus() %>"><%= app.getStatus() %></span>
                <p class="item-meta"><%= app.getAmount() %>원</p>
            </li>
<%
        }
%>
        </ul>
<%
    }
%>
    </div>
    <div class="card">
        <p><a href="${pageContext.request.contextPath}/funds">상품 목록</a></p>
    </div>
</div>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.briefly.alert.dto.AlertDto" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Briefly - 위험 알림</title>
    <%@ include file="/WEB-INF/views/common/nav.jsp" %>
</head>
<body>
<div class="page">
    <div class="card">
        <h1>위험 알림</h1>
<%
    List<AlertDto> alerts = (List<AlertDto>) request.getAttribute("alerts");
    if (alerts == null || alerts.isEmpty()) {
%>
        <div class="empty-state">관심상품 기준 알림이 없습니다.</div>
<%
    } else {
%>
        <ul class="item-list">
<%
        for (AlertDto alert : alerts) {
%>
            <li class="item">
                <span class="item-title"><%= alert.getTitle() %></span>
                <p class="item-meta">상품 #<%= alert.getFundId() %> · 등급 변경 <%= alert.getPreviousGrade() %> → <%= alert.getNewGrade() %></p>
                <p class="item-body"><%= alert.getMessage() %></p>
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

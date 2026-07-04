<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.briefly.report.dto.ReportDto" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Briefly - 운용 브리프</title>
    <%@ include file="/WEB-INF/views/common/nav.jsp" %>
</head>
<body>
<div class="page">
    <div class="card">
        <h1>운용 브리프 (상품 #${fundId})</h1>
<%
    List<ReportDto> reports = (List<ReportDto>) request.getAttribute("reports");
    if (reports == null || reports.isEmpty()) {
%>
        <div class="empty-state">등록된 브리프가 없습니다.</div>
<%
    } else {
%>
        <ul class="item-list">
<%
        for (ReportDto report : reports) {
%>
            <li class="item">
                <span class="item-title"><%= report.getTitle() %></span>
                <p class="item-meta"><%= report.getReportDate() %></p>
                <p class="item-body"><%= report.getContent() %></p>
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
        <p><a href="${pageContext.request.contextPath}/funds/detail?id=${fundId}">상품 상세로</a></p>
    </div>
</div>
</body>
</html>

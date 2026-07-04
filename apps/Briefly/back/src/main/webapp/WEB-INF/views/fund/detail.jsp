<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.briefly.fund.dto.FundDto" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Briefly - 상품 상세</title>
    <%@ include file="/WEB-INF/views/common/nav.jsp" %>
</head>
<body>
<%
    FundDto fund = (FundDto) request.getAttribute("fund");
    Boolean watched = (Boolean) request.getAttribute("watched");
%>
<div class="page">
    <div class="card">
        <h1><%= fund.getName() %> <span class="badge badge-risk-<%= fund.getRiskGrade() %>">위험등급 <%= fund.getRiskGrade() %></span></h1>
        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error">${error}</div>
        <% } %>
        <p class="item-body"><%= fund.getDescription() %></p>
        <p class="item-meta">예상 수익률 <%= fund.getExpectedReturn() %>%</p>

        <% if (session.getAttribute("loginUser") != null) { %>
        <div class="toolbar" style="display:flex; gap:8px; margin-top:16px;">
            <form method="post" action="${pageContext.request.contextPath}/watchlist/toggle">
                <input type="hidden" name="fundId" value="<%= fund.getId() %>">
                <button type="submit"><%= Boolean.TRUE.equals(watched) ? "관심 해제" : "관심 등록" %></button>
            </form>
        </div>
        <form class="form" style="margin-top:16px;" method="post" action="${pageContext.request.contextPath}/applications">
            <input type="hidden" name="fundId" value="<%= fund.getId() %>">
            <label>신청 금액 <input type="number" name="amount" min="1" step="1000" required></label>
            <button type="submit">모의가입 신청</button>
        </form>
        <% } else { %>
        <p class="form-footer"><a href="${pageContext.request.contextPath}/login">로그인</a> 후 관심등록·모의가입이 가능합니다.</p>
        <% } %>
    </div>
    <div class="card">
        <p><a href="${pageContext.request.contextPath}/reports?fundId=<%= fund.getId() %>">운용 브리프 보기</a> &nbsp;|&nbsp; <a href="${pageContext.request.contextPath}/funds">목록으로</a></p>
    </div>
</div>
</body>
</html>

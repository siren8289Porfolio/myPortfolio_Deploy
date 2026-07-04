<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.briefly.fund.dto.FundDto" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Briefly - 투자상품</title>
    <%@ include file="/WEB-INF/views/common/nav.jsp" %>
</head>
<body>
<div class="page">
    <div class="card">
        <h1>투자상품 목록</h1>
<%
    List<FundDto> funds = (List<FundDto>) request.getAttribute("funds");
    if (funds == null || funds.isEmpty()) {
%>
        <div class="empty-state">등록된 상품이 없습니다.</div>
<%
    } else {
%>
        <ul class="item-list">
<%
        for (FundDto fund : funds) {
%>
            <li>
                <a class="item-link" href="${pageContext.request.contextPath}/funds/detail?id=<%= fund.getId() %>">
                    <%= fund.getName() %>
                    <span class="badge badge-risk-<%= fund.getRiskGrade() %>">위험등급 <%= fund.getRiskGrade() %></span>
                    &nbsp;예상수익 <%= fund.getExpectedReturn() %>%
                </a>
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
        <p><a href="${pageContext.request.contextPath}/applications">내 신청 내역</a> &nbsp;|&nbsp; <a href="${pageContext.request.contextPath}/alerts">위험 알림</a></p>
    </div>
</div>
</body>
</html>

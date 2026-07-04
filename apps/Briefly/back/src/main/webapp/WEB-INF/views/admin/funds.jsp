<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.briefly.fund.dto.FundDto" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Briefly - 관리자 상품</title>
    <%@ include file="/WEB-INF/views/common/nav.jsp" %>
</head>
<body>
<div class="page">
    <div class="card">
        <h1>관리자 - 상품 관리</h1>
        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error">${error}</div>
        <% } %>
<%
    List<FundDto> funds = (List<FundDto>) request.getAttribute("funds");
    if (funds == null || funds.isEmpty()) {
%>
        <div class="empty-state">등록된 상품이 없습니다.</div>
<%
    } else {
%>
        <table>
            <thead>
            <tr><th>상품명</th><th>위험등급</th><th>상태</th></tr>
            </thead>
            <tbody>
<%
        for (FundDto fund : funds) {
%>
            <tr>
                <td><%= fund.getName() %></td>
                <td><span class="badge badge-risk-<%= fund.getRiskGrade() %>"><%= fund.getRiskGrade() %></span></td>
                <td><span class="badge badge-status-<%= fund.getStatus() %>"><%= fund.getStatus() %></span></td>
            </tr>
<%
        }
%>
            </tbody>
        </table>
<%
    }
%>
    </div>
    <div class="card">
        <h2>상품 등록</h2>
        <form class="form" method="post" action="${pageContext.request.contextPath}/admin/funds">
            <label>상품명 <input name="name" placeholder="상품명" required></label>
            <label>설명 <textarea name="description" placeholder="설명"></textarea></label>
            <label>위험등급 (1~5) <input name="riskGrade" type="number" min="1" max="5" placeholder="위험등급" required></label>
            <label>예상수익률 (%) <input name="expectedReturn" type="number" step="0.01" placeholder="예상수익률" required></label>
            <label>상태
                <select name="status">
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="INACTIVE">INACTIVE</option>
                </select>
            </label>
            <button type="submit">등록</button>
        </form>
    </div>
    <div class="card">
        <p><a href="${pageContext.request.contextPath}/admin/applications/status">신청 관리로</a></p>
    </div>
</div>
</body>
</html>

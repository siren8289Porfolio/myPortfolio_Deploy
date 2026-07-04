<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.briefly.application.dto.ApplicationDto" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Briefly - 신청 관리</title>
    <%@ include file="/WEB-INF/views/common/nav.jsp" %>
</head>
<body>
<div class="page">
    <div class="card">
        <h1>관리자 - 신청 상태 관리</h1>
<%
    List<ApplicationDto> applications = (List<ApplicationDto>) request.getAttribute("applications");
    if (applications == null || applications.isEmpty()) {
%>
        <div class="empty-state">신청 내역이 없습니다.</div>
<%
    } else {
%>
        <table>
            <thead>
            <tr><th>ID</th><th>사용자</th><th>상품</th><th>금액</th><th>상태</th><th>변경</th></tr>
            </thead>
            <tbody>
<%
        for (ApplicationDto app : applications) {
%>
            <tr>
                <td>#<%= app.getId() %></td>
                <td><%= app.getUserId() %></td>
                <td><%= app.getFundId() %></td>
                <td><%= app.getAmount() %>원</td>
                <td><span class="badge badge-status-<%= app.getStatus() %>"><%= app.getStatus() %></span></td>
                <td>
                    <form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/applications/status">
                        <input type="hidden" name="applicationId" value="<%= app.getId() %>">
                        <select name="status">
                            <option value="PENDING">PENDING</option>
                            <option value="APPROVED">APPROVED</option>
                            <option value="REJECTED">REJECTED</option>
                            <option value="CANCELED">CANCELED</option>
                        </select>
                        <button type="submit">변경</button>
                    </form>
                </td>
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
        <p><a href="${pageContext.request.contextPath}/admin/funds">상품 관리로</a></p>
    </div>
</div>
</body>
</html>

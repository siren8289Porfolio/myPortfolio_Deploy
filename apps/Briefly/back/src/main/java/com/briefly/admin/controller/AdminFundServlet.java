package com.briefly.admin.controller;

import com.briefly.fund.dto.FundDto;
import com.briefly.fund.entity.Fund;
import com.briefly.fund.service.FundService;
import com.briefly.common.util.WebUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 관리자 상품(Fund) 등록/수정만 담당한다.
 * 예전에는 AdminServlet 하나가 상품/브리프/알림/신청상태를 모두 처리했는데(God Servlet),
 * 리소스별로 나눠 각 서블릿이 하나의 책임만 갖도록 분리했다.
 */
@WebServlet("/admin/funds")
public class AdminFundServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AdminFundServlet.class.getName());

    private final FundService fundService;

    public AdminFundServlet() {
        this(new FundService());
    }

    public AdminFundServlet(FundService fundService) {
        this.fundService = fundService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            List<FundDto> funds = fundService.getAllFunds().stream().map(FundDto::from).toList();
            req.setAttribute("funds", funds);
            WebUtil.forward(req, resp, "admin/funds.jsp");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "관리자 상품 목록 조회 실패", e);
            WebUtil.setError(req, "관리자 화면 조회 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "error/error.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Fund fund = new Fund();
            String idParam = req.getParameter("id");
            fund.setName(req.getParameter("name"));
            fund.setDescription(req.getParameter("description"));
            fund.setRiskGrade(Integer.parseInt(req.getParameter("riskGrade")));
            fund.setExpectedReturn(new BigDecimal(req.getParameter("expectedReturn")));
            fund.setStatus(Fund.Status.valueOf(req.getParameter("status").toUpperCase()));
            if (idParam != null && !idParam.isBlank()) {
                fund.setId(Long.parseLong(idParam));
                fundService.updateFund(fund);
            } else {
                fundService.createFund(fund);
            }
            WebUtil.redirect(req, resp, "/admin/funds");
        } catch (IllegalArgumentException e) {
            WebUtil.setError(req, e.getMessage());
            WebUtil.forward(req, resp, "error/error.jsp");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "관리자 상품 등록/수정 실패", e);
            WebUtil.setError(req, "관리자 요청 처리 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "error/error.jsp");
        }
    }
}

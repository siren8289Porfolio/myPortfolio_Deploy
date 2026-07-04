package com.briefly.admin.controller;

import com.briefly.report.entity.FundReport;
import com.briefly.report.service.ReportService;
import com.briefly.common.util.WebUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 관리자의 운용 브리프(Report) 등록만 담당한다. */
@WebServlet("/admin/reports")
public class AdminReportServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AdminReportServlet.class.getName());

    private final ReportService reportService;

    public AdminReportServlet() {
        this(new ReportService());
    }

    public AdminReportServlet(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            FundReport report = new FundReport();
            report.setFundId(Long.parseLong(req.getParameter("fundId")));
            report.setTitle(req.getParameter("title"));
            report.setContent(req.getParameter("content"));
            report.setReportDate(LocalDate.parse(req.getParameter("reportDate")));
            reportService.createReport(report);
            WebUtil.redirect(req, resp, "/admin/funds");
        } catch (IllegalArgumentException e) {
            WebUtil.setError(req, e.getMessage());
            WebUtil.forward(req, resp, "error/error.jsp");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "관리자 브리프 등록 실패", e);
            WebUtil.setError(req, "관리자 요청 처리 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "error/error.jsp");
        }
    }
}

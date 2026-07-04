package com.briefly.admin.controller;

import com.briefly.application.entity.FundApplication;
import com.briefly.application.service.ApplicationService;
import com.briefly.common.util.WebUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 관리자의 모의가입 신청 상태(FundApplication.Status) 조회/변경만 담당한다. */
@WebServlet("/admin/applications/status")
public class AdminApplicationServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AdminApplicationServlet.class.getName());

    private final ApplicationService applicationService;

    public AdminApplicationServlet() {
        this(new ApplicationService());
    }

    public AdminApplicationServlet(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            req.setAttribute("applications", applicationService.getAll());
            WebUtil.forward(req, resp, "admin/applications.jsp");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "관리자 신청 목록 조회 실패", e);
            WebUtil.setError(req, "관리자 화면 조회 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "error/error.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Long applicationId = Long.parseLong(req.getParameter("applicationId"));
            FundApplication.Status status = FundApplication.Status.valueOf(
                    req.getParameter("status").toUpperCase()
            );
            applicationService.updateStatus(applicationId, status);
            WebUtil.redirect(req, resp, "/admin/applications/status");
        } catch (IllegalArgumentException e) {
            WebUtil.setError(req, e.getMessage());
            WebUtil.forward(req, resp, "error/error.jsp");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "관리자 신청 상태 변경 실패", e);
            WebUtil.setError(req, "관리자 요청 처리 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "error/error.jsp");
        }
    }
}

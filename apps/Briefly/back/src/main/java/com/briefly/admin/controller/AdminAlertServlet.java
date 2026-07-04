package com.briefly.admin.controller;

import com.briefly.alert.entity.RiskAlert;
import com.briefly.alert.service.AlertService;
import com.briefly.common.util.WebUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 관리자의 위험 알림(RiskAlert) 등록만 담당한다. */
@WebServlet("/admin/alerts")
public class AdminAlertServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AdminAlertServlet.class.getName());

    private final AlertService alertService;

    public AdminAlertServlet() {
        this(new AlertService());
    }

    public AdminAlertServlet(AlertService alertService) {
        this.alertService = alertService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            RiskAlert alert = new RiskAlert();
            alert.setFundId(Long.parseLong(req.getParameter("fundId")));
            alert.setTitle(req.getParameter("title"));
            alert.setMessage(req.getParameter("message"));
            alert.setPreviousGrade(Integer.parseInt(req.getParameter("previousGrade")));
            alert.setNewGrade(Integer.parseInt(req.getParameter("newGrade")));
            alertService.createAlert(alert);
            WebUtil.redirect(req, resp, "/admin/funds");
        } catch (IllegalArgumentException e) {
            WebUtil.setError(req, e.getMessage());
            WebUtil.forward(req, resp, "error/error.jsp");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "관리자 알림 등록 실패", e);
            WebUtil.setError(req, "관리자 요청 처리 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "error/error.jsp");
        }
    }
}

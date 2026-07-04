package com.briefly.application.controller;

import com.briefly.auth.entity.User;
import com.briefly.application.service.ApplicationService;
import com.briefly.common.util.SessionUtil;
import com.briefly.common.util.WebUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet("/applications")
public class ApplicationServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ApplicationServlet.class.getName());

    private final ApplicationService applicationService;

    public ApplicationServlet() {
        this(new ApplicationService());
    }

    public ApplicationServlet(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            User user = SessionUtil.getLoginUser(req);
            req.setAttribute("applications", applicationService.getByUser(user.getId()));
            WebUtil.forward(req, resp, "application/list.jsp");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "신청 내역 조회 실패", e);
            WebUtil.setError(req, "신청 내역 조회 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "error/error.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            User user = SessionUtil.getLoginUser(req);
            Long fundId = Long.parseLong(req.getParameter("fundId"));
            BigDecimal amount = new BigDecimal(req.getParameter("amount"));
            applicationService.apply(user.getId(), fundId, amount);
            WebUtil.redirect(req, resp, "/applications");
        } catch (IllegalArgumentException e) {
            WebUtil.setError(req, e.getMessage());
            WebUtil.redirect(req, resp, "/funds/detail?id=" + req.getParameter("fundId"));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "모의가입 신청 실패", e);
            WebUtil.setError(req, "모의가입 신청 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "error/error.jsp");
        }
    }
}

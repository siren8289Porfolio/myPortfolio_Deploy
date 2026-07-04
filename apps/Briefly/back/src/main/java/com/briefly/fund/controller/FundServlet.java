package com.briefly.fund.controller;

import com.briefly.fund.dto.FundDto;
import com.briefly.auth.entity.User;
import com.briefly.fund.service.FundService;
import com.briefly.watchlist.service.WatchlistService;
import com.briefly.common.util.SessionUtil;
import com.briefly.common.util.WebUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = {"/funds", "/funds/detail"})
public class FundServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(FundServlet.class.getName());

    private final FundService fundService;
    private final WatchlistService watchlistService;

    public FundServlet() {
        this(new FundService(), new WatchlistService());
    }

    // 테스트에서 목(mock) 서비스를 주입할 수 있도록 컨테이너 기본 생성자와 분리한다.
    public FundServlet(FundService fundService, WatchlistService watchlistService) {
        this.fundService = fundService;
        this.watchlistService = watchlistService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            if ("/funds/detail".equals(req.getServletPath())) {
                Long fundId = Long.parseLong(req.getParameter("id"));
                FundDto fund = FundDto.from(fundService.getFund(fundId));
                req.setAttribute("fund", fund);
                User user = SessionUtil.getLoginUser(req);
                if (user != null) {
                    req.setAttribute("watched", watchlistService.isWatched(user.getId(), fundId));
                }
                WebUtil.forward(req, resp, "fund/detail.jsp");
                return;
            }

            List<FundDto> funds = fundService.getActiveFunds().stream().map(FundDto::from).toList();
            req.setAttribute("funds", funds);
            WebUtil.forward(req, resp, "fund/list.jsp");
        } catch (IllegalArgumentException e) {
            WebUtil.setError(req, e.getMessage());
            WebUtil.forward(req, resp, "error/not-found.jsp");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "상품 조회 실패: " + req.getRequestURI(), e);
            WebUtil.setError(req, "상품 조회 중 오류가 발생했습니다.");
            WebUtil.forward(req, resp, "error/error.jsp");
        }
    }
}

package com.briefly.fund.service;

import com.briefly.fund.dao.FundDao;
import com.briefly.fund.entity.Fund;

import java.sql.SQLException;
import java.util.List;

public class FundService {
    private final FundDao fundDao;

    public FundService() {
        this(new FundDao());
    }

    // 테스트에서 FundDao를 목(mock)으로 교체해 DB 없이 검증할 수 있도록 생성자로 주입받는다.
    public FundService(FundDao fundDao) {
        this.fundDao = fundDao;
    }

    public List<Fund> getActiveFunds() throws SQLException {
        return fundDao.findActiveAll();
    }

    public List<Fund> getAllFunds() throws SQLException {
        return fundDao.findAll();
    }

    public Fund getFund(Long id) throws SQLException {
        return fundDao.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    }

    public Long createFund(Fund fund) throws SQLException {
        validateFund(fund);
        return fundDao.insert(fund);
    }

    public void updateFund(Fund fund) throws SQLException {
        validateFund(fund);
        fundDao.update(fund);
    }

    private void validateFund(Fund fund) {
        if (fund.getRiskGrade() < 1 || fund.getRiskGrade() > 5) {
            throw new IllegalArgumentException("위험등급은 1~5 사이여야 합니다.");
        }
    }
}

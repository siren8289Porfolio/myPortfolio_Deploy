package com.allochub.domain.investment;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InvestmentRepository extends JpaRepository<Investment, String> {

    @Query("SELECT COALESCE(SUM(i.investmentAmount), 0) FROM Investment i")
    int sumInvestmentAmount();

    // list() 화면은 투자 건마다 investorInvestments와 그 investor까지 순회하므로,
    // 지연 로딩 그대로 두면 건당 추가 쿼리가 나가는 N+1이 된다. fetch join으로 한 번에 가져온다.
    @Query("SELECT DISTINCT i FROM Investment i "
            + "LEFT JOIN FETCH i.investorInvestments ii "
            + "LEFT JOIN FETCH ii.investor")
    List<Investment> findAllWithInvestorInvestments();
}

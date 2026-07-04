package com.allochub.domain.investment;

import static org.assertj.core.api.Assertions.assertThat;

import com.allochub.domain.investor.InvestorRequest;
import com.allochub.domain.investor.InvestorService;
import com.allochub.global.security.AuthUser;
import com.allochub.global.security.UserRole;
import com.allochub.integration.DatabaseCleaner;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * list()가 fetch join 없이 지연 로딩(investorInvestments -> investor)에 의존하면
 * 투자 건수만큼 추가 쿼리가 나가는 N+1이 재발할 수 있다. 건수를 늘려도 쿼리 수가
 * 늘어나지 않는지 고정해 회귀를 방지한다.
 */
@SpringBootTest
@ActiveProfiles("test")
class InvestmentServiceQueryCountTest {

    private static final AuthUser OPERATOR = new AuthUser("tester", UserRole.operator);

    @Autowired
    private InvestorService investorService;

    @Autowired
    private InvestmentService investmentService;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void resetDb() {
        databaseCleaner.clean();
    }

    @Test
    void listDoesNotIssueOneQueryPerInvestment() {
        investorService.create(OPERATOR, new InvestorRequest("출자자 A", null, 1_000_000, null, null, 50.0));
        investorService.create(OPERATOR, new InvestorRequest("출자자 B", null, 1_000_000, null, null, 50.0));

        for (int i = 0; i < 5; i++) {
            investmentService.create(OPERATOR, new InvestmentRequest(null, "회사" + i, null, 10_000));
        }

        Statistics statistics =
                entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();

        investmentService.list();

        // fetch join 한 번이면 되므로, 투자 건수(5개)와 무관하게 쿼리는 소수여야 한다.
        // (fetch join 없이 지연 로딩만 썼다면 5건 * 2개 연관 조회 = 최소 11개 이상 나갔을 것)
        assertThat(statistics.getPrepareStatementCount()).isLessThanOrEqualTo(2);
    }
}

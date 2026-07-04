package com.allochub.domain.distribution;

import static org.assertj.core.api.Assertions.assertThat;

import com.allochub.domain.investment.InvestmentRequest;
import com.allochub.domain.investment.InvestmentService;
import com.allochub.domain.investor.InvestorRequest;
import com.allochub.domain.investor.InvestorService;
import com.allochub.global.security.AuthUser;
import com.allochub.global.security.UserRole;
import com.allochub.integration.DatabaseCleaner;
import jakarta.persistence.EntityManagerFactory;
import java.util.Map;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * list()가 fetch join 없이 지연 로딩(investment)에 의존하면 분배 건수만큼 추가 쿼리가
 * 나가는 N+1이 재발할 수 있다. 건수를 늘려도 쿼리 수가 늘어나지 않는지 고정해 회귀를 방지한다.
 */
@SpringBootTest
@ActiveProfiles("test")
class DistributionServiceQueryCountTest {

    private static final AuthUser OPERATOR = new AuthUser("tester", UserRole.operator);

    @Autowired
    private InvestorService investorService;

    @Autowired
    private InvestmentService investmentService;

    @Autowired
    private DistributionService distributionService;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void resetDb() {
        databaseCleaner.clean();
    }

    @Test
    void listDoesNotIssueOneQueryPerDistribution() {
        investorService.create(OPERATOR, new InvestorRequest("출자자 A", null, 1_000_000, null, null, 100.0));

        for (int i = 0; i < 5; i++) {
            Map<String, Object> investment =
                    investmentService.create(OPERATOR, new InvestmentRequest(null, "회사" + i, null, 100_000));
            String investmentId = (String) investment.get("investmentId");
            distributionService.create(
                    OPERATOR, new DistributionRequest(null, investmentId, null, "배당금", null, 10_000));
        }

        Statistics statistics =
                entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();

        distributionService.list();

        // fetch join 한 번이면 되므로, 분배 건수(5개)와 무관하게 쿼리는 소수여야 한다.
        assertThat(statistics.getPrepareStatementCount()).isLessThanOrEqualTo(2);
    }
}

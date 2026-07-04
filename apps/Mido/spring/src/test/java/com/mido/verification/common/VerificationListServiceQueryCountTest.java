package com.mido.verification.common;

import static org.assertj.core.api.Assertions.assertThat;

import com.mido.verification.common.entity.VerificationData;
import com.mido.verification.common.entity.VerificationStatus;
import com.mido.verification.common.repository.VerificationDataRepository;
import com.mido.verification.common.service.VerificationListService;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
class VerificationListServiceQueryCountTest {

    @Autowired
    private VerificationListService verificationListService;

    @Autowired
    private VerificationDataRepository verificationDataRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void resetDb() {
        verificationDataRepository.deleteAll();
    }

    @Test
    void listDoesNotIssueOneQueryPerVerification() {
        for (int i = 0; i < 5; i++) {
            VerificationData data = new VerificationData();
            data.setId(UUID.randomUUID());
            data.setInputType(i % 2 == 0 ? "PASTE" : "FILE");
            data.setStatus(VerificationStatus.DRAFT);
            data.setCode("x".repeat(10_000));
            data.setCreatedAt(Instant.now().minusSeconds(i));
            data.setUpdatedAt(Instant.now());
            verificationDataRepository.save(data);
        }

        Statistics statistics =
                entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();

        verificationListService.list(PageRequest.of(0, 20));

        assertThat(statistics.getPrepareStatementCount()).isLessThanOrEqualTo(2);
    }
}

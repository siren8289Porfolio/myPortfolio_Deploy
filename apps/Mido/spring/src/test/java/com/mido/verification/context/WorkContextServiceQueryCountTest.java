package com.mido.verification.context;

import static org.assertj.core.api.Assertions.assertThat;

import com.mido.verification.common.entity.VerificationData;
import com.mido.verification.common.entity.VerificationStatus;
import com.mido.verification.common.repository.VerificationDataRepository;
import com.mido.verification.context.entity.WorkContext;
import com.mido.verification.context.repository.WorkContextRepository;
import com.mido.verification.context.service.WorkContextService;
import com.mido.verification.upload.entity.UploadedFile;
import com.mido.verification.upload.repository.UploadedFileRepository;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
class WorkContextServiceQueryCountTest {

    @Autowired
    private WorkContextService workContextService;

    @Autowired
    private VerificationDataRepository verificationDataRepository;

    @Autowired
    private WorkContextRepository workContextRepository;

    @Autowired
    private UploadedFileRepository uploadedFileRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void resetDb() {
        uploadedFileRepository.deleteAll();
        workContextRepository.deleteAll();
        verificationDataRepository.deleteAll();
    }

    @Test
    void getContextDoesNotLoadVerificationCodeLob() {
        UUID verificationId = seedFileContext();

        Statistics statistics =
                entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();

        workContextService.get(verificationId);

        // work_context 1번 + uploaded_file 1번. verification_data.code LOB 조회 없음.
        assertThat(statistics.getPrepareStatementCount()).isLessThanOrEqualTo(2);
    }

    private UUID seedFileContext() {
        Instant now = Instant.now();
        UUID verificationId = UUID.randomUUID();

        VerificationData data = new VerificationData();
        data.setId(verificationId);
        data.setInputType("FILE");
        data.setStatus(VerificationStatus.READY);
        data.setCode("lob-content-should-not-be-read");
        data.setCreatedAt(now);
        data.setUpdatedAt(now);
        verificationDataRepository.save(data);

        WorkContext context = new WorkContext();
        context.setId(UUID.randomUUID());
        context.setVerificationData(data);
        context.setDisplayInputType("FILE");
        context.setCreatedAt(now);
        workContextRepository.save(context);

        UploadedFile file = new UploadedFile();
        file.setId(UUID.randomUUID());
        file.setVerificationData(data);
        file.setFileName("Main.java");
        file.setFileContent("class Main {}");
        file.setUploadedAt(now);
        uploadedFileRepository.save(file);

        return verificationId;
    }
}

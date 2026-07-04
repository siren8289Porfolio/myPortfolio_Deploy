package com.example.demo.assessment;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.ai.entity.AIRiskResult;
import com.example.demo.ai.repository.AIRiskResultRepository;
import com.example.demo.applicant.entity.Applicant;
import com.example.demo.applicant.entity.HealthSnapshot;
import com.example.demo.applicant.repository.ApplicantRepository;
import com.example.demo.applicant.repository.HealthSnapshotRepository;
import com.example.demo.assessment.entity.Assessment;
import com.example.demo.assessment.entity.AssessmentStatus;
import com.example.demo.assessment.repository.AssessmentRepository;
import com.example.demo.assessment.service.AssessmentService;
import com.example.demo.job.entity.Job;
import com.example.demo.job.repository.JobRepository;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * listAllRecords()가 DTO projection 없이 entity fetch join/lazy loading에 의존하면
 * 페이지 건수만큼 추가 쿼리가 나가는 N+1이 재발할 수 있다.
 */
@SpringBootTest
@ActiveProfiles("test")
class AssessmentServiceQueryCountTest {

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private HealthSnapshotRepository healthSnapshotRepository;

    @Autowired
    private AIRiskResultRepository aiRiskResultRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void resetDb() {
        assessmentRepository.deleteAll();
        aiRiskResultRepository.deleteAll();
        healthSnapshotRepository.deleteAll();
        jobRepository.deleteAll();
        applicantRepository.deleteAll();
    }

    @Test
    void listAllRecordsDoesNotIssueOneQueryPerAssessment() {
        for (int i = 0; i < 5; i++) {
            saveAssessment(i);
        }

        Statistics statistics =
                entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        statistics.clear();

        assessmentService.listAllRecords(PageRequest.of(0, 20));

        // projection join 1번 + count 1번이면 충분. N+1이면 5건 * 연관 4개 = 20+ 쿼리.
        assertThat(statistics.getPrepareStatementCount()).isLessThanOrEqualTo(2);
    }

    private void saveAssessment(int index) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(index);

        Applicant applicant = applicantRepository.save(newApplicant(index, now));
        Job job = jobRepository.save(newJob(index, now));

        HealthSnapshot healthSnapshot = new HealthSnapshot();
        healthSnapshot.setApplicant(applicant);
        healthSnapshot.setPhysicalLevel(3);
        healthSnapshot.setChronicDiseaseFlag(false);
        healthSnapshot.setWorkHourLimit(20);
        healthSnapshot.setCreatedAt(now);
        healthSnapshot = healthSnapshotRepository.save(healthSnapshot);

        Assessment assessment = new Assessment();
        assessment.setApplicant(applicant);
        assessment.setJob(job);
        assessment.setHealthSnapshot(healthSnapshot);
        assessment.setStatus(AssessmentStatus.FINALIZED);
        assessment.setAssessedAt(now);
        assessment = assessmentRepository.save(assessment);

        AIRiskResult riskResult = new AIRiskResult();
        riskResult.setAssessment(assessment);
        riskResult.setTotalRiskPercent(40 + index);
        riskResult.setRiskGrade(index % 2 == 0 ? "HIGH" : "MID");
        riskResult.setGeneratedAt(now);
        riskResult.setModelVersion("test-v1");
        riskResult = aiRiskResultRepository.save(riskResult);

        assessment.setAiRiskResult(riskResult);
        assessmentRepository.save(assessment);
    }

    private Applicant newApplicant(int index, OffsetDateTime now) {
        Applicant applicant = new Applicant();
        applicant.setDisplayName("신청자" + index);
        applicant.setAge(60 + index);
        applicant.setCreatedAt(now);
        return applicant;
    }

    private Job newJob(int index, OffsetDateTime now) {
        Job job = new Job();
        job.setJobTitle("직무" + index);
        job.setWorkplace("현장" + index);
        job.setWorkHours("주 20시간");
        job.setDescription("설명");
        job.setCreatedAt(now);
        return job;
    }
}

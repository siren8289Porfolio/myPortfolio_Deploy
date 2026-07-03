package com.dasigolmok.domain.admin.service;

import com.dasigolmok.domain.admin.dto.ReportResponse;
import com.dasigolmok.domain.admin.entity.AuditAction;
import com.dasigolmok.domain.admin.entity.AuditLog;
import com.dasigolmok.domain.admin.entity.ReviewLog;
import com.dasigolmok.domain.admin.repository.AuditLogRepository;
import com.dasigolmok.domain.admin.repository.ReviewLogRepository;
import com.dasigolmok.domain.reaction.entity.Report;
import com.dasigolmok.domain.reaction.entity.ReportStatus;
import com.dasigolmok.domain.reaction.repository.ReportRepository;
import com.dasigolmok.domain.story.dto.StoryCardResponse;
import com.dasigolmok.domain.story.entity.Story;
import com.dasigolmok.domain.story.entity.StoryStatus;
import com.dasigolmok.domain.story.repository.StoryRepository;
import com.dasigolmok.domain.story.service.StoryService;
import com.dasigolmok.domain.user.entity.User;
import com.dasigolmok.domain.user.repository.UserRepository;
import com.dasigolmok.global.exception.BusinessException;
import com.dasigolmok.global.response.ErrorCode;
import com.dasigolmok.global.security.SecurityUtils;
import com.dasigolmok.infra.analytics.AnalyticsViewRefresher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final StoryRepository storyRepository;
    private final StoryService storyService;
    private final ReportRepository reportRepository;
    private final ReviewLogRepository reviewLogRepository;
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final AnalyticsViewRefresher analyticsViewRefresher;

    private static final int ADMIN_LIST_LIMIT = 100;

    @Transactional(readOnly = true)
    public List<StoryCardResponse> getStories(String status) {
        String resolved = status != null && !status.isBlank() ? status : "PENDING";
        StoryStatus storyStatus = StoryStatus.valueOf(resolved);
        return storyRepository.findForAdmin(
                        storyStatus,
                        PageRequest.of(0, ADMIN_LIST_LIMIT, Sort.by(Sort.Direction.DESC, "createdAt")))
                .stream()
                .map(storyService::toCard)
                .collect(Collectors.toList());
    }

    @Transactional
    public StoryCardResponse updateStoryStatus(String storyId, String status, String note) {
        Story story = storyRepository.findByIdAndDeletedAtIsNull(storyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "스토리를 찾을 수 없습니다."));
        StoryStatus before = story.getStatus();
        StoryStatus after = StoryStatus.valueOf(status);
        story.setStatus(after);
        if (after == StoryStatus.DELETED) {
            story.setDeletedAt(LocalDateTime.now());
        }

        String adminId = SecurityUtils.getCurrentUserId();
        User admin = userRepository.findById(adminId).orElseThrow();
        reviewLogRepository.save(ReviewLog.builder()
                .story(story)
                .reviewer(admin)
                .beforeStatus(before)
                .afterStatus(after)
                .note(note)
                .build());
        AuditAction action = switch (after) {
            case APPROVED -> AuditAction.APPROVE;
            case REJECTED -> AuditAction.REJECT;
            case HIDDEN -> AuditAction.HIDE;
            case DELETED -> AuditAction.DELETE;
            default -> AuditAction.UPDATE;
        };
        auditLogRepository.save(AuditLog.builder()
                .user(admin)
                .action(action)
                .entityType("Story")
                .entityId(storyId)
                .detail(note)
                .build());
        analyticsViewRefresher.refreshAll();
        return storyService.toCard(story);
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getReports(String status) {
        List<Report> reports = status != null && !status.isBlank()
                ? reportRepository.findByStatusOrderByCreatedAtDesc(ReportStatus.valueOf(status))
                : reportRepository.findAllByOrderByCreatedAtDesc();
        return reports.stream().map(this::toReport).collect(Collectors.toList());
    }

    @Transactional
    public ReportResponse updateReportStatus(String reportId, String status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "신고를 찾을 수 없습니다."));
        report.setStatus(ReportStatus.valueOf(status));
        return toReport(report);
    }

    private ReportResponse toReport(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .storyId(report.getStory().getId())
                .storyTitle(report.getStory().getTitle())
                .reporterNickname(report.getReporter().getNickname())
                .reason(report.getReason())
                .status(report.getStatus().name())
                .createdAt(report.getCreatedAt().toString())
                .build();
    }
}

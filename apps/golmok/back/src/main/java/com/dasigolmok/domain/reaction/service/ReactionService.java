package com.dasigolmok.domain.reaction.service;

import com.dasigolmok.domain.admin.entity.AuditAction;
import com.dasigolmok.domain.admin.entity.AuditLog;
import com.dasigolmok.domain.admin.repository.AuditLogRepository;
import com.dasigolmok.domain.reaction.entity.Reaction;
import com.dasigolmok.domain.reaction.entity.ReactionType;
import com.dasigolmok.domain.reaction.entity.Report;
import com.dasigolmok.domain.reaction.repository.ReactionRepository;
import com.dasigolmok.domain.reaction.repository.ReportRepository;
import com.dasigolmok.domain.story.entity.Story;
import com.dasigolmok.domain.story.repository.StoryRepository;
import com.dasigolmok.domain.user.entity.User;
import com.dasigolmok.domain.user.repository.UserRepository;
import com.dasigolmok.global.exception.BusinessException;
import com.dasigolmok.global.response.ErrorCode;
import com.dasigolmok.global.security.SecurityUtils;
import com.dasigolmok.infra.analytics.AnalyticsViewRefresher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final StoryRepository storyRepository;
    private final ReactionRepository reactionRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final AnalyticsViewRefresher analyticsViewRefresher;

    @Transactional
    public Map<String, Object> like(String storyId) {
        String userId = requireUserId();
        Story story = findStory(storyId);
        if (reactionRepository.existsByUserIdAndStoryIdAndType(userId, storyId, ReactionType.LIKE)) {
            throw new BusinessException(ErrorCode.CONFLICT, "이미 좋아요를 눌렀습니다.");
        }
        User user = userRepository.findById(userId).orElseThrow();
        reactionRepository.save(Reaction.builder().user(user).story(story).type(ReactionType.LIKE).build());
        story.setLikeCount(story.getLikeCount() + 1);
        analyticsViewRefresher.refreshAll();
        return Map.of("likeCount", story.getLikeCount(), "liked", true);
    }

    @Transactional
    public Map<String, Object> unlike(String storyId) {
        String userId = requireUserId();
        Story story = findStory(storyId);
        Reaction reaction = reactionRepository.findByUserIdAndStoryIdAndType(userId, storyId, ReactionType.LIKE)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "좋아요를 찾을 수 없습니다."));
        reactionRepository.delete(reaction);
        story.setLikeCount(Math.max(0, story.getLikeCount() - 1));
        analyticsViewRefresher.refreshAll();
        return Map.of("likeCount", story.getLikeCount(), "liked", false);
    }

    @Transactional
    public void report(String storyId, String reason) {
        String userId = requireUserId();
        Story story = findStory(storyId);
        User user = userRepository.findById(userId).orElseThrow();
        reportRepository.save(Report.builder().story(story).reporter(user).reason(reason).build());
        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .action(AuditAction.CREATE)
                .entityType("Report")
                .entityId(storyId)
                .detail(reason)
                .build());
    }

    private String requireUserId() {
        String userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return userId;
    }

    private Story findStory(String storyId) {
        return storyRepository.findByIdAndDeletedAtIsNull(storyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "스토리를 찾을 수 없습니다."));
    }
}

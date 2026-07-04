package com.dasigolmok.infra.analytics;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 스토리 등록/좋아요/관리자 승인마다 두 개의 Materialized View를 매번 동기로
 * REFRESH CONCURRENTLY 하면, 데이터가 쌓일수록(예: 6만 건 기준 약 1.5~2초)
 * 쓰기 요청 하나의 응답 시간이 뷰 재계산 시간만큼 그대로 늘어난다.
 *
 * refreshAll()은 더 이상 즉시 리프레시를 실행하지 않고 이벤트만 발행한다.
 * 실제 REFRESH는 호출한 쪽의 쓰기 트랜잭션이 커밋된 "이후"에, 별도 스레드에서
 * 비동기로 수행되므로 클라이언트는 DB 반영이 끝나는 즉시 응답을 받는다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsViewRefresher {

    private final EntityManager entityManager;
    private final ApplicationEventPublisher eventPublisher;

    public void refreshAll() {
        eventPublisher.publishEvent(new AnalyticsRefreshRequestedEvent());
    }

    @Async("analyticsRefreshExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRefreshRequested(AnalyticsRefreshRequestedEvent event) {
        try {
            entityManager.createNativeQuery(
                    "REFRESH MATERIALIZED VIEW CONCURRENTLY mv_region_story_stats"
            ).executeUpdate();
            entityManager.createNativeQuery(
                    "REFRESH MATERIALIZED VIEW CONCURRENTLY mv_story_curation_summary"
            ).executeUpdate();
        } catch (Exception e) {
            log.error("Materialized View 비동기 리프레시 실패", e);
        }
    }
}

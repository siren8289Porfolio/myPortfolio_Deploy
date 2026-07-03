package com.dasigolmok.infra.analytics;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AnalyticsViewRefresher {

    private final EntityManager entityManager;

    @Transactional
    public void refreshAll() {
        entityManager.createNativeQuery(
                "REFRESH MATERIALIZED VIEW CONCURRENTLY mv_region_story_stats"
        ).executeUpdate();
        entityManager.createNativeQuery(
                "REFRESH MATERIALIZED VIEW CONCURRENTLY mv_story_curation_summary"
        ).executeUpdate();
    }
}

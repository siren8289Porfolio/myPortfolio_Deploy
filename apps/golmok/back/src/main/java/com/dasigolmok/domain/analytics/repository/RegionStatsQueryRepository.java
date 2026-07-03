package com.dasigolmok.domain.analytics.repository;

import com.dasigolmok.domain.analytics.dto.RegionStatsResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RegionStatsQueryRepository {

    private final EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<RegionStatsResponse> findAllRegionStats() {
        Query query = entityManager.createNativeQuery("""
                SELECT region_id, region_name, region_slug,
                       approved_count, pending_count, total_likes, last_story_updated_at
                FROM mv_region_story_stats
                ORDER BY approved_count DESC, region_name ASC
                """);
        List<Object[]> rows = query.getResultList();
        return rows.stream().map(this::toResponse).toList();
    }

    private RegionStatsResponse toResponse(Object[] row) {
        Timestamp lastUpdated = row[6] != null ? (Timestamp) row[6] : null;
        return RegionStatsResponse.builder()
                .regionId((String) row[0])
                .regionName((String) row[1])
                .regionSlug((String) row[2])
                .approvedCount(((Number) row[3]).longValue())
                .pendingCount(((Number) row[4]).longValue())
                .totalLikes(((Number) row[5]).longValue())
                .lastStoryUpdatedAt(lastUpdated != null ? lastUpdated.toInstant().toString() : null)
                .build();
    }
}

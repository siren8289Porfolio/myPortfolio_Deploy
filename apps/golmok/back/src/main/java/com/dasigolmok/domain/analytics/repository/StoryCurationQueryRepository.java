package com.dasigolmok.domain.analytics.repository;

import com.dasigolmok.domain.story.dto.StoryCardResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class StoryCurationQueryRepository {

    private final EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<StoryCardResponse> findCuration(String sort, String tag, int limit) {
        StringBuilder sql = new StringBuilder("""
                SELECT id, title, content, like_count, image_url, category, period_tag,
                       author_nickname, author_email, place_name, region_name
                FROM mv_story_curation_summary
                WHERE 1 = 1
                """);
        Map<String, Object> params = new HashMap<>();

        if (tag != null && !tag.isBlank()) {
            sql.append(" AND (category = :tag OR period_tag = :tag)");
            params.put("tag", tag);
        }

        if ("latest".equals(sort)) {
            sql.append(" ORDER BY created_at DESC");
        } else {
            sql.append(" ORDER BY like_count DESC, created_at DESC");
        }
        sql.append(" LIMIT :limit");
        params.put("limit", limit);

        Query query = entityManager.createNativeQuery(sql.toString());
        params.forEach(query::setParameter);

        List<Object[]> rows = query.getResultList();
        return rows.stream().map(this::toCard).toList();
    }

    private StoryCardResponse toCard(Object[] row) {
        int likeCount = row[3] != null ? ((Number) row[3]).intValue() : 0;
        String imageUrl = row[4] != null ? (String) row[4] : "/uploads/placeholder.jpg";
        return StoryCardResponse.builder()
                .id((String) row[0])
                .title((String) row[1])
                .description((String) row[2])
                .imageUrl(imageUrl)
                .category(row[5] != null ? (String) row[5] : "거리")
                .periodTag((String) row[6])
                .createdBy(StoryCardResponse.CreatedBy.builder()
                        .nickname((String) row[7])
                        .email((String) row[8])
                        .build())
                .place(StoryCardResponse.PlaceSummary.builder()
                        .locationName((String) row[9])
                        .name((String) row[9])
                        .region(StoryCardResponse.RegionSummary.builder()
                                .name((String) row[10])
                                .build())
                        .build())
                ._count(StoryCardResponse.CountSummary.builder()
                        .reactions(likeCount)
                        .build())
                .build();
    }
}

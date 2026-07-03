package com.dasigolmok.domain.analytics.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegionStatsResponse {
    private String regionId;
    private String regionName;
    private String regionSlug;
    private long approvedCount;
    private long pendingCount;
    private long totalLikes;
    private String lastStoryUpdatedAt;
}

package com.dasigolmok.domain.story.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoryCardResponse {
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private String category;
    private String periodTag;
    private CreatedBy createdBy;
    private PlaceSummary place;
    private CountSummary _count;

    @Getter
    @Builder
    public static class CreatedBy {
        private String nickname;
        private String email;
    }

    @Getter
    @Builder
    public static class PlaceSummary {
        private String locationName;
        private String name;
        private RegionSummary region;
    }

    @Getter
    @Builder
    public static class RegionSummary {
        private String name;
    }

    @Getter
    @Builder
    public static class CountSummary {
        private int reactions;
    }
}

package com.dasigolmok.domain.story.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MapMarkerResponse {
    private String storyId;
    private String title;
    private Double latitude;
    private Double longitude;
    private String thumbnailUrl;
    private String category;
    private String locationName;
}

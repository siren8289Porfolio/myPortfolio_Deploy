package com.dasigolmok.domain.story.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageUploadResponse {
    private String imageUrl;
    private String thumbnailUrl;
}

package com.dasigolmok.domain.story.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStoryRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private String imageUrl;
    private String locationName;
    private Double latitude;
    private Double longitude;

    @NotBlank
    private String regionId;

    private String periodTag;
    private String category;
    private String sourceType;
    private Boolean copyrightAgreed;
}

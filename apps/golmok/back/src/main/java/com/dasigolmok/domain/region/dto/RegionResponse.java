package com.dasigolmok.domain.region.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegionResponse {
    private String id;
    private String name;
    private String slug;
    private String description;
}

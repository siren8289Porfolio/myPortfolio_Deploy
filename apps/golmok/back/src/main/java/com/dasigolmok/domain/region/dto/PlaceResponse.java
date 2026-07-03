package com.dasigolmok.domain.region.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceResponse {
    private String id;
    private String regionId;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
}

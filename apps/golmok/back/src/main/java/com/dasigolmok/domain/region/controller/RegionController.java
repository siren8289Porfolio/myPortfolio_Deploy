package com.dasigolmok.domain.region.controller;

import com.dasigolmok.domain.region.dto.PlaceResponse;
import com.dasigolmok.domain.region.dto.RegionResponse;
import com.dasigolmok.domain.region.entity.Place;
import com.dasigolmok.domain.region.repository.PlaceRepository;
import com.dasigolmok.domain.region.repository.RegionRepository;
import com.dasigolmok.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RegionController {

    private final RegionRepository regionRepository;
    private final PlaceRepository placeRepository;

    @GetMapping("/regions")
    public ApiResponse<List<RegionResponse>> regions() {
        List<RegionResponse> data = regionRepository.findAll().stream()
                .map(r -> RegionResponse.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .slug(r.getSlug())
                        .description(r.getDescription())
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.ok(data);
    }

    @GetMapping("/places")
    public ApiResponse<List<PlaceResponse>> places(@RequestParam(required = false) String regionId) {
        List<Place> places = regionId != null ? placeRepository.findByRegionId(regionId) : placeRepository.findAll();
        List<PlaceResponse> data = places.stream()
                .map(p -> PlaceResponse.builder()
                        .id(p.getId())
                        .regionId(p.getRegion().getId())
                        .name(p.getName())
                        .address(p.getAddress())
                        .latitude(p.getLatitude())
                        .longitude(p.getLongitude())
                        .build())
                .collect(Collectors.toList());
        return ApiResponse.ok(data);
    }
}

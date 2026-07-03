package com.dasigolmok.domain.analytics.controller;

import com.dasigolmok.domain.analytics.dto.RegionStatsResponse;
import com.dasigolmok.domain.analytics.service.AnalyticsService;
import com.dasigolmok.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/regions")
    public ApiResponse<List<RegionStatsResponse>> regionStats() {
        return ApiResponse.ok(analyticsService.getRegionStats());
    }
}

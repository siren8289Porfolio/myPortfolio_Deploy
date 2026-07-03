package com.dasigolmok.domain.analytics.service;

import com.dasigolmok.domain.analytics.dto.RegionStatsResponse;
import com.dasigolmok.domain.analytics.repository.RegionStatsQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final RegionStatsQueryRepository regionStatsQueryRepository;

    @Transactional(readOnly = true)
    public List<RegionStatsResponse> getRegionStats() {
        return regionStatsQueryRepository.findAllRegionStats();
    }
}

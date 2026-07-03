package com.dasigolmok.domain.region.repository;

import com.dasigolmok.domain.region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionRepository extends JpaRepository<Region, String> {
}

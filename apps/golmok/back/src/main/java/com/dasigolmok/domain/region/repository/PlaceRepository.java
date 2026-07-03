package com.dasigolmok.domain.region.repository;

import com.dasigolmok.domain.region.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, String> {
    List<Place> findByRegionId(String regionId);
}

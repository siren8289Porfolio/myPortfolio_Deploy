package com.dasigolmok.domain.admin.repository;

import com.dasigolmok.domain.admin.entity.ReviewLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLogRepository extends JpaRepository<ReviewLog, String> {
}

package com.mido.verification.common.repository;

import com.mido.verification.common.dto.VerificationSummaryResponse;
import com.mido.verification.common.entity.VerificationData;
import com.mido.verification.common.entity.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface VerificationDataRepository extends JpaRepository<VerificationData, UUID> {

    /**
     * 목록 조회용 Projection. code LOB를 읽지 않고 메타 컬럼만 반환한다.
     */
    @Query("""
            select new com.mido.verification.common.dto.VerificationSummaryResponse(
                v.id, v.inputType, v.status, v.createdAt)
            from VerificationData v
            """)
    Page<VerificationSummaryResponse> findSummaries(Pageable pageable);

    @Query("""
            select new com.mido.verification.common.dto.VerificationSummaryResponse(
                v.id, v.inputType, v.status, v.createdAt)
            from VerificationData v
            where v.status = :status
            """)
    Page<VerificationSummaryResponse> findSummariesByStatus(
            @Param("status") VerificationStatus status,
            Pageable pageable
    );
}

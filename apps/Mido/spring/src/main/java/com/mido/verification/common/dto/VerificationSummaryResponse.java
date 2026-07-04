package com.mido.verification.common.dto;

import com.mido.verification.common.entity.VerificationStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * 목록 API용 Projection DTO. code LOB는 포함하지 않는다.
 */
public class VerificationSummaryResponse {

    private UUID id;
    private String inputType;
    private VerificationStatus status;
    private Instant createdAt;

    public VerificationSummaryResponse(
            UUID id,
            String inputType,
            VerificationStatus status,
            Instant createdAt
    ) {
        this.id = id;
        this.inputType = inputType;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getInputType() {
        return inputType;
    }

    public VerificationStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

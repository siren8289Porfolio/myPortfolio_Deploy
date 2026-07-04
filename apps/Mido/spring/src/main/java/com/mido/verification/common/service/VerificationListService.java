package com.mido.verification.common.service;

import com.mido.verification.common.dto.VerificationSummaryResponse;
import com.mido.verification.common.entity.VerificationStatus;
import com.mido.verification.common.repository.VerificationDataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerificationListService {

    private final VerificationDataRepository verificationDataRepository;

    public VerificationListService(VerificationDataRepository verificationDataRepository) {
        this.verificationDataRepository = verificationDataRepository;
    }

    @Transactional(readOnly = true)
    public Page<VerificationSummaryResponse> list(Pageable pageable) {
        return verificationDataRepository.findSummaries(pageable);
    }

    @Transactional(readOnly = true)
    public Page<VerificationSummaryResponse> listByStatus(VerificationStatus status, Pageable pageable) {
        return verificationDataRepository.findSummariesByStatus(status, pageable);
    }
}

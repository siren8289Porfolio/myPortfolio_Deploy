package com.mido.verification.common.controller;

import com.mido.verification.common.dto.VerificationSummaryResponse;
import com.mido.verification.common.entity.VerificationStatus;
import com.mido.verification.common.service.VerificationListService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verifications")
public class VerificationListController {

    private final VerificationListService verificationListService;

    public VerificationListController(VerificationListService verificationListService) {
        this.verificationListService = verificationListService;
    }

    @GetMapping
    public Page<VerificationSummaryResponse> list(
            @RequestParam(required = false) VerificationStatus status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        if (status != null) {
            return verificationListService.listByStatus(status, pageable);
        }
        return verificationListService.list(pageable);
    }
}

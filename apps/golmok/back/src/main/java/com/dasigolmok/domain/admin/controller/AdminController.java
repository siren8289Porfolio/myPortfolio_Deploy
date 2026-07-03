package com.dasigolmok.domain.admin.controller;

import com.dasigolmok.domain.admin.dto.ReportResponse;
import com.dasigolmok.domain.admin.dto.StatusUpdateRequest;
import com.dasigolmok.domain.admin.dto.ReportStatusRequest;
import com.dasigolmok.domain.admin.service.AdminService;
import com.dasigolmok.domain.story.dto.StoryCardResponse;
import com.dasigolmok.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stories")
    public ApiResponse<List<StoryCardResponse>> stories(@RequestParam(required = false) String status) {
        return ApiResponse.ok(adminService.getStories(status));
    }

    @PatchMapping("/stories/{id}/status")
    public ApiResponse<StoryCardResponse> updateStatus(
            @PathVariable String id,
            @RequestBody StatusUpdateRequest request) {
        return ApiResponse.ok(
                adminService.updateStoryStatus(id, request.getStatus(), request.getNote()),
                "스토리 상태가 변경되었습니다.");
    }

    @GetMapping("/reports")
    public ApiResponse<List<ReportResponse>> reports(@RequestParam(required = false) String status) {
        return ApiResponse.ok(adminService.getReports(status));
    }

    @PatchMapping("/reports/{id}")
    public ApiResponse<ReportResponse> updateReport(
            @PathVariable String id,
            @RequestBody ReportStatusRequest request) {
        return ApiResponse.ok(adminService.updateReportStatus(id, request.getStatus()), "신고 상태가 변경되었습니다.");
    }
}

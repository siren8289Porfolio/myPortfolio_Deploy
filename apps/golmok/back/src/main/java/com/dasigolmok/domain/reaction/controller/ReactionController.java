package com.dasigolmok.domain.reaction.controller;

import com.dasigolmok.domain.reaction.dto.ReportRequest;
import com.dasigolmok.domain.reaction.service.ReactionService;
import com.dasigolmok.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/stories/{storyId}")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    @PostMapping("/likes")
    public ApiResponse<Map<String, Object>> like(@PathVariable String storyId) {
        return ApiResponse.ok(reactionService.like(storyId), "좋아요가 반영되었습니다.");
    }

    @DeleteMapping("/likes")
    public ApiResponse<Map<String, Object>> unlike(@PathVariable String storyId) {
        return ApiResponse.ok(reactionService.unlike(storyId), "좋아요가 취소되었습니다.");
    }

    @PostMapping("/reports")
    public ApiResponse<Void> report(@PathVariable String storyId, @RequestBody ReportRequest request) {
        reactionService.report(storyId, request.getReason());
        return ApiResponse.okMessage("신고가 접수되었습니다.");
    }
}

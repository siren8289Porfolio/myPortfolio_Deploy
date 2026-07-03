package com.dasigolmok.domain.story.controller;

import com.dasigolmok.domain.story.dto.*;
import com.dasigolmok.domain.story.service.StoryService;
import com.dasigolmok.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stories")
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    @PostMapping
    public ApiResponse<CreateStoryResponse> create(@Valid @RequestBody CreateStoryRequest request) {
        return ApiResponse.ok(storyService.createStory(request), "스토리가 등록되었습니다. 검토 후 공개됩니다.");
    }

    @PostMapping("/images")
    public ApiResponse<ImageUploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(storyService.uploadImage(file), "이미지가 업로드되었습니다.");
    }

    @GetMapping("/map")
    public ApiResponse<List<MapMarkerResponse>> mapStories(@RequestParam(required = false) String regionId) {
        return ApiResponse.ok(storyService.getMapStories(regionId));
    }

    @GetMapping("/curation")
    public ApiResponse<List<StoryCardResponse>> curationStories(
            @RequestParam(required = false, defaultValue = "popular") String sort,
            @RequestParam(required = false) String tag) {
        return ApiResponse.ok(storyService.getCurationStories(sort, tag));
    }

    @GetMapping("/{storyId}")
    public ApiResponse<StoryDetailResponse> detail(@PathVariable String storyId) {
        return ApiResponse.ok(storyService.getStory(storyId));
    }
}

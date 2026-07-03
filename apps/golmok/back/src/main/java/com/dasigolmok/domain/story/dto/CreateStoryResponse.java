package com.dasigolmok.domain.story.dto;

import lombok.Getter;

@Getter
public class CreateStoryResponse {
    private final String storyId;

    public CreateStoryResponse(String storyId) {
        this.storyId = storyId;
    }
}

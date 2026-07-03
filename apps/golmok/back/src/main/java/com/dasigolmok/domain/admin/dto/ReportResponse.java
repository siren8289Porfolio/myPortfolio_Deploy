package com.dasigolmok.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportResponse {
    private String id;
    private String storyId;
    private String storyTitle;
    private String reporterNickname;
    private String reason;
    private String status;
    private String createdAt;
}

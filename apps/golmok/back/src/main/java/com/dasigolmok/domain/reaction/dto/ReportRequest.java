package com.dasigolmok.domain.reaction.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequest {
    @NotBlank
    private String reason;
}

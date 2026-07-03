package com.dasigolmok.global.response;

import lombok.Getter;

@Getter
public enum ErrorCode {
    VALIDATION_ERROR("VALIDATION_ERROR"),
    UNAUTHORIZED("UNAUTHORIZED"),
    FORBIDDEN("FORBIDDEN"),
    NOT_FOUND("NOT_FOUND"),
    CONFLICT("CONFLICT"),
    INTERNAL_ERROR("INTERNAL_ERROR");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }
}

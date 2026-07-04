package com.example.refactor.common;

/** 예외 발생 시 클라이언트에 내려주는 응답 형태를 통일한다. */
public record ErrorResponse(String message) {
}

package com.example.refactor.common;

/** 조회 대상이 없을 때 던지는 예외. GlobalExceptionHandler가 404로 변환한다. */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

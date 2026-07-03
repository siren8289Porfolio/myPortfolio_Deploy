package com.dasigolmok.domain.auth.controller;

import com.dasigolmok.domain.auth.dto.LoginRequest;
import com.dasigolmok.domain.auth.dto.SignupRequest;
import com.dasigolmok.domain.auth.dto.TokenResponse;
import com.dasigolmok.domain.auth.dto.UserResponse;
import com.dasigolmok.domain.auth.service.AuthService;
import com.dasigolmok.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ApiResponse.ok(authService.signup(request), "회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request), "로그인되었습니다.");
    }
}

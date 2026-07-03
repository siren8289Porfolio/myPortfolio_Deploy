package com.dasigolmok.domain.auth.service;

import com.dasigolmok.domain.admin.entity.AuditAction;
import com.dasigolmok.domain.admin.entity.AuditLog;
import com.dasigolmok.domain.admin.repository.AuditLogRepository;
import com.dasigolmok.domain.auth.dto.*;
import com.dasigolmok.domain.user.entity.Role;
import com.dasigolmok.domain.user.entity.User;
import com.dasigolmok.domain.user.repository.UserRepository;
import com.dasigolmok.global.exception.BusinessException;
import com.dasigolmok.global.response.ErrorCode;
import com.dasigolmok.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public UserResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.CONFLICT, "이미 사용 중인 이메일입니다.");
        }
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role(Role.USER)
                .build();
        userRepository.save(user);
        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .action(AuditAction.CREATE)
                .entityType("User")
                .entityId(user.getId())
                .detail("회원가입")
                .build());
        return toUserResponse(user);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        String token = jwtTokenProvider.createToken(
                user.getId(), user.getEmail(), user.getNickname(), user.getRole().name());
        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .action(AuditAction.LOGIN)
                .entityType("User")
                .entityId(user.getId())
                .detail("로그인")
                .build());
        return TokenResponse.builder()
                .accessToken(token)
                .user(toUserResponse(user))
                .build();
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }
}

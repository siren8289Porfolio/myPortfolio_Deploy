package com.dasigolmok.domain.auth.dto;

import com.dasigolmok.domain.user.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private String id;
    private String email;
    private String nickname;
    private Role role;
}

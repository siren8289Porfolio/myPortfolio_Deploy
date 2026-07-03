package com.dasigolmok.global.security;

import com.dasigolmok.domain.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPrincipal {
    private final String id;
    private final String email;
    private final String nickname;
    private final Role role;

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}

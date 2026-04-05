package com.wesleyruan.e_commerce.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;


@Component
public class SecurityUtils {

    private SecurityUtils() {}

    public Jwt getJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new RuntimeException("User not authenticated");
        }

        return jwt;
    }

    public Long getUserId() {
        Object claim = getJwt().getClaim("id");

        if (claim == null) {
            throw new RuntimeException("Invalid token: missing user id");
        }

        return Long.valueOf(claim.toString());
    }

    public String getEmail() {
        return getJwt().getSubject(); // melhor que "sub"
    }
}
package com.cxj.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

/**
 * 当前登录用户上下文工具
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<Authentication> currentAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    public static Optional<String> currentUsername() {
        return currentAuthentication()
                .map(Authentication::getPrincipal)
                .map(principal -> switch (principal) {
                    case UserDetails ud -> ud.getUsername();
                    case String s -> s;
                    default -> null;
                });
    }

    public static boolean isAuthenticated() {
        return currentAuthentication()
                .map(auth -> auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                .orElse(false);
    }
}

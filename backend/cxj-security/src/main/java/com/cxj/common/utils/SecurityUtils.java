package com.cxj.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
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

    /**
     * 获取当前登录用户 ID（从 JWT claims 中提取）
     */
    public static Optional<Long> currentUserId() {
        return currentAuthentication()
                .map(Authentication::getDetails)
                .filter(Map.class::isInstance)
                .map(d -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> details = (Map<String, Object>) d;
                    return details.get("uid");
                })
                .filter(Number.class::isInstance)
                .map(uid -> ((Number) uid).longValue());
    }

    /**
     * 获取当前 token 的 jti
     */
    public static Optional<String> currentJti() {
        return currentAuthentication()
                .map(Authentication::getDetails)
                .filter(Map.class::isInstance)
                .map(d -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> details = (Map<String, Object>) d;
                    return details.get("jti");
                })
                .filter(String.class::isInstance)
                .map(String.class::cast);
    }

    public static boolean isAuthenticated() {
        return currentAuthentication()
                .map(auth -> auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal()))
                .orElse(false);
    }
}

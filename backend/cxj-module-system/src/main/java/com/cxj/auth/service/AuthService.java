package com.cxj.auth.service;

import com.cxj.common.enums.ResultCode;
import com.cxj.common.exception.BusinessException;
import com.cxj.common.security.ConcurrentSessionService;
import com.cxj.common.security.JwtTokenProvider;
import com.cxj.common.security.RefreshTokenService;
import com.cxj.auth.controller.dto.LoginDTO;
import com.cxj.auth.controller.vo.LoginVO;
import com.cxj.user.controller.vo.UserVO;
import com.cxj.user.converter.UserConverter;
import com.cxj.user.entity.User;
import com.cxj.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UserConverter userConverter;
    private final ConcurrentSessionService concurrentSessionService;
    private final RefreshTokenService refreshTokenService;

    public LoginVO login(LoginDTO dto) {
        User user = userService.loadByUsername(dto.username());
        if (user == null || !passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_FAILED);
        }
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        }
        String token = tokenProvider.generate(user.getUsername(),
                Map.of("uid", user.getId(), "roles", List.of("ROLE_USER")));

        // 注册并发会话
        tokenProvider.extractJti(token).ifPresent(jti ->
                concurrentSessionService.registerToken(user.getId(), jti, tokenProvider.expirationSeconds())
        );

        // 生成 refresh_token
        String refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return LoginVO.of(token, refreshToken, tokenProvider.expirationSeconds(), userConverter.toVO(user));
    }

    /**
     * 用 refresh_token 换取新的 access_token
     */
    public LoginVO refresh(String refreshToken) {
        Optional<Long> userIdOpt = refreshTokenService.validateAndConsume(refreshToken);
        if (userIdOpt.isEmpty()) {
            throw new BusinessException(ResultCode.TOKEN_EXPIRED, "refresh token 已过期，请重新登录");
        }

        Long userId = userIdOpt.get();
        User user = userService.getById(userId);
        if (user == null || !"ACTIVE".equals(user.getStatus())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "账号不可用");
        }

        String newToken = tokenProvider.generate(user.getUsername(),
                Map.of("uid", user.getId(), "roles", List.of("ROLE_USER")));

        // 注册新会话
        tokenProvider.extractJti(newToken).ifPresent(jti ->
                concurrentSessionService.registerToken(user.getId(), jti, tokenProvider.expirationSeconds())
        );

        // 生成新的 refresh_token（一次性轮转）
        String newRefreshToken = refreshTokenService.createRefreshToken(user.getId());

        return LoginVO.of(newToken, newRefreshToken, tokenProvider.expirationSeconds(), userConverter.toVO(user));
    }
}

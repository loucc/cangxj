package com.cxj.modules.system.auth.controller;

import com.cxj.common.enums.ResultCode;
import com.cxj.common.exception.BusinessException;
import com.cxj.common.response.R;
import com.cxj.common.security.ConcurrentSessionService;
import com.cxj.common.security.JwtTokenProvider;
import com.cxj.common.security.RateLimitService;
import com.cxj.common.security.RefreshTokenService;
import com.cxj.common.security.TokenBlacklistService;
import com.cxj.common.utils.SecurityUtils;
import com.cxj.modules.system.auth.controller.dto.LoginDTO;
import com.cxj.modules.system.user.controller.dto.UserCreateDTO;
import com.cxj.modules.system.auth.controller.dto.RefreshDTO;
import com.cxj.modules.system.auth.controller.vo.LoginVO;
import com.cxj.modules.system.user.controller.vo.UserVO;
import com.cxj.modules.system.auth.service.AuthService;
import com.cxj.modules.system.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "认证", description = "登录 / 注册 / 登出 / 刷新")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final RateLimitService rateLimitService;
    private final TokenBlacklistService blacklistService;
    private final ConcurrentSessionService concurrentSessionService;
    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider tokenProvider;

    @Operation(summary = "账号密码登录")
    @PostMapping("/login")
    public R<LoginVO> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        String clientIp = extractClientIp(request);

        // IP + 用户名双维度限流
        if (rateLimitService.isRateLimited("ip:" + clientIp)
                || rateLimitService.isRateLimited("user:" + dto.username())) {
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS, "登录尝试过于频繁，请稍后再试");
        }

        LoginVO result = authService.login(dto);

        // 登录成功，重置限流计数
        rateLimitService.resetRateLimit("ip:" + clientIp);
        rateLimitService.resetRateLimit("user:" + dto.username());

        return R.ok(result);
    }

    @Operation(summary = "注册新用户")
    @PostMapping("/register")
    public R<UserVO> register(@Valid @RequestBody UserCreateDTO dto) {
        return R.ok(userService.create(dto));
    }

    @Operation(summary = "刷新 access_token")
    @PostMapping("/refresh")
    public R<LoginVO> refresh(@Valid @RequestBody RefreshDTO dto) {
        return R.ok(authService.refresh(dto.refreshToken()));
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public R<Void> logout(HttpServletRequest request,
                          @RequestBody(required = false) RefreshDTO dto) {
        // 从请求头获取当前 access_token 并加入黑名单
        String token = extractToken(request);
        if (token != null) {
            blacklistService.blacklist(token);

            // 移除并发会话记录
            SecurityUtils.currentUserId().ifPresent(userId ->
                    tokenProvider.extractJti(token).ifPresent(jti ->
                            concurrentSessionService.removeToken(userId, jti)
                    )
            );
        }

        // 撤销 refresh_token
        if (dto != null && dto.refreshToken() != null) {
            refreshTokenService.revoke(dto.refreshToken());
        }

        return R.ok();
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isEmpty()) {
            return realIp;
        }
        return request.getRemoteAddr();
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}

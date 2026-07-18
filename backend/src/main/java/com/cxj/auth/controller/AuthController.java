package com.cxj.auth.controller;

import com.cxj.common.enums.ResultCode;
import com.cxj.common.exception.BusinessException;
import com.cxj.common.response.R;
import com.cxj.common.security.RateLimitService;
import com.cxj.auth.controller.dto.LoginDTO;
import com.cxj.user.controller.dto.UserCreateDTO;
import com.cxj.auth.controller.vo.LoginVO;
import com.cxj.user.controller.vo.UserVO;
import com.cxj.auth.service.AuthService;
import com.cxj.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "认证", description = "登录 / 注册")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final RateLimitService rateLimitService;

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
}

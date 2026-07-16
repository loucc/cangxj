package com.cxj.auth.service;

import com.cxj.common.enums.ResultCode;
import com.cxj.common.exception.BusinessException;
import com.cxj.common.security.JwtTokenProvider;
import com.cxj.auth.controller.dto.LoginDTO;
import com.cxj.auth.controller.vo.LoginVO;
import com.cxj.user.controller.vo.UserVO;
import com.cxj.user.entity.User;
import com.cxj.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

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
        return LoginVO.of(token, tokenProvider.expirationSeconds(), UserVO.from(user));
    }
}

package com.cxj.auth.service;

import com.cxj.common.enums.ResultCode;
import com.cxj.common.exception.BusinessException;
import com.cxj.common.security.ConcurrentSessionService;
import com.cxj.common.security.JwtTokenProvider;
import com.cxj.common.security.RefreshTokenService;
import com.cxj.auth.controller.dto.LoginDTO;
import com.cxj.user.entity.User;
import com.cxj.auth.controller.vo.LoginVO;
import com.cxj.user.controller.vo.UserVO;
import com.cxj.user.converter.UserConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.cxj.user.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserConverter userConverter;

    @Mock
    private ConcurrentSessionService concurrentSessionService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    private User activeUser;

    @BeforeEach
    void setUp() {
        activeUser = User.builder()
                .id(1L)
                .username("alice")
                .password("$2a$10$encoded-password")
                .nickname("Alice")
                .email("alice@example.com")
                .phone("13800138000")
                .status("ACTIVE")
                .build();
    }

    @Test
    void login_shouldSucceedWithValidCredentials() {
        LoginDTO dto = new LoginDTO("alice", "correct-password");
        when(userService.loadByUsername("alice")).thenReturn(activeUser);
        when(passwordEncoder.matches("correct-password", "$2a$10$encoded-password")).thenReturn(true);
        when(tokenProvider.generate(eq("alice"), anyMap())).thenReturn("mock-jwt-token");
        when(tokenProvider.expirationSeconds()).thenReturn(7200L);
        when(tokenProvider.extractJti("mock-jwt-token")).thenReturn(Optional.of("mock-jti"));
        when(refreshTokenService.createRefreshToken(1L)).thenReturn("mock-refresh-token");

        UserVO userVO = new UserVO(1L, "alice", "Alice", "alice@example.com",
                "13800138000", "ACTIVE", null, null);
        when(userConverter.toVO(activeUser)).thenReturn(userVO);

        LoginVO result = authService.login(dto);

        assertNotNull(result);
        assertEquals("mock-jwt-token", result.accessToken());
        assertEquals("mock-refresh-token", result.refreshToken());
        assertEquals("Bearer", result.tokenType());
        assertEquals(7200L, result.expiresIn());
        assertEquals("alice", result.user().username());
    }

    @Test
    void login_shouldThrowWhenUserNotFound() {
        LoginDTO dto = new LoginDTO("nonexistent", "password");
        when(userService.loadByUsername("nonexistent")).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(dto));
        assertEquals(ResultCode.LOGIN_FAILED.getCode(), ex.getCode());
    }

    @Test
    void login_shouldThrowWhenPasswordWrong() {
        LoginDTO dto = new LoginDTO("alice", "wrong-password");
        when(userService.loadByUsername("alice")).thenReturn(activeUser);
        when(passwordEncoder.matches("wrong-password", "$2a$10$encoded-password")).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(dto));
        assertEquals(ResultCode.LOGIN_FAILED.getCode(), ex.getCode());
    }

    @Test
    void login_shouldThrowWhenAccountDisabled() {
        User disabledUser = User.builder()
                .id(2L)
                .username("disabled")
                .password("$2a$10$encoded")
                .status("DISABLED")
                .build();

        LoginDTO dto = new LoginDTO("disabled", "password");
        when(userService.loadByUsername("disabled")).thenReturn(disabledUser);
        when(passwordEncoder.matches("password", "$2a$10$encoded")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(dto));
        assertEquals(ResultCode.FORBIDDEN.getCode(), ex.getCode());
    }

    @Test
    void refresh_shouldSucceedWithValidRefreshToken() {
        when(refreshTokenService.validateAndConsume("valid-refresh")).thenReturn(Optional.of(1L));
        when(userService.getById(1L)).thenReturn(activeUser);
        when(tokenProvider.generate(eq("alice"), anyMap())).thenReturn("new-jwt-token");
        when(tokenProvider.expirationSeconds()).thenReturn(7200L);
        when(tokenProvider.extractJti("new-jwt-token")).thenReturn(Optional.of("new-jti"));
        when(refreshTokenService.createRefreshToken(1L)).thenReturn("new-refresh-token");

        UserVO userVO = new UserVO(1L, "alice", "Alice", "alice@example.com",
                "13800138000", "ACTIVE", null, null);
        when(userConverter.toVO(activeUser)).thenReturn(userVO);

        LoginVO result = authService.refresh("valid-refresh");

        assertNotNull(result);
        assertEquals("new-jwt-token", result.accessToken());
        assertEquals("new-refresh-token", result.refreshToken());
    }

    @Test
    void refresh_shouldThrowWhenRefreshTokenExpired() {
        when(refreshTokenService.validateAndConsume("expired-refresh")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.refresh("expired-refresh"));
        assertEquals(ResultCode.TOKEN_EXPIRED.getCode(), ex.getCode());
    }

    @Test
    void refresh_shouldThrowWhenUserDisabled() {
        User disabledUser = User.builder()
                .id(2L).username("disabled").status("DISABLED").build();

        when(refreshTokenService.validateAndConsume("valid-refresh")).thenReturn(Optional.of(2L));
        when(userService.getById(2L)).thenReturn(disabledUser);

        BusinessException ex = assertThrows(BusinessException.class, () -> authService.refresh("valid-refresh"));
        assertEquals(ResultCode.FORBIDDEN.getCode(), ex.getCode());
    }
}

package com.cxj.auth.controller;

import com.cxj.common.enums.ResultCode;
import com.cxj.common.exception.BusinessException;
import com.cxj.common.security.JwtAuthenticationFilter;
import com.cxj.common.security.JwtTokenProvider;
import com.cxj.common.security.RateLimitService;
import com.cxj.common.security.SecurityResponseHandlers;
import com.cxj.auth.controller.dto.LoginDTO;
import com.cxj.user.controller.dto.UserCreateDTO;
import com.cxj.auth.controller.vo.LoginVO;
import com.cxj.user.controller.vo.UserVO;
import com.cxj.auth.service.AuthService;
import com.cxj.user.service.UserService;
import com.cxj.user.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(com.cxj.config.WebMvcConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private RateLimitService rateLimitService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private SecurityResponseHandlers securityResponseHandlers;

    @MockitoBean
    private UserMapper userMapper;

    @Test
    void login_shouldReturnToken_onValidCredentials() throws Exception {
        UserVO userVO = new UserVO(1L, "alice", "Alice", "alice@test.com",
                "13800138000", "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
        LoginVO loginVO = LoginVO.of("mock-token", 7200L, userVO);
        when(authService.login(any(LoginDTO.class))).thenReturn(loginVO);
        when(rateLimitService.isRateLimited(anyString())).thenReturn(false);

        LoginDTO dto = new LoginDTO("alice", "password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.accessToken").value("mock-token"))
                .andExpect(jsonPath("$.data.user.username").value("alice"));

        verify(rateLimitService).resetRateLimit("user:alice");
    }

    @Test
    void login_shouldReturn429_whenRateLimited() throws Exception {
        when(rateLimitService.isRateLimited(anyString())).thenReturn(true);

        LoginDTO dto = new LoginDTO("alice", "password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.TOO_MANY_REQUESTS.getCode()));
    }

    @Test
    void login_shouldReturnValidationError_whenBodyInvalid() throws Exception {
        // 缺少必填字段 username 和 password
        String invalidBody = "{}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResultCode.VALIDATION_ERROR.getCode()));
    }

    @Test
    void register_shouldReturnUserVO_onSuccess() throws Exception {
        UserVO userVO = new UserVO(1L, "bob", "Bob", "bob@test.com",
                "13900139000", "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
        when(userService.create(any(UserCreateDTO.class))).thenReturn(userVO);

        UserCreateDTO dto = new UserCreateDTO("bob", "P@ssw0rd!", "Bob",
                "bob@test.com", "13900139000");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.username").value("bob"));
    }

    @Test
    void register_shouldReturnValidationError_whenUsernameTooShort() throws Exception {
        UserCreateDTO dto = new UserCreateDTO("ab", "P@ssw0rd!", "Bob",
                "bob@test.com", "13900139000");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResultCode.VALIDATION_ERROR.getCode()));
    }

    @Test
    void login_shouldReturnBusinessError_whenAuthServiceThrows() throws Exception {
        when(rateLimitService.isRateLimited(anyString())).thenReturn(false);
        when(authService.login(any(LoginDTO.class)))
                .thenThrow(new BusinessException(ResultCode.LOGIN_FAILED));

        LoginDTO dto = new LoginDTO("alice", "wrong-password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.LOGIN_FAILED.getCode()));
    }
}

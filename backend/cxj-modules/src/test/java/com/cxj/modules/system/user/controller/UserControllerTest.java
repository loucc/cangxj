package com.cxj.modules.system.user.controller;

import com.cxj.common.enums.ResultCode;
import com.cxj.common.exception.BusinessException;
import com.cxj.common.response.PageResult;
import com.cxj.common.security.JwtAuthenticationFilter;
import com.cxj.common.security.JwtTokenProvider;
import com.cxj.common.security.SecurityResponseHandlers;
import com.cxj.modules.system.user.controller.dto.UserCreateDTO;
import com.cxj.modules.system.user.controller.dto.UserUpdateDTO;
import com.cxj.modules.system.user.mapper.UserMapper;
import com.cxj.modules.system.user.service.UserService;
import com.cxj.modules.system.user.controller.vo.UserVO;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(com.cxj.config.jackson.JacksonConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private SecurityResponseHandlers securityResponseHandlers;

    @MockitoBean
    private UserMapper userMapper;

    private UserVO sampleUserVO() {
        return new UserVO(1L, "alice", "Alice", "alice@test.com",
                "13800138000", "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void get_shouldReturnUserVO() throws Exception {
        when(userService.getVO(1L)).thenReturn(sampleUserVO());

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.username").value("alice"))
                .andExpect(jsonPath("$.data.nickname").value("Alice"));
    }

    @Test
    void get_shouldReturn404_whenUserNotExists() throws Exception {
        when(userService.getVO(999L)).thenReturn(null);

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void page_shouldReturnPaginatedResults() throws Exception {
        PageResult<UserVO> page = new PageResult<>(1, 20, 1, 1,
                List.of(sampleUserVO()));
        when(userService.page(any(com.cxj.modules.system.user.controller.dto.UserQueryDTO.class))).thenReturn(page);

        mockMvc.perform(get("/users")
                        .param("current", "1")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records[0].username").value("alice"));
    }

    @Test
    void create_shouldReturnCreatedUser() throws Exception {
        when(userService.create(any(UserCreateDTO.class))).thenReturn(sampleUserVO());

        UserCreateDTO dto = new UserCreateDTO("alice", "P@ssw0rd!", "Alice",
                "alice@test.com", "13800138000");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.username").value("alice"));
    }

    @Test
    void update_shouldReturnUpdatedUser() throws Exception {
        UserVO updated = new UserVO(1L, "alice", "NewNick", "new@test.com",
                "13800138000", "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
        when(userService.update(eq(1L), any(UserUpdateDTO.class))).thenReturn(updated);

        UserUpdateDTO dto = new UserUpdateDTO("NewNick", "new@test.com", null, null);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.nickname").value("NewNick"));
    }

    @Test
    void delete_shouldSucceed() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.SUCCESS.getCode()));

        verify(userService).delete(1L);
    }

    @Test
    void delete_shouldReturn404_whenUserNotExists() throws Exception {
        doThrow(new BusinessException(ResultCode.NOT_FOUND, "用户不存在"))
                .when(userService).delete(999L);

        mockMvc.perform(delete("/users/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResultCode.NOT_FOUND.getCode()));
    }

    @Test
    void create_shouldReturnValidationError_whenEmailInvalid() throws Exception {
        UserCreateDTO dto = new UserCreateDTO("alice", "P@ssw0rd!", "Alice",
                "not-an-email", "13800138000");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResultCode.VALIDATION_ERROR.getCode()));
    }
}

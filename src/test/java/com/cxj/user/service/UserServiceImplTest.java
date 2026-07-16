package com.cxj.user.service;

import com.cxj.common.enums.ResultCode;
import com.cxj.common.exception.BusinessException;
import com.cxj.user.controller.dto.UserUpdateDTO;
import com.cxj.user.mapper.UserMapper;
import com.cxj.user.entity.User;
import com.cxj.user.controller.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        // ServiceImpl 依赖 baseMapper 字段，需要通过反射注入
        ReflectionTestUtils.setField(userService, "baseMapper", userMapper);

        existingUser = User.builder()
                .id(1L)
                .username("alice")
                .password("$2a$10$encoded")
                .nickname("Alice")
                .email("alice@example.com")
                .phone("13800138000")
                .status("ACTIVE")
                .version(0)
                .deleted(0)
                .build();
    }

    @Test
    void getVO_shouldReturnUserVO_whenUserExists() {
        when(userMapper.selectById(1L)).thenReturn(existingUser);

        UserVO result = userService.getVO(1L);

        assertNotNull(result);
        assertEquals("alice", result.username());
        assertEquals("Alice", result.nickname());
        assertEquals("ACTIVE", result.status());
        verify(userMapper).selectById(1L);
    }

    @Test
    void getVO_shouldReturnNull_whenUserNotExists() {
        when(userMapper.selectById(999L)).thenReturn(null);

        UserVO result = userService.getVO(999L);

        assertNull(result);
    }

    @Test
    void update_shouldSucceed_whenUserExists() {
        UserUpdateDTO dto = new UserUpdateDTO("NewNick", "new@example.com", null, null);
        when(userMapper.selectById(1L)).thenReturn(existingUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        UserVO result = userService.update(1L, dto);

        assertNotNull(result);
        assertEquals("NewNick", result.nickname());
        assertEquals("new@example.com", result.email());
        verify(userMapper).updateById(any(User.class));
    }

    @Test
    void update_shouldThrow_whenUserNotExists() {
        UserUpdateDTO dto = new UserUpdateDTO("NewNick", null, null, null);
        when(userMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.update(999L, dto));
        assertEquals(ResultCode.NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void delete_shouldSucceed_whenUserExists() {
        when(userMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> userService.delete(1L));
        verify(userMapper).deleteById(1L);
    }

    @Test
    void delete_shouldThrow_whenUserNotExists() {
        when(userMapper.deleteById(999L)).thenReturn(0);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> userService.delete(999L));
        assertEquals(ResultCode.NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void loadByUsername_shouldReturnUser_whenExists() {
        when(userMapper.selectOne(any(), any(Boolean.class))).thenReturn(existingUser);

        User result = userService.loadByUsername("alice");

        assertNotNull(result);
        assertEquals("alice", result.getUsername());
    }

    @Test
    void loadByUsername_shouldReturnNull_whenNotExists() {
        when(userMapper.selectOne(any(), any(Boolean.class))).thenReturn(null);

        User result = userService.loadByUsername("nonexistent");

        assertNull(result);
    }
}

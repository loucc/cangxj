package com.cxj.user.converter;

import com.cxj.user.controller.dto.UserCreateDTO;
import com.cxj.user.controller.dto.UserUpdateDTO;
import com.cxj.user.controller.vo.UserVO;
import com.cxj.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserConverterTest {

    private UserConverter converter;

    @BeforeEach
    void setUp() {
        converter = new UserConverterImpl();
    }

    @Test
    void toVO_shouldMapAllFields() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(1L).username("alice").password("secret").nickname("Alice")
                .email("alice@test.com").phone("13800138000").status("ACTIVE")
                .createdAt(now).updatedAt(now)
                .createdBy("sys").updatedBy("sys").version(0).deleted(0)
                .build();

        UserVO vo = converter.toVO(user);

        assertEquals(1L, vo.id());
        assertEquals("alice", vo.username());
        assertEquals("Alice", vo.nickname());
        assertEquals("alice@test.com", vo.email());
        assertEquals("13800138000", vo.phone());
        assertEquals("ACTIVE", vo.status());
        assertEquals(now, vo.createdAt());
        assertEquals(now, vo.updatedAt());
    }

    @Test
    void toVO_shouldReturnNull_forNullInput() {
        assertNull(converter.toVO(null));
    }

    @Test
    void toVOList_shouldMapList() {
        User u1 = User.builder().id(1L).username("a").status("ACTIVE").build();
        User u2 = User.builder().id(2L).username("b").status("DISABLED").build();

        List<UserVO> result = converter.toVOList(List.of(u1, u2));

        assertEquals(2, result.size());
        assertEquals("a", result.get(0).username());
        assertEquals("b", result.get(1).username());
    }

    @Test
    void toVOList_shouldReturnNull_forNullInput() {
        assertNull(converter.toVOList(null));
    }

    @Test
    void fromCreateDTO_shouldMapFieldsAndSetActive() {
        UserCreateDTO dto = new UserCreateDTO("alice", "P@ssw0rd!", "Alice",
                "alice@test.com", "13800138000");

        User user = converter.fromCreateDTO(dto);

        assertEquals("alice", user.getUsername());
        assertEquals("Alice", user.getNickname());
        assertEquals("alice@test.com", user.getEmail());
        assertEquals("13800138000", user.getPhone());
        assertEquals("ACTIVE", user.getStatus());
        // password 应由 Service 层手动 encode，converter 不映射
        assertNull(user.getPassword());
        // id 和审计字段应被忽略
        assertNull(user.getId());
        assertNull(user.getCreatedAt());
    }

    @Test
    void fromCreateDTO_shouldReturnNull_forNullInput() {
        assertNull(converter.fromCreateDTO(null));
    }

    @Test
    void updateFromDTO_shouldUpdateNonNullFields() {
        User user = User.builder()
                .id(1L).username("alice").nickname("OldNick")
                .email("old@test.com").phone("13800138000").status("ACTIVE")
                .build();

        UserUpdateDTO dto = new UserUpdateDTO("NewNick", "new@test.com", null, null);
        converter.updateFromDTO(dto, user);

        assertEquals("NewNick", user.getNickname());
        assertEquals("new@test.com", user.getEmail());
        // null 字段不应被覆盖
        assertEquals("13800138000", user.getPhone());
        assertEquals("ACTIVE", user.getStatus());
        // username 不应被修改
        assertEquals("alice", user.getUsername());
    }

    @Test
    void updateFromDTO_shouldSkipAllNullFields() {
        User user = User.builder()
                .id(1L).nickname("Keep").email("keep@test.com").status("ACTIVE")
                .build();

        UserUpdateDTO dto = new UserUpdateDTO(null, null, null, null);
        converter.updateFromDTO(dto, user);

        assertEquals("Keep", user.getNickname());
        assertEquals("keep@test.com", user.getEmail());
        assertEquals("ACTIVE", user.getStatus());
    }
}

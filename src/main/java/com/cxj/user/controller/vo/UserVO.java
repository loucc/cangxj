package com.cxj.user.controller.vo;

import com.cxj.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "用户视图对象")
public record UserVO(
        Long id,
        String username,
        String nickname,
        String email,
        String phone,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserVO from(User u) {
        if (u == null) return null;
        return new UserVO(u.getId(), u.getUsername(), u.getNickname(), u.getEmail(),
                u.getPhone(), u.getStatus(), u.getCreatedAt(), u.getUpdatedAt());
    }
}

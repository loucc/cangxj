package com.cxj.modules.system.user.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "用户更新入参")
public record UserUpdateDTO(
        @Size(max = 64) String nickname,
        @Email @Size(max = 128) String email,
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确") String phone,
        @Pattern(regexp = "ACTIVE|DISABLED|LOCKED", message = "状态只能是 ACTIVE/DISABLED/LOCKED") String status
) {
}

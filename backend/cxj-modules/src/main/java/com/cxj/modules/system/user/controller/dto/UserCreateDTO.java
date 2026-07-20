package com.cxj.modules.system.user.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 用户创建入参（Record DTO）
 */
@Schema(description = "用户创建入参")
public record UserCreateDTO(
        @Schema(description = "用户名", example = "alice")
        @NotBlank @Size(min = 3, max = 32)
        String username,

        @Schema(description = "密码", example = "P@ssw0rd!")
        @NotBlank @Size(min = 6, max = 64)
        String password,

        @Schema(description = "昵称")
        @Size(max = 64)
        String nickname,

        @Schema(description = "邮箱")
        @Email @Size(max = 128)
        String email,

        @Schema(description = "手机号")
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
        String phone
) {
}

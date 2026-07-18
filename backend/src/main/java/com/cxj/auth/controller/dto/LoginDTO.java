package com.cxj.auth.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "登录入参")
public record LoginDTO(
        @NotBlank String username,
        @NotBlank String password
) {
}

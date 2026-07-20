package com.cxj.modules.system.auth.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "刷新 token 请求")
public record RefreshDTO(
        @NotBlank String refreshToken
) {
}

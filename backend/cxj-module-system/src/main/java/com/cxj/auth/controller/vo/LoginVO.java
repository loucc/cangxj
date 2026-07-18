package com.cxj.auth.controller.vo;

import com.cxj.user.controller.vo.UserVO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "登录响应")
public record LoginVO(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserVO user
) {
    public static LoginVO of(String token, String refreshToken, long expiresIn, UserVO user) {
        return new LoginVO(token, refreshToken, "Bearer", expiresIn, user);
    }
}

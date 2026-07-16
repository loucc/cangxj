package com.cxj.auth.controller.vo;

import com.cxj.user.controller.vo.UserVO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "登录响应")
public record LoginVO(
        String accessToken,
        String tokenType,
        long expiresIn,
        UserVO user
) {
    public static LoginVO of(String token, long expiresIn, UserVO user) {
        return new LoginVO(token, "Bearer", expiresIn, user);
    }
}

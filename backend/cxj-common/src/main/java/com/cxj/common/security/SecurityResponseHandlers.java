package com.cxj.common.security;

import com.cxj.common.enums.ResultCode;
import com.cxj.common.response.R;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 未认证 / 无权限 时以标准 JSON 结构返回
 */
@Component
@RequiredArgsConstructor
public class SecurityResponseHandlers {

    private final ObjectMapper objectMapper;

    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, ex) -> writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, ResultCode.UNAUTHORIZED);
    }

    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> writeJson(response, HttpServletResponse.SC_FORBIDDEN, ResultCode.FORBIDDEN);
    }

    private void writeJson(HttpServletResponse response, int status, ResultCode code) throws java.io.IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(R.fail(code)));
    }
}

package com.cxj.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.cxj.common.enums.ResultCode;
import com.cxj.common.response.R;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * JWT 认证过滤器：从请求头解析 token，校验黑名单，构造 Spring Security 认证上下文
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final TokenBlacklistService blacklistService;
    private final ObjectMapper objectMapper;

    @Value("${app.security.jwt.header:Authorization}")
    private String header;

    @Value("${app.security.jwt.prefix:Bearer }")
    private String prefix;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        Optional<String> tokenOpt = resolveToken(request);
        if (tokenOpt.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        String token = tokenOpt.get();

        // 黑名单检查
        if (blacklistService.isBlacklisted(token)) {
            writeError(response, ResultCode.TOKEN_INVALID, "token 已失效，请重新登录");
            return;
        }

        Optional<Claims> claimsOpt = tokenProvider.parse(token);
        if (claimsOpt.isEmpty()) {
            writeError(response, ResultCode.TOKEN_INVALID);
            return;
        }
        Claims claims = claimsOpt.get();
        Collection<SimpleGrantedAuthority> authorities = extractAuthorities(claims);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);

        // 将 uid 和 jti 存入 details，供 SecurityUtils 读取
        Map<String, Object> details = new HashMap<>();
        details.put("uid", claims.get("uid"));
        details.put("jti", claims.getId());
        auth.setDetails(details);

        SecurityContextHolder.getContext().setAuthentication(auth);
        chain.doFilter(request, response);
    }

    private Optional<String> resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(header);
        if (StringUtils.hasText(bearer) && bearer.startsWith(prefix)) {
            return Optional.of(bearer.substring(prefix.length()).trim());
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private Collection<SimpleGrantedAuthority> extractAuthorities(Claims claims) {
        Object roles = claims.get("roles");
        if (roles instanceof List<?> list) {
            return list.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }
        return List.of();
    }

    private void writeError(HttpServletResponse response, ResultCode code) throws IOException {
        writeError(response, code, code.getMessage());
    }

    private void writeError(HttpServletResponse response, ResultCode code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(R.fail(code, message)));
    }
}

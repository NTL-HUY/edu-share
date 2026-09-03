package com.nbh.edushare.common.utils.websocket;


import com.nbh.edushare.modules.auth.security.JwtService;
import com.nbh.edushare.modules.auth.security.TokenPayload;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Arrays;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UserHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtService jwtService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token = null;
            Cookie[] cookies = servletRequest.getServletRequest().getCookies();
            if (cookies != null) {
                token = Arrays.stream(cookies)
                        .filter(cookie -> "access_token".equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst()
                        .orElse(null);
            }
            if (token == null) return false;
            TokenPayload tokenPayload = jwtService.verifyAndParseAccessToken(token);
            attributes.put("tokenPayload",tokenPayload);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {}

    private Long extractUserId(String token) {
        try { return Long.parseLong(token); } catch (Exception e) { return null; }
    }
}
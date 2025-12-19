package com.example.leaftalk.global.security.handler;

import com.example.leaftalk.domain.auth.service.AuthService;
import com.example.leaftalk.global.security.jwt.JWTUtil;
import com.example.leaftalk.global.security.jwt.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@AllArgsConstructor
@Qualifier("loginSuccessHandler")
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        // 로그인 성공시 이메일과 역할 추출
        String email = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        // JWT 생성
        String accessToken = jwtUtil.createJWT(email, role, TokenType.Access);
        String refreshToken = jwtUtil.createJWT(email, role, TokenType.Refresh);

        authService.addRefresh(email, refreshToken);

        // 응답
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = String.format("{\"accessToken\":\"%s\", \"refreshToken\":\"%s\"}", accessToken, refreshToken);
        response.getWriter().write(json);
        response.getWriter().flush();
    }
}

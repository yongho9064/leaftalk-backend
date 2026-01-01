package com.example.leaftalk.global.security.handler;

import com.example.leaftalk.domain.auth.service.AuthService;
import com.example.leaftalk.global.security.enums.TokenType;
import com.example.leaftalk.global.security.util.CookieUtil;
import com.example.leaftalk.global.security.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Qualifier("loginSuccessHandler")
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${spring.cookie.refresh-max-age-sec}")
    private int cookieRefreshMaxAgeSec;

    private final JWTUtil jwtUtil;
    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        String email = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        String accessToken = jwtUtil.createJWT(email, role, TokenType.ACCESS);
        String refreshToken = jwtUtil.createJWT(email, role, TokenType.REFRESH);

        authService.addRefresh(request, email, refreshToken);

        // 쿠키 생성 후 응답에 추가
        Cookie refreshCookie = CookieUtil.createCookie("refreshToken", refreshToken, cookieRefreshMaxAgeSec);
        response.addCookie(refreshCookie);

        // 클라이언트에 액세스 토큰을 JSON 형식으로 응답
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = String.format("{\"accessToken\":\"%s\"}", accessToken);
        response.getWriter().write(json);
        response.getWriter().flush();

    }
}
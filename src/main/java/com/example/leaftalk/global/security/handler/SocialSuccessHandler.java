package com.example.leaftalk.global.security.handler;

import com.example.leaftalk.domain.auth.service.AuthService;
import com.example.leaftalk.global.security.enums.TokenType;
import com.example.leaftalk.global.security.util.CookieUtil;
import com.example.leaftalk.global.security.util.IpUtil;
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
@Qualifier("socialSuccessHandler")
public class SocialSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${spring.cookie.refresh-max-age-sec}")
    private int cookieRefreshMaxAgeSec;

    private final JWTUtil jwtUtil;
    private final AuthService authService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String username =  authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        String refreshToken = jwtUtil.createJWT(username, role, TokenType.REFRESH);

        String clientIp = IpUtil.getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        authService.addRefresh(username, refreshToken, clientIp, userAgent);

        Cookie refreshCookie = CookieUtil.createCookie("refreshToken", refreshToken, cookieRefreshMaxAgeSec);

        response.addCookie(refreshCookie);
        response.sendRedirect("http://localhost:5173/oauth/callback");
    }
}

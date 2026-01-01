package com.example.leaftalk.global.security.handler;

import com.example.leaftalk.domain.auth.dto.response.TokenResponse;
import com.example.leaftalk.domain.auth.repository.RefreshTokenRepository;
import com.example.leaftalk.domain.auth.service.AuthService;
import com.example.leaftalk.global.security.util.CookieUtil;
import com.example.leaftalk.global.security.util.IpUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public abstract class AbstractAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    protected final int cookieRefreshMaxAgeSec;
    protected final AuthService authService;
    protected final RefreshTokenRepository refreshTokenRepository;

    protected AbstractAuthenticationSuccessHandler(int cookieRefreshMaxAgeSec, AuthService authService, RefreshTokenRepository refreshTokenRepository) {
        this.cookieRefreshMaxAgeSec = cookieRefreshMaxAgeSec;
        this.authService = authService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public final void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String email = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String clientIp = IpUtil.getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        TokenResponse tokenDto = authService.generateTokes(email, role);
        refreshTokenRepository.saveRefreshMeta(email, tokenDto.refreshToken(), clientIp, userAgent);

        Cookie refreshCookie = CookieUtil.createCookie("refreshToken", tokenDto.refreshToken(), cookieRefreshMaxAgeSec);
        response.addCookie(refreshCookie);

        sendResponse(response, tokenDto);
    }

    protected abstract void sendResponse(HttpServletResponse response, TokenResponse tokenDto) throws IOException;

}

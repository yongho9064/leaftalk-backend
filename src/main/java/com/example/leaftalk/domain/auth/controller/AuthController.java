package com.example.leaftalk.domain.auth.controller;

import com.example.leaftalk.domain.auth.dto.response.TokenResponse;
import com.example.leaftalk.domain.auth.dto.response.AccessTokenResponse;
import com.example.leaftalk.domain.auth.service.AuthService;
import com.example.leaftalk.global.exception.CustomException;
import com.example.leaftalk.global.exception.ErrorCode;
import com.example.leaftalk.global.security.util.CookieUtil;
import com.example.leaftalk.global.security.util.IpUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jwt")
@RequiredArgsConstructor
public class AuthController {

    @Value("${spring.cookie.refresh-max-age-sec}")
    private int refreshCookieMaxAge;

    private final AuthService authService;

    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenResponse> jwtRefresh(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = CookieUtil.getCookie(request, "refreshToken")
                .orElseThrow(() -> new CustomException(ErrorCode.COOKIE_NOT_FOUND)).getValue();
        String clientIp = IpUtil.getClientIp(request);
        String userAgent = request.getHeader("User-Agent");

        TokenResponse tokenResponse = authService.reissueToken(refreshToken, clientIp, userAgent);

        Cookie newRefreshCookie = CookieUtil.createCookie("refreshToken", tokenResponse.refreshToken(), refreshCookieMaxAge);
        response.addCookie(newRefreshCookie);

        return ResponseEntity.ok(new AccessTokenResponse(tokenResponse.accessToken()));
    }

}

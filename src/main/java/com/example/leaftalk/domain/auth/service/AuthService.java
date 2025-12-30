package com.example.leaftalk.domain.auth.service;

import com.example.leaftalk.domain.auth.dto.response.JWTResponse;
import com.example.leaftalk.global.exception.CustomException;
import com.example.leaftalk.global.exception.ErrorCode;
import com.example.leaftalk.global.redis.RedisService;
import com.example.leaftalk.global.security.enums.TokenType;
import com.example.leaftalk.global.security.util.CookieUtil;
import com.example.leaftalk.global.security.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${spring.jwt.refresh-expiration}")
    private Long refreshExpiration;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final String REDIS_REFRESH_PREFIX = "refreshToken:";

    private final JWTUtil jwtUtil;
    private final RedisService redisService;

    @Transactional
    public JWTResponse reissueToken(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키에서 Refresh 토큰 추출
        String refreshToken = getValidRefreshTokenFromCookie(request);

        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        String savedToken = redisService.get(REDIS_REFRESH_PREFIX + email);

        if (savedToken == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 저장된 토큰과 비교 후 일치하지 않으면 로그아웃 -> 탈취된 토큰일 가능성
        if (!savedToken.equals(refreshToken)) {
            redisService.delete(REDIS_REFRESH_PREFIX + email);
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 토큰 생성
        String newAccessToken = jwtUtil.createJWT(email, role, TokenType.ACCESS);
        String newRefreshToken = jwtUtil.createJWT(email, role, TokenType.REFRESH);

        // 신규 Refresh 토큰 저장
        redisService.save(REDIS_REFRESH_PREFIX + email, newRefreshToken, refreshExpiration);

        // 신규 쿠키 추가
        Cookie newRefreshCookie = CookieUtil.createCookie(REFRESH_TOKEN_COOKIE_NAME, newRefreshToken, 7 * 24 * 60 * 60);
        response.addCookie(newRefreshCookie);

        return new JWTResponse(newAccessToken);
    }

    @Transactional
    public void addRefresh(String email, String refreshToken) {
        redisService.save(REDIS_REFRESH_PREFIX + email, refreshToken, refreshExpiration);
    }

    @Transactional
    public boolean removeRefreshToken(String refreshToken) {
        String email = jwtUtil.getEmail(refreshToken);
        return redisService.delete(REDIS_REFRESH_PREFIX + email);
    }

    @Transactional
    public boolean removeRefreshTokenByEmail(String key) {
        return redisService.delete(REDIS_REFRESH_PREFIX + key);
    }

    private String getValidRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie cookie = CookieUtil.getCookie(request, REFRESH_TOKEN_COOKIE_NAME)
                .orElseThrow(() -> new CustomException(ErrorCode.COOKIE_NOT_FOUND));

        String refreshToken = cookie.getValue();

        if (refreshToken == null || !jwtUtil.isValid(refreshToken, TokenType.REFRESH)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
        return refreshToken;
    }

}

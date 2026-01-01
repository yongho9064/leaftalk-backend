package com.example.leaftalk.domain.auth.service;

import com.example.leaftalk.domain.auth.dto.request.RefreshTokenMeta;
import com.example.leaftalk.domain.auth.dto.response.JWTResponse;
import com.example.leaftalk.global.exception.CustomException;
import com.example.leaftalk.global.exception.ErrorCode;
import com.example.leaftalk.domain.auth.repository.RefreshTokenRepository;
import com.example.leaftalk.global.security.enums.TokenType;
import com.example.leaftalk.global.security.util.IpUtil;
import com.example.leaftalk.global.security.util.CookieUtil;
import com.example.leaftalk.global.security.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${spring.jwt.refresh-expiration}")
    private Long refreshExpiration;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public JWTResponse reissueToken(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키에서 Refresh 토큰 추출
        String refreshToken = getValidRefreshTokenFromCookie(request);
        
        // IP 및 User-Agent 추출
        String currentIp = IpUtil.getClientIp(request);
        String currentUserAgent = request.getHeader("User-Agent");

        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        RefreshTokenMeta savedMeta = refreshTokenRepository.getRefreshTokenMetaByEmail(email);

        if (savedMeta == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 저장된 IP와 현재 요청의 IP가 다르면 로그아웃 -> 탈취된 토큰일 가능성
        if (!savedMeta.getIp().equals(currentIp)) {
            refreshTokenRepository.deleteRefreshTokenByEmail(email);
            log.warn("IP 주소와 매칭이 되지 않음! User: {}, Saved: {}, Current: {}", email, savedMeta.getIp(), currentIp);
            throw new CustomException(ErrorCode.SECURITY_RISK_DETECTED);
        }

        // 저장된 토큰과 비교 후 일치하지 않으면 로그아웃 -> 탈취된 토큰일 가능성
        if (!savedMeta.getRefreshToken().equals(refreshToken)) {
            refreshTokenRepository.deleteRefreshTokenByEmail(email);
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 토큰 생성
        String newAccessToken = jwtUtil.createJWT(email, role, TokenType.ACCESS);
        String newRefreshToken = jwtUtil.createJWT(email, role, TokenType.REFRESH);

        // 신규 Refresh 토큰 저장
        refreshTokenRepository.saveRefreshToken(email, newRefreshToken, currentIp, currentUserAgent, refreshExpiration);

        // 신규 쿠키 추가
        Cookie newRefreshCookie = CookieUtil.createCookie(REFRESH_TOKEN_COOKIE_NAME, newRefreshToken, 7 * 24 * 60 * 60);
        response.addCookie(newRefreshCookie);

        return new JWTResponse(newAccessToken);
    }

    @Transactional
    public void addRefresh(HttpServletRequest request, String email, String refreshToken) {

        String currentIp = IpUtil.getClientIp(request);
        String currentUserAgent = request.getHeader("User-Agent");

        refreshTokenRepository.saveRefreshToken(email, refreshToken, currentIp, currentUserAgent, refreshExpiration);
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

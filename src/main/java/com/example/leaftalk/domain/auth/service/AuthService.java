package com.example.leaftalk.domain.auth.service;

import com.example.leaftalk.domain.auth.dto.request.RefreshRequest;
import com.example.leaftalk.domain.auth.dto.response.JWTResponse;
import com.example.leaftalk.domain.auth.entity.Refresh;
import com.example.leaftalk.domain.auth.repository.RefreshRepository;
import com.example.leaftalk.global.exception.CustomException;
import com.example.leaftalk.global.exception.ErrorCode;
import com.example.leaftalk.global.security.enums.TokenType;
import com.example.leaftalk.global.security.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshRepository refreshRepository;
    private final JWTUtil jwtUtil;

    // 소셜 로그인 성공 후 쿠키(Refresh) -> 헤더 방식으로 응답
    @Transactional
    public JWTResponse cookie2Header(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키 리스트
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new CustomException(ErrorCode.COOKIE_NOT_FOUND);
        }

        // Refresh 토큰 획득
        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if ("refreshToken".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
                break;
            }
        }

        if (refreshToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // Refresh 토큰 검증
        boolean isValid = jwtUtil.isValid(refreshToken, TokenType.REFRESH);
        if (!isValid) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 정보 추출
        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 토큰 생성
        String newAccessToken = jwtUtil.createJWT(email, role, TokenType.ACCESS);
        String newRefreshToken = jwtUtil.createJWT(email, role, TokenType.REFRESH);

        // 기존 Refresh 토큰 DB 삭제 후 신규 추가
        Refresh newRefreshEntity = Refresh.builder()
                .email(email)
                .refreshToken(newRefreshToken)
                .build();

        refreshRepository.deleteByRefreshToken(refreshToken);
        refreshRepository.flush();                              // 즉시 삭제 반영 -> 쓰기 지연이라 insert가 먼저 될 수 있음
        refreshRepository.save(newRefreshEntity);

        // 기존 쿠키 제거
        expireCookie(response);

        return new JWTResponse(newAccessToken, newRefreshToken);
    }

    // Refresh 토큰으로 Access 토큰 재발급 로직
    @Transactional
    public JWTResponse refreshRotate(RefreshRequest request) {

        String refreshToken = request.getRefreshToken();

        // Refresh 토큰 검증
        boolean isValid = jwtUtil.isValid(refreshToken, TokenType.REFRESH);
        if (!isValid) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // RefreshToken 존재 확인
        if (!refreshRepository.existsByRefreshToken(refreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // 정보 추출
        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 토큰 생성
        String newAccessToken = jwtUtil.createJWT(email, role, TokenType.REFRESH);
        String newRefreshToken = jwtUtil.createJWT(email, role, TokenType.REFRESH);

        // 기존 Refresh 토큰 DB 삭제 후 신규 추가
        Refresh newRefreshEntity = Refresh.builder()
                .email(email)
                .refreshToken(newRefreshToken)
                .build();

        refreshRepository.deleteByRefreshToken(refreshToken);
        refreshRepository.save(newRefreshEntity);

        return new JWTResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void addRefresh(String email, String refreshToken) {
        Refresh refresh = Refresh.builder()
                .email(email)
                .refreshToken(refreshToken)
                .build();

        refreshRepository.save(refresh);
    }

    @Transactional(readOnly = true)
    public boolean existsRefreshToken(String refreshToken) {
        return refreshRepository.existsByRefreshToken(refreshToken);
    }

    @Transactional
    public Long removeRefreshToken(String refreshToken) {
        return refreshRepository.deleteByRefreshToken(refreshToken);
    }

    @Transactional
    public void removeRefreshEmail(String email) {
        refreshRepository.deleteByEmail(email);
    }

    private void expireCookie(HttpServletResponse response) {
        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(10);
        response.addCookie(refreshCookie);
    }

}

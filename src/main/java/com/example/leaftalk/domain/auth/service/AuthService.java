package com.example.leaftalk.domain.auth.service;

import com.example.leaftalk.domain.auth.dto.response.JWTResponse;
import com.example.leaftalk.domain.auth.entity.Refresh;
import com.example.leaftalk.domain.auth.repository.RefreshRepository;
import com.example.leaftalk.global.exception.CustomException;
import com.example.leaftalk.global.exception.ErrorCode;
import com.example.leaftalk.global.security.enums.TokenType;
import com.example.leaftalk.global.security.util.CookieUtil;
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

    @Transactional
    public JWTResponse rotateToken(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키에서 Refresh 토큰 추출
        Cookie cookie = CookieUtil.getCookie(request,"refreshToken")
                .orElseThrow(() -> new CustomException(ErrorCode.COOKIE_NOT_FOUND));

        String refreshToken = cookie.getValue();

        if (refreshToken == null) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // Refresh 토큰 검증
        if (!jwtUtil.isValid(refreshToken, TokenType.REFRESH)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // 토큰 생성
        String newAccessToken = jwtUtil.createJWT(email, role, TokenType.ACCESS);
        String newRefreshToken = jwtUtil.createJWT(email, role, TokenType.REFRESH);

        // 기존 Refresh 토큰 DB 삭제
        refreshRepository.deleteByRefreshToken(refreshToken);
        refreshRepository.flush();                              // 즉시 삭제 반영 -> 쓰기 지연이라 insert가 먼저 될 수 있음

        // 신규 Refresh 토큰 DB 저장
        Refresh newRefreshEntity = Refresh.builder()
                .email(email)
                .refreshToken(newRefreshToken)
                .build();
        refreshRepository.save(newRefreshEntity);

        // 신규 쿠키 추가
        Cookie newRefreshCookie = CookieUtil.createCookie("refreshToken", newRefreshToken, 7 * 24 * 60 * 60);
        response.addCookie(newRefreshCookie);

        return new JWTResponse(newAccessToken);
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

}

package com.example.leaftalk.domain.auth.service;

import com.example.leaftalk.domain.auth.dto.request.RefreshMetaRequest;
import com.example.leaftalk.domain.auth.dto.response.TokenResponse;
import com.example.leaftalk.domain.auth.repository.RefreshTokenRepository;
import com.example.leaftalk.global.exception.CustomException;
import com.example.leaftalk.global.exception.ErrorCode;
import com.example.leaftalk.global.security.enums.TokenType;
import com.example.leaftalk.global.security.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenResponse reissueToken(String refreshToken, String clientIp, String userAgent) {

        // Refresh 토큰 유효성 검사
        if (!jwtUtil.isValid(refreshToken, TokenType.REFRESH)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String email = jwtUtil.getEmail(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        RefreshMetaRequest savedMeta = refreshTokenRepository.getRefreshTokenMetaByEmail(email);

        if (savedMeta == null) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 저장된 IP와 현재 요청의 IP가 다르면 로그아웃 -> 탈취된 토큰일 가능성
        if (!savedMeta.getIp().equals(clientIp)) {
            refreshTokenRepository.deleteRefreshTokenByEmail(email);
            log.warn("IP 주소와 매칭이 되지 않음! User: {}, Saved: {}, Current: {}", email, savedMeta.getIp(), clientIp);
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
        refreshTokenRepository.saveRefreshToken(email, newRefreshToken, clientIp, userAgent);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public TokenResponse generateTokes(String email, String role, String clientIp, String userAgent) {

        String accessToken = jwtUtil.createJWT(email, role, TokenType.ACCESS);
        String refreshToken = jwtUtil.createJWT(email, role, TokenType.REFRESH);

        refreshTokenRepository.saveRefreshToken(email, refreshToken, clientIp, userAgent);

        return new TokenResponse(accessToken, refreshToken);
    }

}

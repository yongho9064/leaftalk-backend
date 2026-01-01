package com.example.leaftalk.domain.auth.repository;

import com.example.leaftalk.domain.auth.dto.request.RefreshMetaRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    @Value("${spring.jwt.refresh-expiration}")
    private long refreshExpiration;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String RT_PREFIX = "RT:";

    public void saveRefreshToken(String email, String refreshToken, String ip, String userAgent) {

        RefreshMetaRequest meta = RefreshMetaRequest.builder()
                .refreshToken(refreshToken)
                .ip(ip)
                .userAgent(userAgent)
                .build();

        String key = RT_PREFIX + email;
        redisTemplate.opsForValue().set(key, meta, refreshExpiration, TimeUnit.MILLISECONDS);
    }

    public RefreshMetaRequest getRefreshTokenMetaByEmail(String email) {

        String key = RT_PREFIX + email;
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        return objectMapper.convertValue(value, RefreshMetaRequest.class);
    }

    public boolean deleteRefreshTokenByEmail(String email) {
        String key = RT_PREFIX + email;
        return redisTemplate.delete(key);
    }

}
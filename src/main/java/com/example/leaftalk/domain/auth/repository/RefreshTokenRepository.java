package com.example.leaftalk.domain.auth.repository;

import com.example.leaftalk.domain.auth.dto.request.RefreshTokenMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String RT_PREFIX = "RT:";

    public void saveRefreshToken(String email, String refreshToken, String ip, String userAgent, long timeout) {

        RefreshTokenMeta meta = RefreshTokenMeta.builder()
                .refreshToken(refreshToken)
                .ip(ip)
                .userAgent(userAgent)
                .build();

        String key = RT_PREFIX + email;
        redisTemplate.opsForValue().set(key, meta, timeout, TimeUnit.MILLISECONDS);
    }

    public RefreshTokenMeta getRefreshTokenMetaByEmail(String email) {

        String key = RT_PREFIX + email;
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        return objectMapper.convertValue(value, RefreshTokenMeta.class);
    }

    public boolean deleteRefreshTokenByEmail(String email) {
        String key = RT_PREFIX + email;
        return redisTemplate.delete(key);
    }

}
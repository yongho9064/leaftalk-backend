package com.example.leaftalk.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private final SecretKey key;
    private final long accessTokenExpireTime;
    private final long refreshTokenExpireTime;

    public JWTUtil(
            @Value("${spring.jwt.secret}") String key,
            @Value("${spring.jwt.access-expiration}") long accessTokenExpireTime,
            @Value("${spring.jwt.refresh-expiration}") long refreshTokenExpireTime) {

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        this.key = new SecretKeySpec(keyBytes, Jwts.SIG.HS256.key().build().getAlgorithm());

        this.accessTokenExpireTime = accessTokenExpireTime;
        this.refreshTokenExpireTime = refreshTokenExpireTime;
    }

    public String getEmail(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("sub", String.class);
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public String createJWT(String email, String role, boolean isAccess) {

        long now = System.currentTimeMillis();
        long expireTime = isAccess ?  accessTokenExpireTime : refreshTokenExpireTime;
        String type = isAccess ? "access" : "refresh";

        return Jwts.builder()
                .claim("sub", email)
                .claim("role", role)
                .claim("type", type)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expireTime))
                .signWith(key)
                .compact();
    }

    // JWT 유효 여부 (위조, 시간, Acc/Ref 여부)
    public boolean isValid(String token, boolean isAccess) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String type = claims.get("type", String.class);

            return isAccess ? "access".equals(type) : "refresh".equals(type);

        } catch (JwtException | IllegalArgumentException _) {
            return false;
        }

    }

}

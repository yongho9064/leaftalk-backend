package com.example.leaftalk.global.security.handler;

import com.example.leaftalk.domain.auth.repository.RefreshTokenRepository;
import com.example.leaftalk.global.security.enums.TokenType;
import com.example.leaftalk.global.security.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.StringUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final RefreshTokenRepository refreshTokenRepository;

    private final JWTUtil jwtUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, @Nullable Authentication authentication) {
        try {

            String body = new BufferedReader(new InputStreamReader(request.getInputStream()))
                    .lines().reduce("", String::concat);

            if (!StringUtils.hasText(body)){
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Request body is missing");
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(body);
            String refreshToken = jsonNode.has("refreshToken") ? jsonNode.get("refreshToken").asString() : null;

            if (refreshToken == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Refresh Token is missing");
                return;
            }
            boolean isValid = jwtUtil.isValid(refreshToken, TokenType.REFRESH);
            if (!isValid) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or Expired Refresh Token");
                return;
            }

            String email = jwtUtil.getEmail(refreshToken);
            boolean isDeleted = refreshTokenRepository.deleteRefreshTokenByEmail(email);

            if (isDeleted) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete Refresh Token");
            }

        } catch (IOException e) {
            throw new AuthenticationServiceException("Failed to read refresh token", e);
        }
    }

}

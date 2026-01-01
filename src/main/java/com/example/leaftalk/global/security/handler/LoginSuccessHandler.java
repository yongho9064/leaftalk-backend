package com.example.leaftalk.global.security.handler;

import com.example.leaftalk.domain.auth.dto.response.TokenResponse;
import com.example.leaftalk.domain.auth.repository.RefreshTokenRepository;
import com.example.leaftalk.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

@Component
@Qualifier("loginSuccessHandler")
public class LoginSuccessHandler extends AbstractAuthenticationSuccessHandler{

    private final ObjectMapper objectMapper = new ObjectMapper();

    public LoginSuccessHandler(
            @Value("${spring.cookie.refresh-max-age-sec}") int cookieRefreshMaxAgeSec,
            AuthService authService,
            RefreshTokenRepository refreshTokenRepository) {

        super(cookieRefreshMaxAgeSec, authService, refreshTokenRepository);
    }

    @Override
    protected void sendResponse(HttpServletResponse response, TokenResponse tokenDto) throws IOException {

        // 클라이언트에 액세스 토큰을 JSON 형식으로 응답
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = objectMapper.writeValueAsString(Map.of("accessToken", tokenDto.accessToken()));
        response.getWriter().write(jsonResponse);

    }
}
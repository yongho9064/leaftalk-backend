package com.example.leaftalk.global.security.handler;

import com.example.leaftalk.domain.auth.dto.response.TokenResponse;
import com.example.leaftalk.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Qualifier("socialSuccessHandler")
public class SocialSuccessHandler extends AbstractAuthenticationSuccessHandler {

    public SocialSuccessHandler(
            @Value("${spring.cookie.refresh-max-age-sec}") int cookieRefreshMaxAgeSec,
            AuthService authService) {

        super(cookieRefreshMaxAgeSec, authService);
    }

    @Override
    protected void sendResponse(HttpServletResponse response, TokenResponse tokenDto) throws IOException {
        response.sendRedirect("http://localhost:5173/oauth/callback");
    }

}

package com.example.leaftalk.domain.auth.controller;

import com.example.leaftalk.domain.auth.dto.request.RefreshRequest;
import com.example.leaftalk.domain.auth.dto.response.JWTResponse;
import com.example.leaftalk.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jwt")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/exchange")
    public JWTResponse jwtExchange(HttpServletRequest request, HttpServletResponse response) {
        return authService.cookie2Header(request, response);
    }

    @PostMapping("/refresh")
    public JWTResponse jwtRefresh(@RequestBody @Valid RefreshRequest request) {
        return authService.refreshRotate(request);
    }
}

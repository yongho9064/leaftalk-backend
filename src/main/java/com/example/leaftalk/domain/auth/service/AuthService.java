package com.example.leaftalk.domain.auth.service;

import com.example.leaftalk.domain.auth.entity.Refresh;
import com.example.leaftalk.domain.auth.repository.RefreshRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AuthService {

    private final RefreshRepository refreshRepository;

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
    public void removeRefreshToken(String refreshToken) {
        refreshRepository.deleteByRefreshToken(refreshToken);
    }

    @Transactional
    public void removeRefEmail(String email) {
        refreshRepository.deleteByEmail(email);
    }

}

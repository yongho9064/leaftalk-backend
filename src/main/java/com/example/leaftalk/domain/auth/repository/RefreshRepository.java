package com.example.leaftalk.domain.auth.repository;

import com.example.leaftalk.domain.auth.entity.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {

    boolean existsByRefreshToken(String refreshToken);

    Long deleteByRefreshToken(String refreshToken);

    void deleteByEmail(String email);

    void deleteByCreatedAtBefore(LocalDateTime createdAtBefore);
}

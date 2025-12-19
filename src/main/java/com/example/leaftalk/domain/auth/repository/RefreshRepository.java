package com.example.leaftalk.domain.auth.repository;

import com.example.leaftalk.domain.auth.entity.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {

    boolean existsByRefreshToken(String refreshToken);

    void deleteByRefreshToken(String refreshToken);

    void deleteByEmail(String email);
}

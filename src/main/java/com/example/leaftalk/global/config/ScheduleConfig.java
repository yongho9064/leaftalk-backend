package com.example.leaftalk.global.config;

import com.example.leaftalk.domain.auth.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ScheduleConfig {

    private final RefreshRepository refreshRepository;

    // 매일 새벽 3시에 8일 지난 Refresh 토큰 삭제
    @Scheduled(cron = "0 0 3 * * *")
    public void refreshTtlSchedule() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(8);
        refreshRepository.deleteByCreatedAtBefore(cutoff);
    }
}

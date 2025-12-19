package com.example.leaftalk.domain.auth.entity;

import com.example.leaftalk.global.entity.BaseCreateTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Refresh extends BaseCreateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length=512)
    private String refreshToken;

    @Builder
    public Refresh(String email, String refreshToken) {
        this.email = email;
        this.refreshToken = refreshToken;
    }

}

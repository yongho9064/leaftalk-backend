package com.example.leaftalk.domain.member.entity;

import com.example.leaftalk.global.entity.BaseCreateTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseCreateTimeEntity {

    @Id @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private boolean marketingAgree;

    @Column(nullable = false)
    private boolean isLock;

    @Column(nullable = false)
    private boolean isSocial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialProvider socialProvider;

    @Builder
    public Member(String email, String password, String nickname,  Role role, boolean marketingAgree, boolean isLock, boolean isSocial, SocialProvider socialProvider) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.marketingAgree = marketingAgree;
        this.role = role;
        this.isLock = isLock;
        this.isSocial = isSocial;
        this.socialProvider = socialProvider;
    }

    public void updateMember(String nickname) {
        this.nickname = nickname;
    }

}

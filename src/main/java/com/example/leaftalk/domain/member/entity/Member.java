package com.example.leaftalk.domain.member.entity;

import com.example.leaftalk.domain.member.dto.request.MemberRequestDto;
import com.example.leaftalk.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private boolean isLock;

    @Column(nullable = false)
    private boolean isSocial;

    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialProvider socialProvider;

    @Builder
    public Member(String email, String password, String nickname,  Role role, boolean isLock, boolean isSocial) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
        this.isLock = isLock;
        this.isSocial = isSocial;
    }

    public void updateMember(MemberRequestDto.Update request) {
        this.nickname = request.getNickname();
        this.password = request.getPassword();
    }

}

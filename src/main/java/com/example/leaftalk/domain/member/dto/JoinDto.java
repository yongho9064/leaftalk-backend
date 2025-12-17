package com.example.leaftalk.domain.member.dto;

import com.example.leaftalk.domain.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinDto {

    private String email;

    private String password;

    private String nickname;

    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .email(this.email)
                .password(encodedPassword)
                .nickname(this.nickname)
                .role("ROLE_USER")
                .build();
    }
}

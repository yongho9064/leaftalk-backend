package com.example.leaftalk.domain.member.dto.request;

import com.example.leaftalk.domain.member.entity.Member;
import com.example.leaftalk.domain.member.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSignupRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    @NotBlank
    private String nickname;

    public Member toEntity(String encodedPassword) {
        return Member.builder()
                     .email(this.email)
                     .password(encodedPassword)
                     .nickname(this.nickname)
                     .role(Role.USER)
                     .isSocial(false)
                     .isLock(false)
                     .build();
    }

}

package com.example.leaftalk.domain.member.dto.request;

import com.example.leaftalk.domain.member.entity.Member;
import com.example.leaftalk.domain.member.entity.Role;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSignupRequest {

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 60, message = "이메일은 60자 이내여야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
    private String passwordConfirm;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    @Pattern(
            regexp = "^[a-zA-Z0-9가-힣]+$",
            message = "닉네임은 특수문자나 공백을 포함할 수 없습니다."
    )
    private String nickname;

    @AssertTrue(message = "이용 약관에 동의해야 합니다.")
    private boolean agreeToTerms;

    @AssertTrue(message = "개인정보 처리방침에 동의해야 합니다.")
    private boolean agreeToPrivacyPolicy;

    private boolean agreeToMarketing;

    @AssertTrue(message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.")
    public boolean isPasswordCheck() {
        if (password == null || passwordConfirm == null) {
            return false;
        }
        return password.equals(passwordConfirm);
    }

    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .email(this.email)
                .password(encodedPassword)
                .nickname(this.nickname)
                .marketingAgree(this.agreeToMarketing)
                .role(Role.USER)
                .isSocial(false)
                .isLock(false)
                .build();
    }

}

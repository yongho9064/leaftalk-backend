package com.example.leaftalk.domain.member.service;

import com.example.leaftalk.domain.auth.repository.RefreshTokenRepository;
import com.example.leaftalk.domain.member.dto.request.MemberSignupRequest;
import com.example.leaftalk.domain.member.entity.Member;
import com.example.leaftalk.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static com.example.leaftalk.domain.member.entity.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private Member createMember(Long id, String email) {
        Member member = Member.builder()
                .email(email)
                .password("Password123!")
                .nickname("TestUser")
                .marketingAgree(false)
                .role(USER)
                .isLock(false)
                .isSocial(false)
                .build();

        ReflectionTestUtils.setField(member, "id", id);

        return member;
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void registerMember_success() {
        // given
        MemberSignupRequest request = MemberSignupRequest.builder()
                .email("test@naver.com")
                .password("Password123!")
                .passwordConfirm("Password123!")
                .nickname("TestUser")
                .agreeToTerms(true)
                .agreeToPrivacyPolicy(true)
                .agreeToMarketing(false)
                .build();

        given(memberRepository.existsByEmail(request.getEmail())).willReturn(false);
        given(bCryptPasswordEncoder.encode(request.getPassword())).willReturn("encodedPassword");

        Member saveMember = Member.builder()
                .email(request.getEmail())
                .password("encodedPassword")
                .nickname(request.getNickname())
                .marketingAgree(request.isAgreeToMarketing())
                .role(USER)
                .isLock(false)
                .isSocial(false)
                .build();

        ReflectionTestUtils.setField(saveMember, "id", 1L);

        given(memberRepository.save(any(Member.class))).willReturn(saveMember);

        //when
        Long resultId = memberService.registerMember(request);

        // then
        assertThat(resultId).isEqualTo(1L);

        verify(memberRepository, times(1)).existsByEmail(request.getEmail());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 조회")
    void getMemberById_success() {

        // given
        Long memberId = 1L;
        Member member = createMember(1L, "test@naver.com");

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // wher
        Member findMember = memberRepository.findById(memberId).get();

        // then
        assertThat(findMember.getId()).isEqualTo(memberId);
        assertThat(findMember.getEmail()).isEqualTo("test@naver.com");
    }
}
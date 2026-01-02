package com.example.leaftalk.domain.member.repository;

import com.example.leaftalk.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.util.ReflectionTestUtils;

import static com.example.leaftalk.domain.member.entity.Role.USER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

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
    @DisplayName("멤버 저장 테스트")
    void saveMember() {
        // given
        Member member = createMember(1L, "test@naver.com");

        // when
        Member savedMember = memberRepository.save(member);

        // then
        assertThat(savedMember).isNotNull();
    }

}
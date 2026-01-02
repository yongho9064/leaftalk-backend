package com.example.leaftalk.domain.member.integration;

import com.example.leaftalk.domain.member.dto.request.MemberSignupRequest;
import com.example.leaftalk.domain.member.entity.Member;
import com.example.leaftalk.domain.member.repository.MemberRepository;
import com.example.leaftalk.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    private Member createMember(Long id, String email) {
        Member member = Member.builder()
                .email(email)
                .password("Password123!")
                .nickname("TestUser")
                .marketingAgree(false)
                .build();

        ReflectionTestUtils.setField(member, "id", id);

        return member;
    }

    @Test
    @DisplayName("회원 가입 통합 테스트")
    void integrationTest() throws Exception {

        // given
        MemberSignupRequest request = MemberSignupRequest.builder()
                .email("test@naver.com")
                .password("Password123!")
                .passwordConfirm("Password123!")
                .nickname("TestUser")
                .agreeToMarketing(true)
                .agreeToPrivacyPolicy(true)
                .agreeToTerms(true)
                .build();

        // when & then
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

}

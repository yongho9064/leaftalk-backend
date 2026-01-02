package com.example.leaftalk.domain.member.controller;

import com.example.leaftalk.domain.member.dto.request.MemberSignupRequest;
import com.example.leaftalk.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = MemberController.class,
    excludeAutoConfiguration = {
        OAuth2ClientAutoConfiguration.class,
    }
)
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    MemberService memberService;

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() throws Exception {

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

        given(memberService.registerMember(any(MemberSignupRequest.class))).willReturn(1L);

        // when & then
        mockMvc.perform(post("/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
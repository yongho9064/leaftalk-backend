package com.example.leaftalk.global.security.service;

import com.example.leaftalk.domain.member.entity.Member;
import com.example.leaftalk.domain.member.entity.Role;
import com.example.leaftalk.domain.member.entity.SocialProvider;
import com.example.leaftalk.domain.member.repository.MemberRepository;
import com.example.leaftalk.global.security.details.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes;

        String email;
        String nickname;

        // provider 제공자별 데이터 획득
        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();

        if (registrationId.equals(SocialProvider.NAVER.name())) {

            attributes = (Map<String, Object>) oAuth2User.getAttributes().get("response");
            email = attributes.get("email").toString();
            nickname = attributes.get("nickname").toString();

        } else if (registrationId.equals(SocialProvider.GOOGLE.name())) {

            attributes = oAuth2User.getAttributes();
            email = attributes.get("email").toString();
            nickname = attributes.get("name").toString();

        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");
        }

        // 데이터베이스 조회 -> 존재하면 업데이트, 없으면 신규 가입
        Member member = memberRepository.findByEmailAndIsSocial(email, true).orElseThrow(
                () -> new OAuth2AuthenticationException("소셜 회원 정보 조회 중 오류가 발생했습니다."));

        if (member != null) {
            // 기존 유저 업데이트 -> 닉네임 변경 되었거나 프로필 사진이 변경 되었을 경우 다시 저장
            /*MemberRequestDto.Update dto = MemberRequestDto.Update
                    .builder()
                    .email(email)
                    .nickname(nickname)
                    .build();*/

            member.updateMember(nickname);

            memberRepository.save(member);
        } else {
            Member newMember = Member.builder()
                                     .email(email)
                                     .password("")
                                     .isLock(false)
                                     .isSocial(true)
                                     .socialProvider(SocialProvider.valueOf(registrationId))
                                     .role(Role.USER)
                                     .nickname(nickname)
                                     .email(email)
                                     .build();

            memberRepository.save(newMember);
        }

        return new MemberDetails(member, attributes);

    }
}

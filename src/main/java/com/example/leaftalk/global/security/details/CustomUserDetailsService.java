package com.example.leaftalk.global.security.details;

import com.example.leaftalk.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return memberRepository.findMemberByEmail(email)
                .map(member -> CustomUserDetails.builder()
                        .email(member.getEmail())
                        .password(member.getPassword())
                        .role(member.getRole())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일로 가입된 사용자가 없습니다. email: " + email));
    }

}

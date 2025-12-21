package com.example.leaftalk.global.security.service;

import com.example.leaftalk.domain.member.entity.Member;
import com.example.leaftalk.domain.member.repository.MemberRepository;
import com.example.leaftalk.global.security.details.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmailAndIsLockAndIsSocial(email, false, false)
                                        .orElseThrow(() -> new UsernameNotFoundException(email));

        return new MemberDetails(member);
    }
}

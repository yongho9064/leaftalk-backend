package com.example.leaftalk.domain.member.service;

import com.example.leaftalk.domain.member.dto.JoinDto;
import com.example.leaftalk.domain.member.entity.Member;
import com.example.leaftalk.domain.member.exception.DuplicateEmailException;
import com.example.leaftalk.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void createMember(JoinDto joinDto) {

        if (memberRepository.existsByEmail(joinDto.getEmail())) {
            throw new DuplicateEmailException("이미 존재하는 이메일입니다. ", joinDto.getEmail());
        }

        Member member = joinDto.toEntity(bCryptPasswordEncoder.encode(joinDto.getPassword()));

        memberRepository.save(member);
    }
}

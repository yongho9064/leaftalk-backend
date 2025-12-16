package com.example.leaftalk.domain.member.service;

import com.example.leaftalk.domain.member.dto.JoinDTO;
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

    public void createMember(JoinDTO joinDTO) {

        if (memberRepository.existsByEmail(joinDTO.getEmail())) {
            throw new DuplicateEmailException("이미 존재하는 이메일입니다. ", joinDTO.getEmail());
        }

        String encodedPassword = bCryptPasswordEncoder.encode(joinDTO.getPassword());
        Member member = joinDTO.toEntity(encodedPassword);

        memberRepository.save(member);
    }
}

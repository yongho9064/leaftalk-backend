package com.example.leaftalk.domain.member.service;

import com.example.leaftalk.domain.member.dto.request.MemberRequestDto;
import com.example.leaftalk.domain.member.entity.Member;
import com.example.leaftalk.domain.member.exception.DuplicateEmailException;
import com.example.leaftalk.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Transactional
    public void createMember(MemberRequestDto.Join joinDto) {

        if (memberRepository.existsByEmail(joinDto.getEmail())) {
            throw new DuplicateEmailException("이미 존재하는 이메일입니다. ", joinDto.getEmail());
        }

        Member member = joinDto.toEntity(bCryptPasswordEncoder.encode(joinDto.getPassword()));

        memberRepository.save(member);
    }

    @Transactional
    public void updateMember(MemberRequestDto.Update request) throws AccessDeniedException {

        // 인증 정보 가져오기
        String sessionEmail = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                                      .map(Authentication::getName)
                                      .orElseThrow(() -> new AccessDeniedException("인증 정보가 없습니다. 다시 로그인해주세요."));

        // 본인만 수정 가능 검증
        if (!sessionEmail.equals(request.getEmail())) {
            throw new AccessDeniedException("본인만 수정할 수 있습니다.");
        }

        Member member = memberRepository.findByEmailAndIsLockAndIsSocial(request.getEmail(), false, false)
                .orElseThrow(() -> new UsernameNotFoundException(request.getEmail()));

        member.updateMember(request);

        memberRepository.save(member);  // 더티 체크로 인한 저장이 되지만 혼란을 막기 위해 명시적으로 저장
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmailAndIsLockAndIsSocial(email, false, false)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().name())
                .accountLocked(member.isLock())
                .build();
    }

}
package com.example.leaftalk.domain.member.service;

import com.example.leaftalk.domain.auth.service.AuthService;
import com.example.leaftalk.domain.member.dto.request.MemberSignupRequest;
import com.example.leaftalk.domain.member.dto.request.MemberUpdateRequest;
import com.example.leaftalk.domain.member.dto.response.MemberResponse;
import com.example.leaftalk.domain.member.entity.Member;
import com.example.leaftalk.domain.member.repository.MemberRepository;
import com.example.leaftalk.global.exception.CustomException;
import com.example.leaftalk.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AuthService authService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public Long registerMember(MemberSignupRequest request) {

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        Member member = request.toEntity(bCryptPasswordEncoder.encode(request.getPassword()));
        Member saveMember = memberRepository.save(member);

        return saveMember.getId();
    }

    @Transactional(readOnly = true)
    public MemberResponse getMember(String email) {
        Member member = memberRepository.findByEmailAndIsLock(email, false)
                                        .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다. " + email));
        return new MemberResponse(email, member.isSocial(), member.getNickname(), member.getEmail());
    }

    @Transactional
    public void updateMember(MemberUpdateRequest request, String email) {

        Member member = memberRepository.findByEmailAndIsLockAndIsSocial(email, false, false)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        member.updateMember(request.getNickname());

        memberRepository.save(member);  // 더티 체크로 인한 저장이 되지만 혼란을 막기 위해 명시적으로 저장
    }

    @Transactional
    public void deleteMember(String email) {
        memberRepository.deleteByEmail(email);
        authService.removeRefreshEmail(email);
    }

    @Transactional(readOnly = true)      // 따로 분리 할까 생각중
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

}
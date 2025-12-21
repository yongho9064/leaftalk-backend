package com.example.leaftalk.domain.member.controller;

import com.example.leaftalk.domain.member.dto.request.EmailCheckRequest;
import com.example.leaftalk.domain.member.dto.request.MemberSignupRequest;
import com.example.leaftalk.domain.member.dto.request.MemberUpdateRequest;
import com.example.leaftalk.domain.member.dto.response.MemberResponse;
import com.example.leaftalk.domain.member.service.MemberService;
import com.example.leaftalk.global.security.annotation.LoginMember;
import com.example.leaftalk.global.security.dto.AuthMember;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Long> createMember(@RequestBody @Valid MemberSignupRequest request) {
        Long memberId = memberService.registerMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberId);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMember(@LoginMember AuthMember authMember) {
        return ResponseEntity.ok(memberService.getMember(authMember.email()));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/me")
    public ResponseEntity<Boolean> updateMember(@RequestBody @Valid MemberUpdateRequest request, @LoginMember AuthMember authMember) {
        memberService.updateMember(request, authMember.email());
        return ResponseEntity.ok(true);
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMember(@LoginMember AuthMember authMember){
        memberService.deleteMember(authMember.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/exist")
    public ResponseEntity<Boolean> existEmail(@RequestBody @Valid EmailCheckRequest request) {
        return ResponseEntity.ok(memberService.existsByEmail(request.getEmail()));
    }

}
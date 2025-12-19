package com.example.leaftalk.domain.member.controller;

import com.example.leaftalk.domain.member.dto.request.MemberRequestDto;
import com.example.leaftalk.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody @Valid MemberRequestDto.Join joinDto) {

        memberService.createMember(joinDto);

        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/exist")
    public ResponseEntity<Boolean> existEmail(@RequestBody @Valid MemberRequestDto.Exist existDto) {
        return ResponseEntity.ok(memberService.existsByEmail(existDto.getEmail()));
    }

}

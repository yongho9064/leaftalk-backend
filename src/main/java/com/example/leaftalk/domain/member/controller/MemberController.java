package com.example.leaftalk.domain.member.controller;

import com.example.leaftalk.domain.member.dto.JoinDto;
import com.example.leaftalk.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody JoinDto joinDTO) {

        memberService.createMember(joinDTO);

        return ResponseEntity.ok("회원가입 성공");
    }

}

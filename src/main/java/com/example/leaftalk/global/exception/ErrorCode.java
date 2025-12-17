package com.example.leaftalk.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // == 회원(Member) 에러 ==
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER", "이미 존재하는 이메일입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER", "회원을 찾을 수 없습니다."),

    // == 공통(Global) 에러 ==
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GLOBAL", "서버 내부 에러입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "GLOBAL", "잘못된 입력값입니다.");

    private final HttpStatus httpStatus;
    private final String type;
    private final String message;
}
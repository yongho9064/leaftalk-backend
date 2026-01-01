package com.example.leaftalk.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // == 회원(Member) 에러 ==
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, Constants.MEMBER, "이미 존재하는 이메일입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, Constants.MEMBER, "회원을 찾을 수 없습니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, Constants.MEMBER, "비밀번호가 일치하지 않습니다."),

    // == 공통(Global) 에러 ==
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, Constants.GLOBAL, "서버 내부 에러입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, Constants.GLOBAL, "잘못된 입력값입니다."),

    // == 권한(Authentication) 에러 ==
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, Constants.AUTHENTICATION, "인증에 실패하였습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, Constants.AUTHENTICATION, "인증이 거부되었습니다."),
    COOKIE_NOT_FOUND(HttpStatus.UNAUTHORIZED, Constants.AUTHENTICATION, "쿠키가 존재하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, Constants.AUTHENTICATION, "Refresh Token이 존재하지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, Constants.AUTHENTICATION, "유효하지 않은 Refresh Token입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, Constants.AUTHENTICATION, "만료된 Refresh Token입니다."),
    SECURITY_RISK_DETECTED(HttpStatus.UNAUTHORIZED, Constants.AUTHENTICATION, "보안 위험이 감지되었습니다.");

    private final HttpStatus httpStatus;
    private final String type;
    private final String message;

    private static class Constants {
        public static final String MEMBER = "MEMBER";
        public static final String GLOBAL = "GLOBAL";
        public static final String AUTHENTICATION = "AUTHENTICATION";
    }
}
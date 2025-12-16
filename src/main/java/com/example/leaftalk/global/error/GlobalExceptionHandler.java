package com.example.leaftalk.global.error;

import com.example.leaftalk.domain.member.exception.DuplicateEmailException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException e) {
        log.warn("중복 가입: {}", e.getMessage());
        return ErrorResponse.toResponseEntity(ErrorCode.DUPLICATE_EMAIL);
    }

}
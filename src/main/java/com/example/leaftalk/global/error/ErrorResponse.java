package com.example.leaftalk.global.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ErrorResponse {

    private final int status;
    private final String code;
    private final String message;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ErrorResponse.builder()
                               .status(errorCode.getHttpStatus().value())
                               .code(errorCode.getType())
                               .message(errorCode.getMessage())
                               .build()
            );
    }
}
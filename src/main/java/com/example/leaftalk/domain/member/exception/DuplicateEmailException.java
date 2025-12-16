package com.example.leaftalk.domain.member.exception;

public class DuplicateEmailException extends RuntimeException{

    public DuplicateEmailException(String message) {
        super(message);
    }

    public DuplicateEmailException(String message, String email) {
        super(message + "(" + email + ")");
    }

}

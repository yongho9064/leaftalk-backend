package com.example.leaftalk.domain.member.entity;

import lombok.Getter;

@Getter
public enum SocialProvider {

    NAVER("네이버"), GOOGLE("구글");

    private final String description;

    SocialProvider(String description) {
        this.description = description;
    }

}
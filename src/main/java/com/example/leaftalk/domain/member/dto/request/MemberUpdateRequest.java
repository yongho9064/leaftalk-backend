package com.example.leaftalk.domain.member.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberUpdateRequest {
    private String nickname;

    @Builder
    public MemberUpdateRequest(String nickname) {
        this.nickname = nickname;
    }
}

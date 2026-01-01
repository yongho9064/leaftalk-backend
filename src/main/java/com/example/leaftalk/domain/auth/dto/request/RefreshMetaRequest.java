package com.example.leaftalk.domain.auth.dto.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshMetaRequest {

    private String refreshToken;
    private String ip;
    private String userAgent;

    @Builder
    public RefreshMetaRequest(String refreshToken, String ip, String userAgent) {
        this.refreshToken = refreshToken;
        this.ip = ip;
        this.userAgent = userAgent;
    }

}

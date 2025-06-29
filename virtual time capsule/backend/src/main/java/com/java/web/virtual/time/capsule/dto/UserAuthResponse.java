package com.java.web.virtual.time.capsule.dto;

import lombok.Data;

@Data
public class UserAuthResponse {

    private String accessToken;
    private String refreshToken;
    private UserResponseDto user;

    public UserAuthResponse(String accessToken, String refreshToken, UserResponseDto user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

}

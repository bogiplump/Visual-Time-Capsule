package com.java.web.virtual.time.capsule.dto;

import lombok.Data;

@Data
public class UsersAuthResponse {

    private String accessToken;
    private String refreshToken;
    private UserResponseDto user;

    public UsersAuthResponse(String accessToken, String refreshToken, UserResponseDto user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

}

package com.java.web.virtual.time.capsule.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class UsersAuthResponse {

    private String accessToken;
    private String refreshToken;

    public UsersAuthResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

}

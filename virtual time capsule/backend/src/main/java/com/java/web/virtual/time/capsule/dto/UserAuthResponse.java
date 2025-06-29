package com.java.web.virtual.time.capsule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAuthResponse {
    private String accessToken;
    private String refreshToken;
    private UserResponseDto user;
}

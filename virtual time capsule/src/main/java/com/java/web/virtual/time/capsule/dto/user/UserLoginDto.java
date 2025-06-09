package com.java.web.virtual.time.capsule.dto.user;

import com.java.web.virtual.time.capsule.annotation.ValidPassword;
import com.java.web.virtual.time.capsule.annotation.ValidUsername;
import lombok.Getter;

@Getter
public class UserLoginDto {

    @ValidUsername
    private String username;

    @ValidPassword
    private String password;

}

package com.java.web.virtual.time.capsule.dto;

import com.java.web.virtual.time.capsule.annotation.ValidEmail;
import com.java.web.virtual.time.capsule.annotation.ValidName;
import com.java.web.virtual.time.capsule.annotation.ValidPassword;
import com.java.web.virtual.time.capsule.annotation.ValidUsername;
import lombok.Data;


@Data
public class UserCreateDto {

    @ValidUsername
    private String username;

    @ValidPassword
    private String password;

    @ValidName
    private String firstName;

    @ValidName
    private String lastName;

    @ValidEmail
    private String email;
}

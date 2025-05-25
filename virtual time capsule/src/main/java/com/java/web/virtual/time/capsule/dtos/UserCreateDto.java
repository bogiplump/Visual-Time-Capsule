package com.java.web.virtual.time.capsule.dtos;

import com.java.web.virtual.time.capsule.annotations.ValidEmail;
import com.java.web.virtual.time.capsule.annotations.ValidName;
import com.java.web.virtual.time.capsule.annotations.ValidPassword;
import com.java.web.virtual.time.capsule.annotations.ValidUsername;
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

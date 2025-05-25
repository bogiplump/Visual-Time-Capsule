package com.java.web.virtual.time.capsule.dtos;

import com.java.web.virtual.time.capsule.annotations.ValidPassword;
import com.java.web.virtual.time.capsule.annotations.ValidUsername;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;

@Getter
public class UserLoginDto {

    @ValidUsername
    private String username;

    @ValidPassword
    private String password;

}

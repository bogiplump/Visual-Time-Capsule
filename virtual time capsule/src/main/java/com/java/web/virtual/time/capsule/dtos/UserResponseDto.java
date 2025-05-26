package com.java.web.virtual.time.capsule.dtos;

import com.java.web.virtual.time.capsule.annotations.ValidEmail;
import com.java.web.virtual.time.capsule.annotations.ValidName;
import com.java.web.virtual.time.capsule.annotations.ValidPassword;
import com.java.web.virtual.time.capsule.annotations.ValidUsername;
import com.java.web.virtual.time.capsule.model.CapsuleUser;
import lombok.Data;

@Data
public class UserResponseDto {

    private Long id;

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

    public static UserResponseDto fromUser(final CapsuleUser user) {
        final UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername(user.getUsername());
        userResponseDto.setPassword(user.getPassword());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());
        userResponseDto.setEmail(user.getEmail());
        return userResponseDto;
    }
}

package com.java.web.virtual.time.capsule.dto.user;

import com.java.web.virtual.time.capsule.annotation.ValidEmail;
import com.java.web.virtual.time.capsule.annotation.ValidName;
import com.java.web.virtual.time.capsule.annotation.ValidPassword;
import com.java.web.virtual.time.capsule.annotation.ValidUsername;
import com.java.web.virtual.time.capsule.model.User;
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

    public static UserResponseDto fromUser(final User user) {
        final UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername(user.getUsername());
        userResponseDto.setPassword(user.getPassword());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());
        userResponseDto.setEmail(user.getEmail());
        return userResponseDto;
    }
}

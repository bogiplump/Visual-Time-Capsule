package com.java.web.virtual.time.capsule.dto;

import com.java.web.virtual.time.capsule.annotation.ValidEmail;
import com.java.web.virtual.time.capsule.annotation.ValidName;
import com.java.web.virtual.time.capsule.annotation.ValidUsername;
import com.java.web.virtual.time.capsule.model.Friendship;
import com.java.web.virtual.time.capsule.model.UserModel;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class UserResponseDto {

    private Long id;

    @ValidUsername
    private String username;

    @ValidName
    private String firstName;

    @ValidName
    private String lastName;

    @ValidEmail
    private String email;

    private List<Friendship> friendships;

    public static UserResponseDto fromUserAndHisFriends(final UserModel user, List<Friendship> friendships) {
        UserResponseDto userResponseDto = fromUser(user);
        userResponseDto.friendships = friendships;
        return userResponseDto;
    }

    public static UserResponseDto fromUser(final UserModel user) {
        final UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setUsername(user.getUsername());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());
        userResponseDto.setEmail(user.getEmail());
        return userResponseDto;
    }
}

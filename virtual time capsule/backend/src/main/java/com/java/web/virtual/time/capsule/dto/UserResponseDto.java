package com.java.web.virtual.time.capsule.dto;

import com.java.web.virtual.time.capsule.annotation.ValidEmail;
import com.java.web.virtual.time.capsule.annotation.ValidName;
import com.java.web.virtual.time.capsule.annotation.ValidUsername;
import com.java.web.virtual.time.capsule.model.Friendship;
import lombok.Data;

import java.util.List;

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
    private String creationDate;

}

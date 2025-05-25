package com.java.web.virtual.time.capsule.service;


import com.java.web.virtual.time.capsule.dtos.FriendshipDto;
import com.java.web.virtual.time.capsule.dtos.UserCreateDto;
import com.java.web.virtual.time.capsule.dtos.UserLoginDto;
import com.java.web.virtual.time.capsule.dtos.UserResponseDto;

import java.util.List;

public interface UserService {

    void registerUser(UserCreateDto user);

    void updateUser(Long id);

    void deleteUser(Long id);

    void loginUser(UserLoginDto user);

    List<UserResponseDto> getUsers();

    void sendInvitation(String sender, Long receiver);

    void answerInvitation(String sender, FriendshipDto friendshipDto);
}

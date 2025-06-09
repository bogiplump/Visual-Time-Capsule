package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.user.FriendshipDto;
import com.java.web.virtual.time.capsule.dto.user.UserCreateDto;
import com.java.web.virtual.time.capsule.dto.user.UserLoginDto;
import com.java.web.virtual.time.capsule.dto.user.UserResponseDto;
import com.java.web.virtual.time.capsule.dto.user.UserUpdateDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    void registerUser(UserCreateDto user);

    void updateUser(Long id, UserUpdateDto user);

    void deleteUser(Long id);

    void loginUser(UserLoginDto user);

    List<UserResponseDto> getUsers();

    void sendInvitation(Long sender, Long receiver);

    void answerInvitation(Long sender, FriendshipDto friendshipDto);
}

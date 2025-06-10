package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.FriendshipDto;
import com.java.web.virtual.time.capsule.dto.UserCreateDto;
import com.java.web.virtual.time.capsule.dto.UserLoginDto;
import com.java.web.virtual.time.capsule.dto.UserResponseDto;
import com.java.web.virtual.time.capsule.dto.UserUpdateDto;
import com.java.web.virtual.time.capsule.exception.user.UserNotFound;
import com.java.web.virtual.time.capsule.model.User;
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

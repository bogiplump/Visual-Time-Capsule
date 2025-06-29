package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.CapsuleResponseDto;
import com.java.web.virtual.time.capsule.dto.FriendshipDto;
import com.java.web.virtual.time.capsule.dto.UserCreateDto;
import com.java.web.virtual.time.capsule.dto.UserLoginDto;
import com.java.web.virtual.time.capsule.dto.UserProfileDto;
import com.java.web.virtual.time.capsule.dto.UserResponseDto;
import com.java.web.virtual.time.capsule.dto.UserUpdateDto;
import com.java.web.virtual.time.capsule.model.Friendship;
import com.java.web.virtual.time.capsule.model.UserModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface UserService {

    void registerUser(UserCreateDto user);

    void updateUser(Long id, UserUpdateDto user);

    void deleteUser(Long id);

    UserModel loginUser(UserLoginDto user);

    List<UserResponseDto> getUsers();

    Set<CapsuleResponseDto> getCapsules(Long id);

    void sendInvitation(Long sender, Long receiver);

    void answerInvitation(Long id, FriendshipDto friendshipDto);

    UserModel getUser(String username);

    UserModel getUserById(Long id);

    List<FriendshipDto> getFriendships(Long id);


    List<UserProfileDto> getAllUserProfilesExceptCurrentUser(String username);
}

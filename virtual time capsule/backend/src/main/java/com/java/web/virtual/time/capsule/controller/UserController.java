package com.java.web.virtual.time.capsule.controller;

import com.java.web.virtual.time.capsule.dto.CapsuleResponseDto;
import com.java.web.virtual.time.capsule.dto.FriendshipDto;
import com.java.web.virtual.time.capsule.dto.GoalDto;
import com.java.web.virtual.time.capsule.dto.UserProfileDto;
import com.java.web.virtual.time.capsule.dto.UserResponseDto;
import com.java.web.virtual.time.capsule.dto.UserUpdateDto;
import com.java.web.virtual.time.capsule.mapper.GoalMapper;
import com.java.web.virtual.time.capsule.mapper.UserMapper;
import com.java.web.virtual.time.capsule.model.UserModel;
import com.java.web.virtual.time.capsule.service.GoalService;
import com.java.web.virtual.time.capsule.service.UserService;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {


    private final UserService userService;
    private final GoalService goalService;
    private final GoalMapper goalMapper;
    private final UserMapper userMapper;

    public UserController(UserService userService, GoalService goalService,
                          GoalMapper goalMapper, UserMapper userMapper) {
        this.userService = userService;
        this.goalService = goalService;
        this.goalMapper = goalMapper;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable @Positive long id) {
        UserModel userModel = userService.getUserById(id);
        UserResponseDto userResponseDto = userMapper.toUserResponseDto(userModel);
        return ResponseEntity.ok(userResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@Positive @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Deleted user with id " + id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@Positive @PathVariable Long id, @RequestBody UserUpdateDto userUpdateDto) {
        userService.updateUser(id,userUpdateDto);
        return ResponseEntity.ok("Updated user with id " + id);
    }

    @PostMapping("/friendship/{id}")
    public ResponseEntity<String> createFriendshipInvitation(@Positive @PathVariable Long id, Principal principal) {
        String user = principal.getName();
        UserModel currentUser = userService.getUser(user);
        userService.sendInvitation(currentUser.getId(),id);
        return ResponseEntity.ok("Friendship invitation created");
    }

    @PutMapping("/friendship/{id}")
    public ResponseEntity<String> updateFriendship(@Positive @PathVariable Long id, @RequestBody FriendshipDto friendshipDto) {
        userService.answerInvitation(id, friendshipDto);
        return ResponseEntity.ok("Friendship invitation answered");
    }

    @GetMapping("/{id}/goals")
    public ResponseEntity<List<GoalDto>> getUserGoals(@PathVariable Long id) {
        List<GoalDto> userGoals = goalService.getUserGoals(id).stream()
            .map(goalMapper::toDto)
            .toList();

        return ResponseEntity.ok(userGoals);
    }

    @GetMapping("/{id}/capsules")
    public ResponseEntity<Set<CapsuleResponseDto>> getUserCapsules(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getCapsules(id));
    }

    @GetMapping
    public ResponseEntity<List<UserProfileDto>> getAllUsersExceptCurrent(Principal principal) {
        log.info("Received request to get all user profiles");

        List<UserProfileDto> users = userService.getAllUserProfilesExceptCurrentUser(principal.getName());

        log.info("User profiles except current user: {}", users.toString());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}/friendships")
    public ResponseEntity<List<FriendshipDto>> getUserFriendships(@PathVariable Long id) {
        log.info("Received request to get all user friendships");

        UserModel user = userService.getUserById(id);
        List<FriendshipDto> friendshipDtos = userService.getFriendships(user.getId());
        return ResponseEntity.ok(friendshipDtos);
    }

}

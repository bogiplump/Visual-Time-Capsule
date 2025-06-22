package com.java.web.virtual.time.capsule.controller;

import com.java.web.virtual.time.capsule.dto.FriendshipDto;
import com.java.web.virtual.time.capsule.dto.GoalDto;
import com.java.web.virtual.time.capsule.dto.UserResponseDto;
import com.java.web.virtual.time.capsule.dto.UserUpdateDto;
import com.java.web.virtual.time.capsule.mapper.GoalMapper;
import com.java.web.virtual.time.capsule.model.Friendship;
import com.java.web.virtual.time.capsule.model.UserModel;
import com.java.web.virtual.time.capsule.service.GoalService;
import com.java.web.virtual.time.capsule.service.impl.JwtService;
import com.java.web.virtual.time.capsule.service.UserService;
import jakarta.validation.constraints.Positive;
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

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    private final JwtService jwtService;
    private final UserService userService;
    private final GoalService goalService;

    public UserController(JwtService jwtService, UserService userService, GoalService goalService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.goalService = goalService;
    }

    @GetMapping()
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable @Positive long id) {
        UserModel userModel = userService.getUserById(id);
        List<Friendship> friendships = userService.getFriendships(id);
        return ResponseEntity.ok(UserResponseDto.fromUserAndHisFriends(userModel,friendships));
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
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String user = principal.getName();
        UserModel currentUser = userService.getUser(user);
        userService.sendInvitation(currentUser.getId(),id);
        return ResponseEntity.ok("Friendship invitation created");
    }

    @PutMapping("/friendship/{id}")
    public ResponseEntity<String> updateFriendship(@Positive @PathVariable Long id,@Positive @RequestBody FriendshipDto friendshipDto, Principal principal) {
        userService.answerInvitation(id, friendshipDto);
        return ResponseEntity.ok("Friendship invitation answered");
    }

    @GetMapping("/{id}/goals")
    public ResponseEntity<List<GoalDto>> getUserGoals(@PathVariable Long id) {
        List<GoalDto> userGoals = goalService.getUserGoals(id).stream()
            .map(GoalMapper.INSTANCE::toDTO)
            .toList();

        return ResponseEntity.ok(userGoals);
    }
}

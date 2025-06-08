package com.java.web.virtual.time.capsule.controller;

import com.java.web.virtual.time.capsule.dtos.FriendshipDto;
import com.java.web.virtual.time.capsule.dtos.GoalDto;
import com.java.web.virtual.time.capsule.dtos.UserResponseDto;
import com.java.web.virtual.time.capsule.mapper.GoalMapper;
import com.java.web.virtual.time.capsule.service.GoalService;
import com.java.web.virtual.time.capsule.service.JwtService;
import com.java.web.virtual.time.capsule.service.UserService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {
    private final UserService userService;
    private final GoalService goalService;

    public UserController(JwtService jwtService, UserService userService, GoalService goalService) {
        this.userService = userService;
        this.goalService = goalService;
    }

    @GetMapping()
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @DeleteMapping("/id")
    public ResponseEntity<String> deleteUser(@Positive @RequestParam Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Deleted user with id " + id);
    }

    @PutMapping("/id")
    public ResponseEntity<String> updateUser(@Positive @RequestParam Long id) {
        userService.updateUser(id);
        return ResponseEntity.ok("Updated user with id " + id);
    }

    @PostMapping("/friendship/id")
    public ResponseEntity<String> createFriendshipInvitation(@Positive @RequestParam Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String user = principal.getName();
        userService.sendInvitation(user,id);
        return ResponseEntity.ok("Friendship invitation created");
    }

    @PutMapping("/friendship/id")
    public ResponseEntity<String> updateFriendship(@Positive @RequestBody FriendshipDto friendshipDto, Principal principal) {
        String user = principal.getName();
        userService.answerInvitation(user, friendshipDto);
        return ResponseEntity.ok("Friendship invitation answered");
    }

    @GetMapping("/{id}/goals")
    public ResponseEntity<List<GoalDto>> getUserGoals(@RequestParam Long id) {
        List<GoalDto> userGoals = goalService.getUserGoals(id).stream()
            .map(GoalMapper.INSTANCE::toDTO)
            .toList();

        return ResponseEntity.ok(userGoals);
    }
}

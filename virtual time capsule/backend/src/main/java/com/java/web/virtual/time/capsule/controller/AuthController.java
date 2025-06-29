package com.java.web.virtual.time.capsule.controller;

import com.java.web.virtual.time.capsule.dto.UserCreateDto;
import com.java.web.virtual.time.capsule.dto.UserLoginDto;
import com.java.web.virtual.time.capsule.dto.UserAuthResponse;
import com.java.web.virtual.time.capsule.mapper.UserMapper;
import com.java.web.virtual.time.capsule.model.UserModel;
import com.java.web.virtual.time.capsule.service.impl.JwtService;
import com.java.web.virtual.time.capsule.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;
    private final UserMapper userMapper;

    public AuthController(JwtService jwtService, UserService userService, UserMapper userMapper) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<UserAuthResponse> login(@Valid @RequestBody UserLoginDto request) {
        UserModel userModel = userService.loginUser(request);
        String accessToken = jwtService.generateAccessToken(request.getUsername());
        String refreshToken = jwtService.generateRefreshToken(request.getUsername());
        return ResponseEntity.ok(new UserAuthResponse(accessToken, refreshToken, userMapper.toUserResponseDto(userModel)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<UserAuthResponse> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (jwtService.isTokenValid(refreshToken)) {
            String username = jwtService.extractUsername(refreshToken);
            String newAccessToken = jwtService.generateAccessToken(username);
            UserModel userModel = userService.getUser(username);
            return ResponseEntity.ok(new UserAuthResponse(newAccessToken, refreshToken,userMapper.toUserResponseDto(userModel)));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserCreateDto request) {
        userService.registerUser(request);
        return ResponseEntity.ok().build();
    }
}

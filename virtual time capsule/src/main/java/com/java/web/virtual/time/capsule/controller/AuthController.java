package com.java.web.virtual.time.capsule.controller;

import com.java.web.virtual.time.capsule.dtos.UserCreateDto;
import com.java.web.virtual.time.capsule.dtos.UserLoginDto;
import com.java.web.virtual.time.capsule.dtos.UsersAuthResponse;
import com.java.web.virtual.time.capsule.service.JwtService;
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

    public AuthController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UsersAuthResponse> login(@Valid @RequestBody UserLoginDto request) {
        userService.loginUser(request);
        String accessToken = jwtService.generateAccessToken(request.getUsername());
        String refreshToken = jwtService.generateRefreshToken(request.getUsername());
        return ResponseEntity.ok(new UsersAuthResponse(accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<UsersAuthResponse> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (jwtService.isTokenValid(refreshToken)) {
            String username = jwtService.extractUsername(refreshToken);
            String newAccessToken = jwtService.generateAccessToken(username);
            return ResponseEntity.ok(new UsersAuthResponse(newAccessToken, refreshToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserCreateDto request) {
        userService.registerUser(request);
        return ResponseEntity.ok().build();
    }
}

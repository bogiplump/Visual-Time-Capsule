package com.java.web.virtual.time.capsule.dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateDto {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
}

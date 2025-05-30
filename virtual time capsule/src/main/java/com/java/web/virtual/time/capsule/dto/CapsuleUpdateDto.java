package com.java.web.virtual.time.capsule.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CapsuleUpdateDto {
    @NotBlank(message = "Capsule name must not be blank")
    @Size(max = 20, message = "Capsule name must not exceed 20 characters")
    private String capsuleName;

    @Future(message = "Open date must be in the future")
    private LocalDateTime openDate;

    private GoalDto goal;
}

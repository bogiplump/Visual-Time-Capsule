package com.java.web.virtual.time.capsule.dto;

import com.java.web.virtual.time.capsule.model.Goal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CapsuleCreateDto {
    @NotBlank(message = "Capsule name cannot be empty")
    @Size(max = 100, message = "Capsule name cannot exceed 100 characters")
    private String capsuleName;

    // IMPORTANT: Receive as a String directly from frontend's toISOString()
    // It will be parsed into LocalDateTime in your service/mapper
    private String openDateTime;

    @NotNull(message = "Goal details cannot be null")
    private GoalDto goal; // Assuming GoalCreateDto is a separate DTO
}

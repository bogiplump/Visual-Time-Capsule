package com.java.web.virtual.time.capsule.dto;

import jakarta.validation.Valid; // Keep this import
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor; // Add AllArgsConstructor
import lombok.Builder; // Add Builder
import lombok.Data;
import lombok.NoArgsConstructor; // Add NoArgsConstructor

import java.util.Set; // Import Set for sharedWithUserIds

@Data
@Builder // Add Builder to CapsuleCreateDto
@NoArgsConstructor // Add NoArgsConstructor
@AllArgsConstructor // Add AllArgsConstructor
public class CapsuleCreateDto {
    @NotBlank(message = "Capsule name cannot be empty")
    @Size(max = 100, message = "Capsule name cannot exceed 100 characters")
    private String capsuleName;

    private String openDateTime;

    @NotNull(message = "Goal details cannot be null")
    @Valid // Ensure nested GoalCreateDto is validated
    private GoalCreateDto goal;

    // NEW: List of user IDs to share this capsule with
    private Set<Long> sharedWithUserIds;

}

package com.java.web.virtual.time.capsule.dto;

import jakarta.validation.Valid; 
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor; 
import lombok.Builder; 
import lombok.Data;
import lombok.NoArgsConstructor; 

import java.util.Set; 

@Data
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
public class CapsuleCreateDto {
    @NotBlank(message = "Capsule name cannot be empty")
    @Size(max = 100, message = "Capsule name cannot exceed 100 characters")
    private String capsuleName;
    private String openDateTime;
    @NotNull(message = "Goal details cannot be null")
    @Valid 
    private GoalCreateDto goal;
    private Set<Long> sharedWithUserIds;
}

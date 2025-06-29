package com.java.web.virtual.time.capsule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalCreateDto {
    @NotBlank(message = "Goal content cannot be empty")
    @Size(max = 500, message = "Goal content cannot exceed 500 characters")
    private String content;
    private Boolean isVisible;
}
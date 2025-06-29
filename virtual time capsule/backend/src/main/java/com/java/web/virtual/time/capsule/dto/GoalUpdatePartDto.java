package com.java.web.virtual.time.capsule.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GoalUpdatePartDto {
    @NotNull(message = "Goal content cannot be null")
    @Size(min = 1, max = 500, message = "Goal content must be between 1 and 500 characters")
    private String content;

    private boolean isAchieved;
    private boolean isVisible;
}


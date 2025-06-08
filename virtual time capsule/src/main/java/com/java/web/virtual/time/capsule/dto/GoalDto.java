package com.java.web.virtual.time.capsule.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalDto {
    private boolean isVisible;
    private boolean isAchieved;
    private String content;
}

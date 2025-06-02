package com.java.web.virtual.time.capsule.dtos;

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
    private Long id;
    private boolean isVisible;
    private boolean isAchieved;
    private LocalDate creationDate;
    private String content;
}

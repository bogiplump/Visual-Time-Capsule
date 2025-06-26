package com.java.web.virtual.time.capsule.dto.goal;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalCreateDto {
    private boolean isVisible;
    private boolean isAchieved;
    private String content;
}

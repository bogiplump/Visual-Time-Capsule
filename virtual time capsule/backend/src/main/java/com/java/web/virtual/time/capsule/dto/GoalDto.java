package com.java.web.virtual.time.capsule.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalDto {
    private Long id;
    private String content;
    private boolean isAchieved;
    private boolean isVisible;
    private String creationDate; 
    private Long creator; 
    private Long capsuleId; 
}

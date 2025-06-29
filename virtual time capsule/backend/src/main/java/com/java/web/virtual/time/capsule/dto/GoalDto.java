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
    private String creationDate; // ISO string
    private Long creator; // Expecting creator's ID here
    private Long capsuleId; // Back-reference to capsule ID if needed in DTO
}

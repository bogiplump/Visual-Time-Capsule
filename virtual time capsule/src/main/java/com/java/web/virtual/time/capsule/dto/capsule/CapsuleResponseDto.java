package com.java.web.virtual.time.capsule.dto.capsule;

import com.java.web.virtual.time.capsule.dto.goal.GoalResponseDto;
import com.java.web.virtual.time.capsule.dto.memory.MemoryResponseDto;
import lombok.Data;

import java.util.Set;

@Data
public class CapsuleResponseDto {
    private String capsuleName;
    private String status;

    private String creationDate;
    private String lockDate;
    private String openDate;

    private String creatorName;

    private GoalResponseDto goal;
    private Set<MemoryResponseDto> memories;
}

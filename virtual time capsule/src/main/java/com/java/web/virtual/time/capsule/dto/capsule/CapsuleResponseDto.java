package com.java.web.virtual.time.capsule.dto.capsule;

import com.java.web.virtual.time.capsule.dto.goal.GoalResponseDto;
import com.java.web.virtual.time.capsule.dto.memory.MemoryResponseDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class CapsuleResponseDto {
    private String capsuleName;
    private String status;

    private LocalDateTime creationDate;
    private LocalDateTime lockDate;
    private LocalDateTime openDate;

    private String creatorName;

    private GoalResponseDto goal;
    private Set<MemoryResponseDto> memories;
}

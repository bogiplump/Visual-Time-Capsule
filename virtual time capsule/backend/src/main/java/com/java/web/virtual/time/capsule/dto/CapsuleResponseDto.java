package com.java.web.virtual.time.capsule.dto;

import com.java.web.virtual.time.capsule.enums.CapsuleStatus;
import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.Goal; // Import Goal
import com.java.web.virtual.time.capsule.model.Memory; // Import Memory
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set; // For Set<MemoryDto>

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CapsuleResponseDto {
    private Long id;
    private String capsuleName;
    private CapsuleStatus status;
    private String creationDate;
    private String lockDate;
    private String openDateTime;
    private UserProfileDto creator;
    private GoalDto goal; // UNCOMMENT AND INCLUDE
    private Set<MemoryDto> memories; // NEW: Add memories as DTOs
}
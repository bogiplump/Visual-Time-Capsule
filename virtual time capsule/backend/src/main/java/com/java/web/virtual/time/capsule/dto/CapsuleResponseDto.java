package com.java.web.virtual.time.capsule.dto;

import com.java.web.virtual.time.capsule.enums.CapsuleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

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
    private Long goalId;
    private Set<MemoryDto> memories;
    private Boolean isShared;
    private Set<UserProfileDto> sharedWithUsers; 
    private Integer readyParticipantsCount; 
    private Integer totalParticipantsCount; 
    private Boolean isCurrentUserReadyToClose; 
}

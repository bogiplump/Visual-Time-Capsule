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
    private GoalDto goal;
    private Set<MemoryDto> memories;

    // NEW: Fields for shared capsules
    private boolean isShared;
    private Set<UserProfileDto> sharedWithUsers; // List of users the capsule is shared with
    private Integer readyParticipantsCount; // 'm' in m/n
    private Integer totalParticipantsCount; // 'n' in m/n
    private Boolean isCurrentUserReadyToClose; // True if the current authenticated user has marked themselves "ready"
}

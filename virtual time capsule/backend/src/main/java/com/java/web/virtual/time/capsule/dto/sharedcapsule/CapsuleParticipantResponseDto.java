package com.java.web.virtual.time.capsule.dto.sharedcapsule;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CapsuleParticipantResponseDto {
    private Long idOfUser;
    private String username;

    private boolean isReadyToClose;
}

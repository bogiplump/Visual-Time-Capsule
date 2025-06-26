package com.java.web.virtual.time.capsule.dto.sharedcapsule;

import com.java.web.virtual.time.capsule.dto.capsule.CapsuleResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class SharedCapsuleResponseDto {
    private CapsuleResponseDto capsuleDto;

    private Set<CapsuleParticipantResponseDto> participantDtos;
}

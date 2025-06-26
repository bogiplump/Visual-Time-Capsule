package com.java.web.virtual.time.capsule.model;

import com.java.web.virtual.time.capsule.dto.capsule.CapsuleResponseDto;
import com.java.web.virtual.time.capsule.dto.sharedcapsule.CapsuleParticipantResponseDto;
import com.java.web.virtual.time.capsule.dto.sharedcapsule.SharedCapsuleResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class SharedCapsule {
    private Capsule capsule;

    private Set<CapsuleParticipant> participants;

    public SharedCapsuleResponseDto toResponseDto() {
        CapsuleResponseDto capsuleDto = capsule.toCapsuleResponseDto();

        Set<CapsuleParticipantResponseDto> participantDtos = new LinkedHashSet<>();
        for (CapsuleParticipant curr : participants) {
            participantDtos.add(curr.toResponseDto());
        }

        return new SharedCapsuleResponseDto(capsuleDto, participantDtos);
    }
}

package com.java.web.virtual.time.capsule.model;

import com.java.web.virtual.time.capsule.dto.sharedcapsule.SharedCapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.sharedcapsule.SharedCapsuleResponseDto;
import com.java.web.virtual.time.capsule.service.SharedCapsuleService;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class SharedCapsule {
    private Capsule capsule;

    private Set<String> usernames;

    public SharedCapsuleResponseDto toResponseDto() {
        return null; //TODO implement
    }

    public static SharedCapsule fromDtoAndUser(SharedCapsuleCreateDto sharedCapsuleDto,
                                               User currentUser) {
        return null; //TODO implement
    }
}

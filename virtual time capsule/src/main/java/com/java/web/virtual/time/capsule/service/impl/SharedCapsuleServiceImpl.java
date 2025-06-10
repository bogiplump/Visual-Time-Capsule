package com.java.web.virtual.time.capsule.service.impl;

import com.java.web.virtual.time.capsule.dto.sharedcapsule.SharedCapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.sharedcapsule.SharedCapsuleResponseDto;
import com.java.web.virtual.time.capsule.repository.UserRepository;
import com.java.web.virtual.time.capsule.service.CapsuleService;
import com.java.web.virtual.time.capsule.service.SharedCapsuleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class SharedCapsuleServiceImpl implements SharedCapsuleService {
    private UserRepository userRepository;
    private CapsuleService capsuleService;

    @Override
    public Long createSharedCapsule(SharedCapsuleCreateDto sharedCapsuleDto, String currentUser) {
        return null;
    }

    @Override
    public SharedCapsuleResponseDto getSharedCapsuleById(Long id, String currentUser) {
        return null;
    }

    @Override
    public void addUserToCapsule(Long capsuleId, Long userId, String currentUser) {

    }

    @Override
    public void removeUserFromCapsule(Long capsuleId, Long userId, String currentUser) {

    }

    @Override
    public void deleteSharedCapsuleById(Long id, String currentUser) {

    }
}

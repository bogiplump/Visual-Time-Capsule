package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.CapsuleResponseDto;
import com.java.web.virtual.time.capsule.dto.CapsuleUpdateDto;
import com.java.web.virtual.time.capsule.dto.MemoryDto;

import java.util.Set;

public interface CapsuleService {

    CapsuleResponseDto createCapsule(CapsuleCreateDto capsuleDto, String currentUser, Set<Long> sharedWithUserIds);

    CapsuleResponseDto getCapsuleById(Long capsuleId, String username);

    Set<CapsuleResponseDto> getAllCapsulesOfUser(String currentUser);

    CapsuleResponseDto lockCapsule(Long id, String openDateTimeInString, String lockingUsername);

    CapsuleResponseDto updateCapsule(Long id, CapsuleUpdateDto capsuleDto, String currentUser);

    void deleteCapsule(Long id, String currentUser);

    Set<MemoryDto> getMemoriesFromCapsule(Long capsuleId, String currentUser);

    CapsuleResponseDto openCapsule(Long id, String currentUser);

    CapsuleResponseDto markReadyToClose(Long capsuleId, String username);
}

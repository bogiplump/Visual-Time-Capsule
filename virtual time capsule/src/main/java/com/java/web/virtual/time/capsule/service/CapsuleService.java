package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.model.entity.CapsuleEntity;
import com.java.web.virtual.time.capsule.model.entity.GoalEntity;
import com.java.web.virtual.time.capsule.model.entity.MemoryEntity;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface CapsuleService {
    CapsuleEntity getCapsuleById(Long id);

    void saveCapsule(CapsuleEntity capsule);

    void deleteCapsuleById(Long id);

    void addMemoryToCapsule(Long capsuleId, MemoryEntity memory);

    void putMemoryInCapsule(Long capsuleId, Long memoryId);

    void removeMemoryFromCapsule(Long capsuleID, Long memoryId);

    MemoryEntity getMemoryFromCapsule(Long capsuleId, Long memoryId);

    Set<MemoryEntity> getMemoriesFromCapsule(Long capsuleId);

    void addGoalToCapsule(Long capsuleId, GoalEntity goal);

    GoalEntity getGoalOfCapsule(Long capsuleId);

    void removeGoalFromCapsule(Long capsuleId);

    void lockCapsuleById(Long id);

    void openCapsuleIfPossible(Long id);
}

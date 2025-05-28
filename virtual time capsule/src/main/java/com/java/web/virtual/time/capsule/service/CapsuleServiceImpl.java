package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.MemoryCreateDto;
import com.java.web.virtual.time.capsule.model.entity.CapsuleEntity;
import com.java.web.virtual.time.capsule.model.entity.GoalEntity;
import com.java.web.virtual.time.capsule.model.entity.MemoryEntity;

import java.util.Set;

public class CapsuleServiceImpl implements CapsuleService {
    @Override
    public CapsuleEntity getCapsuleById(Long id) {
        return null;
    }

    @Override
    public Set<CapsuleEntity> getAllCapsulesOfUser() {
        return Set.of();
    }

    @Override
    public void createCapsule(CapsuleCreateDto capsuleDto) {

    }

    @Override
    public void deleteCapsuleById(Long id) {

    }

    @Override
    public void addMemoryToCapsule(Long capsuleId, MemoryCreateDto memory) {

    }

    @Override
    public void putMemoryInCapsule(Long capsuleId, Long memoryId) {

    }

    @Override
    public void removeMemoryFromCapsule(Long capsuleId, Long memoryId) {

    }

    @Override
    public MemoryEntity getMemoryFromCapsule(Long capsuleId, Long memoryId) {
        return null;
    }

    @Override
    public Set<MemoryEntity> getMemoriesFromCapsule(Long capsuleId) {
        return Set.of();
    }

    @Override
    public void addGoalToCapsule(Long capsuleId, GoalEntity goal) {

    }

    @Override
    public GoalEntity getGoalOfCapsule(Long capsuleId) {
        return null;
    }

    @Override
    public void removeGoalFromCapsule(Long capsuleId) {

    }

    @Override
    public void lockCapsuleById(Long id, String openDate) {

    }

    @Override
    public void openCapsuleIfPossible(Long id) {

    }
}

package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.MemoryCreateDto;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleNotFound;
import com.java.web.virtual.time.capsule.model.entity.CapsuleEntity;
import com.java.web.virtual.time.capsule.model.entity.GoalEntity;
import com.java.web.virtual.time.capsule.model.entity.MemoryEntity;
import com.java.web.virtual.time.capsule.repository.CapsuleRepository;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Service
public class CapsuleServiceImpl implements CapsuleService {
    private String dateTimePattern = "HH-mm-ss_dd-MM-yy";

    private SecurityService securityService;
    private CapsuleRepository repository;
    private DateTimeFormatter formatter;

    public CapsuleServiceImpl(CapsuleRepository repository, SecurityService securityService) {
        this.repository = repository;
        this.securityService = securityService;

        this.formatter = DateTimeFormatter.ofPattern(dateTimePattern);
    }

    @Override
    public CapsuleEntity getCapsuleById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new CapsuleNotFound("Capsule with this id was not found. "));
    }

    @Override
    public Set<CapsuleEntity> getAllCapsulesOfUser() {
       Long currentUserId = securityService.getCurrentUserId();

       return repository.findByCreatedById(currentUserId);
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
    public void lockCapsuleById(Long id, String openDateInString) {
        if (id == null || openDateInString == null) {
            throw new IllegalArgumentException("Null reference in CapsuleServiceImpl::lockCapsuleById()");
        }

        CapsuleEntity capsule = repository.findById(id)
            .orElseThrow(() -> new CapsuleNotFound("Capsule with this id was not found"));

        LocalDateTime openDate = LocalDateTime.parse(openDateInString, formatter);

        capsule.lock(openDate);
    }

    @Override
    public void openCapsuleIfPossible(CapsuleEntity capsule) {
        if (capsule == null) {
            throw new IllegalArgumentException("Null reference in CapsuleServiceImpl::openCapsuleIfPossible()");
        }

        if (capsule.isTimeToOpen()) {
            capsule.open();
        }
    }
}

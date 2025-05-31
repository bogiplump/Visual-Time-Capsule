package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.CapsuleUpdateDto;
import com.java.web.virtual.time.capsule.dto.MemoryCreateDto;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleNotFound;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleNotOwnedByYou;
import com.java.web.virtual.time.capsule.exception.goal.GoalNotFound;
import com.java.web.virtual.time.capsule.exception.memory.MemoryNotInBank;
import com.java.web.virtual.time.capsule.exception.memory.MemoryNotInCapsule;
import com.java.web.virtual.time.capsule.model.entity.CapsuleEntity;
import com.java.web.virtual.time.capsule.model.entity.GoalEntity;
import com.java.web.virtual.time.capsule.model.entity.MemoryEntity;
import com.java.web.virtual.time.capsule.model.entity.UserEntity;
import com.java.web.virtual.time.capsule.repository.CapsuleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Service
public class CapsuleServiceImpl implements CapsuleService {
    private final static String dateTimePattern = "HH-mm-ss_dd-MM-yyyy";
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimePattern);

    private CapsuleRepository repository;

    private SecurityService securityService;
    private UserService userService;

    private GoalService goalService;
    private MemoryService memoryService;

    public CapsuleServiceImpl(CapsuleRepository repository, SecurityService securityService,
                              UserService userService, GoalService goalService,
                              MemoryService memoryService) {
        this.repository = repository;

        this.securityService = securityService;
        this.userService = userService;

        this.goalService = goalService;
        this.memoryService = memoryService;
    }

    private void handleCapsuleNotOwnedByUser(CapsuleEntity capsule) {
        if (capsule == null) {
            throw new IllegalArgumentException("Null reference in CapsuleService::isCapsuleOwnedByCurrentUser()");
        }

        Long currentUserId = securityService.getCurrentUserId();

        if (!capsule.getCreator().getId().equals(currentUserId)) {
            throw new CapsuleNotOwnedByYou("Capsule with id " + capsule.getId() + " is not owned by you");
        }
    }

    @Override
    public CapsuleEntity getCapsuleById(Long id) { //TODO: How a locked capsule is send to client
        if (id == null) {
            throw new IllegalArgumentException("Null reference in CapsuleService::getCapsuleById()");
        }

        CapsuleEntity capsule = repository.findById(id)
            .orElseThrow(() -> new CapsuleNotFound("Capsule with  id " + id + " was not found. "));

        handleCapsuleNotOwnedByUser(capsule);

        return capsule;
    }

    @Override
    public Set<CapsuleEntity> getAllCapsulesOfUser() {
       Long currentUserId = securityService.getCurrentUserId();

       return repository.findByCreatedById(currentUserId);
    }

    @Override
    public CapsuleEntity parseCapsuleCreateDto(CapsuleCreateDto capsuleDto) {
        if (capsuleDto == null) {
            throw new IllegalArgumentException("Null reference in CapsuleService::createCapsule()");
        }

        GoalEntity goal = null;
        if (capsuleDto.getGoal() != null) {
            goal = goalService.parseGoalDto(capsuleDto.getGoal());
        }

        UserEntity creator = userService.getUserById(securityService.getCurrentUserId());

        return new CapsuleEntity(capsuleDto.getCapsuleName(), creator, goal);
    }

    @Override
    public void createCapsule(CapsuleCreateDto capsuleDto) {
        CapsuleEntity capsule = parseCapsuleCreateDto(capsuleDto);

        repository.save(capsule);
    }

    @Override
    public void updateCapsule(Long id, CapsuleUpdateDto capsuleDto) {
        if (id == null || capsuleDto == null) {
            throw new IllegalArgumentException("Null reference in CapsuleService::updateCapsule()");
        }

        CapsuleEntity capsule = repository.findById(id)
            .orElseThrow(() -> new CapsuleNotFound("Capsule with  id " + id + " was not found. "));

        handleCapsuleNotOwnedByUser(capsule);

        if (capsuleDto.getCapsuleName() != null) {
            capsule.setCapsuleName(capsuleDto.getCapsuleName());
        }
        if (capsuleDto.getOpenDate() != null) {
            capsule.setOpenDate(capsuleDto.getOpenDate());
        }
        if (capsuleDto.getGoal() != null) {
            GoalEntity goal = goalService.parseGoalDto(capsuleDto.getGoal());

            if (capsule.getGoal() != null) {
                goalService.deleteGoal(capsule.getGoal().getId());
            }

            capsule.setGoal(goal);
        }
    }

    @Override
    public void deleteCapsuleById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Null reference in CapsuleService::deleteCapsuleById()");
        }

        if (repository.existsByIdAndCreatedById(id, securityService.getCurrentUserId())) {
           throw new CapsuleNotFound("Capsule with  id " + id + " was not found or is not owned by you. ");
        }

        repository.deleteById(id);
    }

    @Override
    public void addMemoryToCapsule(Long capsuleId, MemoryCreateDto memoryDto) {
        if (capsuleId == null || memoryDto == null) {
            throw new IllegalArgumentException("Null reference in CapsuleService::addMemoryToCapsule()");
        }

        CapsuleEntity capsule = repository.findById(capsuleId)
            .orElseThrow(() -> new CapsuleNotFound("Capsule with  id " + capsuleId + " was not found"));

        handleCapsuleNotOwnedByUser(capsule);

        MemoryEntity memory = memoryService.parseMemoryDto(memoryDto);

        capsule.addMemory(memory);

        repository.save(capsule);
    }

    @Override
    public void putMemoryInCapsule(Long capsuleId, Long memoryId) {
        if (capsuleId == null || memoryId == null) {
            throw new IllegalArgumentException("Null reference CapsuleService::putMemoryInCapsule()");
        }

        CapsuleEntity capsule = repository.findById(capsuleId)
            .orElseThrow(() -> new CapsuleNotFound("Capsule with  id " + capsuleId + " was not found"));

        MemoryEntity memory = memoryService.getMemoryById(memoryId);

        if (memory.getCapsule() != null) {
            throw new MemoryNotInBank("Memory with id " + memoryId + " is not in your memory bank");
        }

        capsule.addMemory(memory);

        repository.save(capsule);
    }

    @Override
    public void removeMemoryFromCapsule(Long capsuleId, Long memoryId) {
        if (capsuleId == null || memoryId == null) {
            throw new IllegalArgumentException("Null reference in CapsuleService::removeMemoryFromCapsule()");
        }

        if (!memoryService.isMemoryInCapsule(memoryId, capsuleId)) {
            throw new MemoryNotInCapsule();
        }

        memoryService.deleteMemoryById(memoryId);
    }

    @Override
    public MemoryEntity getMemoryFromCapsule(Long capsuleId, Long memoryId) {
        if (capsuleId == null || memoryId == null) {
            throw new IllegalArgumentException("Null reference CapsuleService::getMemoryFromCapsule()");
        }

        CapsuleEntity capsule = repository.findById(capsuleId)
            .orElseThrow(() -> new CapsuleNotFound("Capsule with  id " + capsuleId + " was not found"));

        handleCapsuleNotOwnedByUser(capsule);

        MemoryEntity memory = memoryService.getMemoryById(memoryId);

        if (!memory.getCapsule().getId().equals(capsule.getId())) {
            throw new MemoryNotInCapsule("Memory with id" + memoryId + " is not in your capsule");
        }

        return memory;
    }

    @Override
    public Set<MemoryEntity> getMemoriesFromCapsule(Long capsuleId) {
        if (capsuleId == null) {
            throw new IllegalArgumentException("Null reference in CapsuleService::getMemoriesFromCapsule()");
        }

        CapsuleEntity capsule = repository.findById(capsuleId)
            .orElseThrow(() -> new CapsuleNotFound("Capsule with  id " + capsuleId + " was not found"));

        handleCapsuleNotOwnedByUser(capsule);

        return capsule.getMemoryEntries();
    }

    @Override
    public void addGoalToCapsule(Long capsuleId, GoalEntity goal) { //TODO Check if goal is created by the current user
        if (capsuleId == null || goal == null) {
            throw new IllegalArgumentException("Null reference in CapsuleService::addGoalToCapsule()");
        }

        CapsuleEntity capsule = repository.findById(capsuleId)
            .orElseThrow(() -> new CapsuleNotFound("Capsule with  id " + capsuleId + " was not found"));

        handleCapsuleNotOwnedByUser(capsule);

        capsule.setGoal(goal);
        repository.save(capsule);
    }

    @Override
    public GoalEntity getGoalOfCapsule(Long capsuleId) {
        if (capsuleId == null) {
            throw new IllegalArgumentException("Null reference in CapsuleServiceImpl::getGoalOfCapsule()");
        }

        CapsuleEntity capsule = repository.findById(capsuleId)
            .orElseThrow(() -> new CapsuleNotFound("Capsule with  id " + capsuleId + " was not found"));

        handleCapsuleNotOwnedByUser(capsule);

        return capsule.getGoal();
    }

    @Override
    public void removeGoalFromCapsule(Long capsuleId) {
        if (capsuleId == null) {
            throw new IllegalArgumentException("Null reference in CapsuleService::removeGoalFromCapsule");
        }

        CapsuleEntity capsule = repository.findById(capsuleId)
            .orElseThrow(() -> new CapsuleNotFound("Capsule with  id " + capsuleId + " was not found"));

        if (capsule.getGoal() == null) {
            throw new GoalNotFound("The capsule with id " + capsuleId + " has no goal");
        }

        goalService.deleteGoal(capsule.getGoal().getId());
        capsule.setGoal(null);

        repository.save(capsule);
    }

    @Override
    public void lockCapsuleById(Long id, String openDateInString) { //TODO better parsing of openDateInString
        if (id == null || openDateInString == null) {
            throw new IllegalArgumentException("Null reference in CapsuleServiceImpl::lockCapsuleById()");
        }

        CapsuleEntity capsule = repository.findById(id)
            .orElseThrow(() -> new CapsuleNotFound("Capsule with  id " + id + " was not found"));

        handleCapsuleNotOwnedByUser(capsule);

        LocalDateTime openDate = LocalDateTime.parse(openDateInString, formatter);

        capsule.lock(openDate);

        repository.save(capsule);
    }

    @Override
    public void openCapsuleIfPossible(CapsuleEntity capsule) {
        if (capsule == null) {
            throw new IllegalArgumentException("Null reference in CapsuleServiceImpl::openCapsuleIfPossible()");
        }

        handleCapsuleNotOwnedByUser(capsule);

        if (capsule.isTimeToOpen()) {
            capsule.open();
        }
    }
}

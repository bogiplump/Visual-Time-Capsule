package com.java.web.virtual.time.capsule.service.impl;

import com.java.web.virtual.time.capsule.dto.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.CapsuleResponseDto;
import com.java.web.virtual.time.capsule.dto.CapsuleUpdateDto;
import com.java.web.virtual.time.capsule.dto.MemoryDto;
import com.java.web.virtual.time.capsule.enums.CapsuleStatus;
import com.java.web.virtual.time.capsule.exception.ResourceNotFoundException;
import com.java.web.virtual.time.capsule.mapper.CapsuleMapper;
import com.java.web.virtual.time.capsule.mapper.MemoryMapper;
import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.UserModel;
import com.java.web.virtual.time.capsule.repository.CapsuleRepository;
import com.java.web.virtual.time.capsule.repository.GoalRepository;
import com.java.web.virtual.time.capsule.repository.UserRepository;
import com.java.web.virtual.time.capsule.service.CapsuleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CapsuleServiceImpl implements CapsuleService {

    private final CapsuleRepository capsuleRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository; 
    private final CapsuleMapper capsuleMapper;
    private final MemoryMapper memoryMapper;

    @Override
    @Transactional
    public CapsuleResponseDto createCapsule(CapsuleCreateDto capsuleDto, String currentUsername, Set<Long> sharedWithUserIds) {
        UserModel creator = userRepository.findByUsername(currentUsername);

        Set<UserModel> sharedUsers = new HashSet<>();
        if (sharedWithUserIds != null && !sharedWithUserIds.isEmpty()) {
            sharedWithUserIds.forEach(userId -> userRepository.findById(userId).ifPresent(sharedUsers::add));
            sharedUsers.add(creator); 
        }

        LocalDateTime openDateTime = parseInputDateTimeToUTC(capsuleDto.getOpenDateTime());

        Capsule newCapsule = capsuleMapper.toEntity(capsuleDto);
        newCapsule.setOpenDateTime(openDateTime);
        Capsule savedCapsule = capsuleRepository.save(newCapsule);

        
        if (savedCapsule.getGoal() != null) {
            savedCapsule.getGoal().setCapsule(savedCapsule);
            goalRepository.save(savedCapsule.getGoal());
        }

        return capsuleMapper.toDto(savedCapsule);
    }

    @Override
    @Transactional(readOnly = true)
    public CapsuleResponseDto getCapsuleById(Long capsuleId, String username) {
        Capsule capsule = capsuleRepository.findById(capsuleId)
            .orElseThrow(() -> new ResourceNotFoundException("Capsule not found with ID: " + capsuleId));

        UserModel currentUser = userRepository.findByUsername(username);

        boolean isCreator = capsule.getCreator().equals(currentUser);
        boolean isSharedParticipant = capsule.getIsShared() && capsule.getSharedWithUsers().contains(currentUser);

        if (!isCreator && !isSharedParticipant) {
            throw new AccessDeniedException("You do not have access to view this capsule.");
        }

        CapsuleResponseDto dto = capsuleMapper.toDto(capsule);
        if (capsule.getIsShared()) {
            dto.setTotalParticipantsCount(capsule.getSharedWithUsers().size());
            dto.setReadyParticipantsCount(capsule.getReadyToCloseUsers().size());
            dto.setIsCurrentUserReadyToClose(capsule.getReadyToCloseUsers().contains(currentUser));
        }

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<CapsuleResponseDto> getAllCapsulesOfUser(String currentUsername) {
        UserModel user = userRepository.findByUsername(currentUsername);

        Set<Capsule> createdCapsules = capsuleRepository.findByCreator_Id(user.getId());

        Set<Capsule> sharedCapsules = capsuleRepository.findBySharedWithUsersContaining(Set.of(user));

        Set<CapsuleResponseDto> allCapsules = new HashSet<>();
        allCapsules.addAll(createdCapsules.stream().map(capsuleMapper::toDto).collect(Collectors.toSet()));
        allCapsules.addAll(sharedCapsules.stream().map(capsuleMapper::toDto).collect(Collectors.toSet()));

        for (CapsuleResponseDto dto : allCapsules) {
            Optional<Capsule> originalCapsuleOpt = capsuleRepository.findById(dto.getId());
            if (originalCapsuleOpt.isPresent() && originalCapsuleOpt.get().getIsShared()) {
                Capsule originalCapsule = originalCapsuleOpt.get();
                dto.setTotalParticipantsCount(originalCapsule.getSharedWithUsers().size());
                dto.setReadyParticipantsCount(originalCapsule.getReadyToCloseUsers().size());
                dto.setIsCurrentUserReadyToClose(originalCapsule.getReadyToCloseUsers().contains(user));
            }
        }

        return allCapsules;
    }

    @Override
    @Transactional
    public CapsuleResponseDto lockCapsule(Long id, String openDateTimeInString, String lockingUsername) {
        Capsule capsule = capsuleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Capsule not found with ID: " + id));

        UserModel currentUser = userRepository.findByUsername(lockingUsername);

        capsule.addReadyUser(currentUser);

        if (!capsule.getCreator().equals(currentUser) && !capsule.getSharedWithUsers().contains(currentUser)) {
            throw new AccessDeniedException("Only the creator can lock this capsule.");
        }

        if (capsule.getStatus() != CapsuleStatus.CREATED) {
            throw new IllegalStateException("Capsule must be in 'CREATED' status to be locked.");
        }

        LocalDateTime openDateTime = parseInputDateTimeToUTC(openDateTimeInString);

        if (capsule.getIsShared()) {
            log.info("in shared capsule");
            int totalParticipants = capsule.getSharedWithUsers().size();
            int readyParticipants = capsule.getReadyToCloseUsers().size();
            double halfParticipants = (double) totalParticipants / 2.0;

            if (readyParticipants > halfParticipants) {
                capsule.setLockDate(LocalDateTime.now(ZoneOffset.UTC));
                capsule.setOpenDateTime(openDateTime);
                capsule.setStatus(CapsuleStatus.CLOSED);
            }
        } else {
            capsule.setLockDate(LocalDateTime.now(ZoneOffset.UTC));
            capsule.setOpenDateTime(openDateTime);
            capsule.setStatus(CapsuleStatus.CLOSED);
        }


        CapsuleResponseDto capsuleResponseDto = capsuleMapper.toDto(capsuleRepository.save(capsule));
        capsuleResponseDto.setReadyParticipantsCount(capsule.getReadyToCloseUsers().size());
        capsuleResponseDto.setTotalParticipantsCount(capsule.getSharedWithUsers().size());
        capsuleResponseDto.setIsCurrentUserReadyToClose(true);
        return capsuleResponseDto;
    }

    @Override
    @Transactional
    public CapsuleResponseDto updateCapsule(Long id, CapsuleUpdateDto capsuleDto, String currentUsername) {
        Capsule capsule = capsuleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Capsule not found with ID: " + id));

        UserModel currentUser = userRepository.findByUsername(currentUsername);

        if (!capsule.getCreator().equals(currentUser) && !capsule.getSharedWithUsers().contains(currentUser)){
            throw new AccessDeniedException("You do not have permission to update this capsule.");
        }

        if (capsule.getStatus() == CapsuleStatus.CLOSED || capsule.getStatus() == CapsuleStatus.OPEN) {
            throw new IllegalStateException("Cannot update a capsule that is already CLOSED or OPENED.");
        }

        Optional.ofNullable(capsuleDto.getCapsuleName())
            .ifPresent(capsule::changeCapsuleName);

        Optional.ofNullable(capsuleDto.getOpenDateTime())
            .map(this::parseInputDateTimeToUTC) 
            .ifPresent(capsule::setOpenDateTime);

        if (capsuleDto.getGoal() != null && capsule.getGoal() != null) {
            capsule.getGoal().setContent(capsuleDto.getGoal().getContent());
            capsule.getGoal().setIsAchieved(capsuleDto.getGoal().isAchieved());
            capsule.getGoal().setIsVisible(capsuleDto.getGoal().isVisible());
            goalRepository.save(capsule.getGoal()); 
        }

        if (capsule.getIsShared() && capsuleDto.getIsReadyToClose() != null) {
            if (!capsule.getSharedWithUsers().contains(currentUser)) {
                throw new AccessDeniedException("Only shared participants can mark readiness for this capsule.");
            }
            if (capsuleDto.getIsReadyToClose()) {
                capsule.addReadyUser(currentUser);
            } else {
                capsule.removeReadyUser(currentUser);
            }
        }

        return capsuleMapper.toDto(capsuleRepository.save(capsule));
    }


    @Override
    @Transactional
    public void deleteCapsule(Long id, String currentUsername) {
        Capsule capsule = capsuleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Capsule not found with ID: " + id));

        UserModel currentUser = userRepository.findByUsername(currentUsername);

        if (!capsule.getCreator().equals(currentUser)) {
            throw new AccessDeniedException("Only the creator can delete this capsule.");
        }
        capsuleRepository.delete(capsule);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<MemoryDto> getMemoriesFromCapsule(Long capsuleId, String currentUsername) {
        Capsule capsule = capsuleRepository.findById(capsuleId)
            .orElseThrow(() -> new ResourceNotFoundException("Capsule not found with ID: " + capsuleId));

        UserModel currentUser = userRepository.findByUsername(currentUsername);

        boolean isCreator = capsule.getCreator().equals(currentUser);
        boolean isSharedParticipant = capsule.getIsShared() && capsule.getSharedWithUsers().contains(currentUser);

        if (capsule.getStatus() != CapsuleStatus.OPEN && !isCreator && !isSharedParticipant) {
            throw new AccessDeniedException("You do not have access to view memories for this capsule yet.");
        }
        return capsule.getMemoryEntries().stream()
            .map(memoryMapper::toDto)
            .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public CapsuleResponseDto openCapsule(Long id, String currentUsername) {
        Capsule capsule = capsuleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Capsule not found with ID: " + id));

        UserModel currentUser = userRepository.findByUsername(currentUsername);

        if (!capsule.getCreator().equals(currentUser)) {
            throw new AccessDeniedException("Only the creator can open this capsule.");
        }

        capsule.open(); 

        return capsuleMapper.toDto(capsuleRepository.save(capsule));
    }

    @Override
    @Transactional
    public CapsuleResponseDto markReadyToClose(Long capsuleId, String currentUsername) {
        Capsule capsule = capsuleRepository.findById(capsuleId)
            .orElseThrow(() -> new ResourceNotFoundException("Capsule not found with ID: " + capsuleId));

        UserModel user = userRepository.findByUsername(currentUsername);

        if (!capsule.getIsShared() || !capsule.getSharedWithUsers().contains(user)) {
            throw new AccessDeniedException("You are not a participant of this shared capsule.");
        }

        if (capsule.getStatus() != CapsuleStatus.CREATED) {
            throw new IllegalStateException("Cannot mark readiness for a capsule not in 'CREATED' status.");
        }

        if (capsule.getReadyToCloseUsers().contains(user)) {
            log.warn("User {} already marked ready for capsule {}", currentUsername, capsuleId);
        } else {
            capsule.addReadyUser(user);
        }

        return capsuleMapper.toDto(capsuleRepository.save(capsule));
    }

    private LocalDateTime parseInputDateTimeToUTC(String dateTimeString) {
        log.info("in parseInputDateTimeToUTC: {}", dateTimeString);
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }

        try {
            if (dateTimeString.endsWith("Z") || dateTimeString.matches(".*[+-]\\d{2}:\\d{2}$")) {
                OffsetDateTime odt = OffsetDateTime.parse(dateTimeString);
                return odt.toLocalDateTime(); 
            } else {
                LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString);
                ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
                return zdt.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            }
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse '{}' as ISO Z/OffsetDateTime or default LocalDateTime. Error: {}", dateTimeString, e.getMessage());
            return null;
        }
    }
}

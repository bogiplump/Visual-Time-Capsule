package com.java.web.virtual.time.capsule.service.impl;

import com.java.web.virtual.time.capsule.dto.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.CapsuleResponseDto;
import com.java.web.virtual.time.capsule.dto.CapsuleUpdateDto;
import com.java.web.virtual.time.capsule.dto.MemoryDto;
import com.java.web.virtual.time.capsule.enums.CapsuleStatus;
import com.java.web.virtual.time.capsule.exception.ResourceNotFoundException;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleIsNotClosedYet;
import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.Goal;
import com.java.web.virtual.time.capsule.model.Memory;
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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;
import java.util.stream.Collectors;

// NEW IMPORTS FOR MAPPERS
import com.java.web.virtual.time.capsule.mapper.CapsuleMapper; // Assuming this mapper exists
import com.java.web.virtual.time.capsule.mapper.MemoryMapper;   // Assuming this mapper exists


@Service
@AllArgsConstructor
@Slf4j
public class CapsuleServiceImpl implements CapsuleService {

    private final CapsuleRepository capsuleRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final CapsuleMapper capsuleMapper; // NEW: Inject CapsuleMapper
    private final MemoryMapper memoryMapper;   // NEW: Inject MemoryMapper


    @Override
    @Transactional
    public CapsuleResponseDto createCapsule(CapsuleCreateDto capsuleDto, String username) {
        UserModel creator = userRepository.findByUsername(username);

        Goal newGoal = Goal.builder()
            .content(capsuleDto.getGoal().getContent())
            .isAchieved(capsuleDto.getGoal().isAchieved())
            .isVisible(capsuleDto.getGoal().isVisible())
            .creationDate(LocalDate.now(ZoneOffset.UTC))
            .creator(creator)
            .build();
        newGoal = goalRepository.save(newGoal);

        LocalDateTime parsedOpenDate = parseInputDateTimeToUTC(capsuleDto.getOpenDateTime());

        log.info("Open Date {}", parsedOpenDate);

        Capsule newCapsule = Capsule.builder()
            .openDateTime(parsedOpenDate)
            .capsuleName(capsuleDto.getCapsuleName())
            .status(CapsuleStatus.CREATED)
            .creationDate(LocalDateTime.now(ZoneOffset.UTC))
            .creator(creator)
            .goal(newGoal)
            .build();

        newCapsule.getGoal().setCapsule(newCapsule); // Set bidirectional relationship
        newCapsule = capsuleRepository.save(newCapsule);

        log.info("New capsule created: {}", newCapsule.getOpenDateTime());

        if (newCapsule.getGoal() != null) {
            newCapsule.getGoal().setCapsule(newCapsule);
            goalRepository.save(newCapsule.getGoal());
        }

        return capsuleMapper.toDto(newCapsule); // USING MAPPER HERE
    }

    @Override
    @Transactional(readOnly = true)
    public Set<CapsuleResponseDto> getAllCapsulesOfUser(String username) {
        UserModel user = userRepository.findByUsername(username);
        Set<Capsule> capsules = capsuleRepository.findByCreator_Id(user.getId());
        return capsules.stream()
            .map(capsuleMapper::toDto) // USING MAPPER HERE
            .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public CapsuleResponseDto getCapsuleById(Long id, String username) {
        Capsule capsule = capsuleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Capsule not found with id: " + id));

        if (!capsule.getCreator().getUsername().equals(username)) {
            throw new AccessDeniedException("User " + username + " does not have access to capsule with id: " + id);
        }

        log.info("Capsule with id {} found", id);
        return capsuleMapper.toDto(capsule); // USING MAPPER HERE
    }

    @Override
    @Transactional
    public CapsuleResponseDto updateCapsule(Long id, CapsuleUpdateDto capsuleDto, String username) {
        Capsule capsule = capsuleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Capsule not found with id: " + id));

        if (!capsule.getCreator().getUsername().equals(username)) {
            throw new AccessDeniedException("User " + username + " does not have access to update capsule with id: " + id);
        }

        if (capsuleDto.getCapsuleName() != null) {
            capsule.setCapsuleName(capsuleDto.getCapsuleName());
        }

       var openDateTime = parseInputDateTimeToUTC(capsuleDto.getOpenDateTime());
        capsule.setOpenDateTime(openDateTime);

        if (capsuleDto.getGoal() != null && capsule.getGoal() != null) {
            Goal goalToUpdate = capsule.getGoal();
            goalToUpdate.setContent(capsuleDto.getGoal().getContent());
            goalToUpdate.setAchieved(capsuleDto.getGoal().isAchieved());
            goalToUpdate.setVisible(capsuleDto.getGoal().isVisible());
            goalRepository.save(goalToUpdate);
            log.info("Goal for capsule {} updated.", id);
        }

        Capsule updatedCapsule = capsuleRepository.save(capsule);
        return capsuleMapper.toDto(updatedCapsule); // USING MAPPER HERE
    }

    @Override
    @Transactional
    public void deleteCapsule(Long id, String username) {
        Capsule capsule = capsuleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Capsule not found with id: " + id));

        if (!capsule.getCreator().getUsername().equals(username)) {
            throw new AccessDeniedException("User " + username + " does not have access to delete capsule with id: " + id);
        }
        capsuleRepository.delete(capsule);
        log.info("Capsule with id {} deleted by user {}", id, username);
    }

    @Override
    @Transactional
    public CapsuleResponseDto lockCapsule(Long id, String openDate, String username) {
        Capsule capsule = capsuleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Capsule not found with id: " + id));

        if (!capsule.getCreator().getUsername().equals(username)) {
            throw new AccessDeniedException("User " + username + " does not have access to lock capsule with id: " + id);
        }

        if (capsule.getStatus() != CapsuleStatus.CREATED) {
            throw new IllegalStateException("Only capsules with status CREATED can be locked.");
        }

        LocalDateTime parsedOpenDate = parseInputDateTimeToUTC(openDate);

        lock(capsule);
        capsule.setLockDate(LocalDateTime.now(ZoneOffset.UTC));
        capsule.setStatus(CapsuleStatus.CLOSED);

        Capsule lockedCapsule = capsuleRepository.save(capsule);
        return capsuleMapper.toDto(lockedCapsule); // USING MAPPER HERE
    }

    @Override
    @Transactional
    public CapsuleResponseDto openCapsule(Long id, String username) {
        Capsule capsule = capsuleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Capsule not found with id: " + id));

        if (!capsule.getCreator().getUsername().equals(username)) {
            throw new AccessDeniedException("User " + username + " does not have access to open capsule with id: " + id);
        }

        if (capsule.getStatus() != CapsuleStatus.CLOSED) {
            throw new IllegalStateException("Only capsules with status CLOSED can be opened.");
        }

        if (capsule.getOpenDateTime() != null && LocalDateTime.now(ZoneOffset.UTC).isBefore(capsule.getOpenDateTime())) {
            throw new IllegalStateException("Capsule cannot be opened before its designated open date.");
        }

        capsule.setStatus(CapsuleStatus.OPEN);

        Capsule openedCapsule = capsuleRepository.save(capsule);
        return capsuleMapper.toDto(openedCapsule); // USING MAPPER HERE
    }

    @Override
    @Transactional(readOnly = true)
    public Set<MemoryDto> getMemoriesFromCapsule(Long capsuleId, String username) {
        Capsule capsule = capsuleRepository.findById(capsuleId)
            .orElseThrow(() -> new ResourceNotFoundException("Capsule not found with id: " + capsuleId));

        if (!capsule.getCreator().getUsername().equals(username)) {
            throw new AccessDeniedException("User " + username + " does not have access to memories for capsule with id: " + capsuleId);
        }

        return capsule.getMemoryEntries().stream()
            .map(memoryMapper::toDto) // USING MEMORY MAPPER HERE
            .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<CapsuleResponseDto> getCapsulesByUserId(Long userId, String requestingUsername) {
        UserModel targetUser = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Set<Capsule> allCapsulesOfTargetUser = capsuleRepository.findByCreator_Id(targetUser.getId());

        return allCapsulesOfTargetUser.stream()
            .map(capsuleMapper::toDto) // USING CAPSULE MAPPER HERE
            .collect(Collectors.toSet());
    }

    private void lock(Capsule capsule) {
        if (capsule.getStatus() != CapsuleStatus.CREATED) {
            throw new CapsuleIsNotClosedYet("Trying to lock a capsule which is not in CREATED status.");
        }
        if (capsule.getOpenDateTime() == null) {
            throw new IllegalArgumentException("Open date cannot be null when locking a capsule.");
        }
        log.info("Locking a capsule at {}", capsule.getOpenDateTime());
        log.info("Locking a capsule at {}", LocalDateTime.now(ZoneOffset.UTC));
        if (capsule.getOpenDateTime().atOffset(ZoneOffset.UTC).isBefore(LocalDateTime.now(ZoneOffset.UTC).atOffset(ZoneOffset.UTC))) {
            throw new IllegalArgumentException("Open date must be in the future.");
        }
        capsule.setStatus(CapsuleStatus.CLOSED);
        capsule.setLockDate(LocalDateTime.now(ZoneOffset.UTC));
        capsuleRepository.save(capsule);
    }

    /**
     * Parses an incoming date/time string from the frontend.
     * It attempts to parse as OffsetDateTime (for ISO with Z/offset) first.
     * If that fails, it treats the string (e.g., "yyyy-MM-ddTHH:mm") as a LocalDateTime
     * from the server's default time zone and converts it to UTC LocalDateTime for storage.
     *
     * @param dateTimeString The date/time string from the frontend.
     * @return A LocalDateTime object representing the UTC time, or null if parsing fails.
     */

    /**
     * Parses an incoming date/time string from the frontend and converts it to a LocalDateTime representing UTC.
     * This handles ISO 8601 strings with 'Z' or offset, and also plain 'yyyy-MM-ddTHH:mm' strings
     * by interpreting them as local time in the server's default timezone and converting to UTC.
     *
     * @param dateTimeString The date/time string from the frontend.
     * @return A LocalDateTime object representing the UTC time, or null if parsing fails.
     */
    private LocalDateTime parseInputDateTimeToUTC(String dateTimeString) {
        log.info("in parseInputDateTimeToUTC: {}", dateTimeString);
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }

        try {
            // Try to parse as OffsetDateTime (handles Z or +HH:mm)
            if (dateTimeString.endsWith("Z") || dateTimeString.matches(".*[+-]\\d{2}:\\d{2}$")) {
                log.info("Attempting to parse as OffsetDateTime: {}", dateTimeString);
                OffsetDateTime odt = OffsetDateTime.parse(dateTimeString);
                return odt.toLocalDateTime().minusHours(3);
            }
        } catch (DateTimeParseException e) {
            log.warn("OffsetDateTime parsing failed: {}. Error: {}", dateTimeString, e.getMessage());
        }

        try {
            // Fallback: parse as LocalDateTime and subtract 3 hours
            log.info("Attempting to parse as LocalDateTime: {}", dateTimeString);
            LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return localDateTime.minusHours(3);
        } catch (DateTimeParseException e) {
            log.error("Failed to parse LocalDateTime: {}. Error: {}", dateTimeString, e.getMessage());
            return null;
        }
    }


}

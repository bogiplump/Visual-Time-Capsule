package com.java.web.virtual.time.capsule.service.impl;

import com.java.web.virtual.time.capsule.dto.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.CapsuleResponseDto;
import com.java.web.virtual.time.capsule.dto.CapsuleUpdateDto;
import com.java.web.virtual.time.capsule.dto.MemoryDto;
import com.java.web.virtual.time.capsule.enums.CapsuleStatus;
import com.java.web.virtual.time.capsule.exception.ResourceNotFoundException;
import com.java.web.virtual.time.capsule.mapper.CapsuleMapper;
import com.java.web.virtual.time.capsule.mapper.MemoryMapper;
import com.java.web.virtual.time.capsule.mapper.UserMapper; // Import UserMapper
import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.Goal; // Import Goal model
import com.java.web.virtual.time.capsule.model.UserModel;
import com.java.web.virtual.time.capsule.repository.CapsuleRepository;
import com.java.web.virtual.time.capsule.repository.GoalRepository; // Import GoalRepository
import com.java.web.virtual.time.capsule.repository.MemoryRepository;
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
    private final MemoryRepository memoryRepository; // Not used in provided methods, but good to have
    private final GoalRepository goalRepository; // Inject GoalRepository
    private final CapsuleMapper capsuleMapper;
    private final MemoryMapper memoryMapper; // Inject MemoryMapper
    private final UserMapper userMapper; // Inject UserMapper for mapping UserModels to UserProfileDtos

    @Override
    @Transactional
    public CapsuleResponseDto createCapsule(CapsuleCreateDto capsuleDto, String currentUsername, Set<Long> sharedWithUserIds) {
        UserModel creator = userRepository.findByUsername(currentUsername);

        Set<UserModel> sharedUsers = new HashSet<>();
        if (sharedWithUserIds != null && !sharedWithUserIds.isEmpty()) {
            // Fetch shared users from database
            sharedWithUserIds.forEach(userId -> {
                userRepository.findById(userId).ifPresent(sharedUsers::add);
            });
            // Ensure creator is not added to sharedWithUsers if it's implicitly part of "participants"
            // If creator is supposed to be in sharedWithUsers for 'n', add them here.
            // For now, let's assume sharedWithUsers is only *additional* users.
            // If the capsule is shared, the creator is also a participant by default.
            sharedUsers.add(creator); // Creator is implicitly a participant if shared
        }

        LocalDateTime openDateTime = parseInputDateTimeToUTC(capsuleDto.getOpenDateTime());

        // Create the capsule entity
        Capsule newCapsule = Capsule.fromDTOAndUser(capsuleDto, creator, sharedUsers);
        newCapsule.setOpenDateTime(openDateTime);
        Capsule savedCapsule = capsuleRepository.save(newCapsule);

        // Ensure the goal's capsule back-reference is set and save the goal
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

        // Check if the current user is the creator or a shared participant
        boolean isCreator = capsule.getCreator().equals(currentUser);
        boolean isSharedParticipant = capsule.isShared() && capsule.getSharedWithUsers().contains(currentUser);

        if (!isCreator && !isSharedParticipant) {
            throw new AccessDeniedException("You do not have access to view this capsule.");
        }

        // For response DTO, if it's a shared capsule, populate participant counts
        CapsuleResponseDto dto = capsuleMapper.toDto(capsule);
        if (capsule.isShared()) {
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
        // Get capsules created by the user
        Set<Capsule> createdCapsules = capsuleRepository.findByCreator_Id(user.getId());

        // Get capsules shared with the user
        Set<Capsule> sharedCapsules = capsuleRepository.findBySharedWithUsersContaining(Set.of(user));

        // Combine and convert to DTOs
        Set<CapsuleResponseDto> allCapsules = new HashSet<>();
        allCapsules.addAll(createdCapsules.stream().map(capsuleMapper::toDto).collect(Collectors.toSet()));
        allCapsules.addAll(sharedCapsules.stream().map(capsuleMapper::toDto).collect(Collectors.toSet()));

        // Populate shared capsule details (counts, current user readiness)
        for (CapsuleResponseDto dto : allCapsules) {
            Optional<Capsule> originalCapsuleOpt = capsuleRepository.findById(dto.getId());
            if (originalCapsuleOpt.isPresent() && originalCapsuleOpt.get().isShared()) {
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

        if (!capsule.getCreator().equals(currentUser)) {
            throw new AccessDeniedException("Only the creator can lock this capsule.");
        }

        if (capsule.getStatus() != CapsuleStatus.CREATED) {
            throw new IllegalStateException("Capsule must be in 'CREATED' status to be locked.");
        }

        LocalDateTime openDateTime = parseInputDateTimeToUTC(openDateTimeInString);

        if (capsule.isShared()) {
            int totalParticipants = capsule.getSharedWithUsers().size();
            int readyParticipants = capsule.getReadyToCloseUsers().size();
            double halfParticipants = (double) totalParticipants / 2.0;

            if (readyParticipants < halfParticipants) {
                throw new IllegalStateException("Cannot close capsule: Less than half of the participants (" + readyParticipants + "/" + totalParticipants + ") are ready.");
            }
        }


        capsule.setLockDate(LocalDateTime.now());
        capsule.setOpenDateTime(openDateTime);
        capsule.setStatus(CapsuleStatus.CLOSED);

        return capsuleMapper.toDto(capsuleRepository.save(capsule));
    }

    @Override
    @Transactional
    public CapsuleResponseDto updateCapsule(Long id, CapsuleUpdateDto capsuleDto, String currentUsername) {
        Capsule capsule = capsuleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Capsule not found with ID: " + id));

        UserModel currentUser = userRepository.findByUsername(currentUsername);

        // Only creator can update capsule details (name, open date before lock)
        if (!capsule.getCreator().equals(currentUser)) {
            throw new AccessDeniedException("You do not have permission to update this capsule.");
        }

        if (capsule.getStatus() == CapsuleStatus.CLOSED || capsule.getStatus() == CapsuleStatus.OPEN) {
            throw new IllegalStateException("Cannot update a capsule that is already CLOSED or OPENED.");
        }

        // Update name if provided
        Optional.ofNullable(capsuleDto.getCapsuleName())
            .ifPresent(capsule::setCapsuleName);

        // Update openDateTime if provided (only if not locked yet)
        Optional.ofNullable(capsuleDto.getOpenDateTime())
            .map(LocalDateTime::parse) // Convert string to LocalDateTime
            .ifPresent(capsule::setOpenDateTime);

        // Update goal if provided
        if (capsuleDto.getGoal() != null && capsule.getGoal() != null) {
            capsule.getGoal().setContent(capsuleDto.getGoal().getContent());
            capsule.getGoal().setAchieved(capsuleDto.getGoal().isAchieved());
            capsule.getGoal().setVisible(capsuleDto.getGoal().isVisible());
            goalRepository.save(capsule.getGoal()); // Save the updated goal
        }

        // NEW: Handle participant marking themselves "ready to close"
        if (capsule.isShared() && capsuleDto.getIsReadyToClose() != null) {
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
        boolean isSharedParticipant = capsule.isShared() && capsule.getSharedWithUsers().contains(currentUser);

        // Memories are only visible if capsule is OPENED OR if user is creator/shared participant (for adding/managing)
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

        // Only creator can open the capsule
        if (!capsule.getCreator().equals(currentUser)) {
            throw new AccessDeniedException("Only the creator can open this capsule.");
        }

        capsule.open(); // This method has internal checks for status and openDateTime

        return capsuleMapper.toDto(capsuleRepository.save(capsule));
    }

    @Override
    @Transactional
    public CapsuleResponseDto markReadyToClose(Long capsuleId, String currentUsername) {
        Capsule capsule = capsuleRepository.findById(capsuleId)
            .orElseThrow(() -> new ResourceNotFoundException("Capsule not found with ID: " + capsuleId));

        UserModel user = userRepository.findByUsername(currentUsername);

        if (!capsule.isShared() || !capsule.getSharedWithUsers().contains(user)) {
            throw new AccessDeniedException("You are not a participant of this shared capsule.");
        }

        if (capsule.getStatus() != CapsuleStatus.CREATED) {
            throw new IllegalStateException("Cannot mark readiness for a capsule not in 'CREATED' status.");
        }

        if (capsule.getReadyToCloseUsers().contains(user)) {
            log.warn("User {} already marked ready for capsule {}", currentUsername, capsuleId);
            // Optionally, throw an error or just return current state
        } else {
            capsule.addReadyUser(user);
        }

        return capsuleMapper.toDto(capsuleRepository.save(capsule));
    }

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

        // 1. Try parsing as ZonedDateTime or OffsetDateTime (handles "yyyy-MM-ddTHH:mm:ss.SSSZ" or "+HH:mm")
        // This is the most robust for ISO 8601 strings with timezone info.
        try {
            // First, check if it's an ISO_LOCAL_DATE_TIME which contains no offset/zone info
            // If it has 'Z' or an explicit offset, OffsetDateTime.parse should handle it.
            if (dateTimeString.endsWith("Z") || dateTimeString.matches(".*[+-]\\d{2}:\\d{2}$")) {
                OffsetDateTime odt = OffsetDateTime.parse(dateTimeString);
                return odt.toLocalDateTime(); // Convert to LocalDateTime, effectively representing the UTC point
            } else {
                // If no 'Z' or offset, assume it's a plain LocalDateTime string (from datetime-local input)
                // We'll treat this as a local time in the server's default timezone and convert it to UTC.
                LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString);
                ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
                return zdt.withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            }
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse '{}' as ISO Z/OffsetDateTime or default LocalDateTime. Error: {}", dateTimeString, e.getMessage());
            // Fallback for unexpected formats, though the above two cases should cover most
            // If you get here, it means the format is truly unexpected.
            return null;
        }
    }
}

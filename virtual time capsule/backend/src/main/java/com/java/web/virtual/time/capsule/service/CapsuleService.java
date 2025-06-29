package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.CapsuleResponseDto;
import com.java.web.virtual.time.capsule.dto.CapsuleUpdateDto;
import com.java.web.virtual.time.capsule.dto.MemoryDto;
import com.java.web.virtual.time.capsule.exception.ResourceNotFoundException; // Adjusted import for consistency
// Removed imports for Capsule, Goal, Memory models as they are not used in interface DTOs directly

import java.util.Set;

public interface CapsuleService {
    /**
     * Creates and saves the capsule in the database, from a data transfer object.
     *
     * @param capsuleDto must not be null.
     * @param currentUser The username of the user creating the capsule.
     * @return The created capsule as a DTO.
     * @throws IllegalArgumentException if capsuleDto is null.
     */
    CapsuleResponseDto createCapsule(CapsuleCreateDto capsuleDto, String currentUser);

    /**
     * Retrieves a single capsule by its ID for a specific user.
     *
     * @param capsuleId unique identifier of the capsule, must not be null.
     * @param username The username of the user requesting the capsule.
     * @return The capsule as a DTO.
     * @throws ResourceNotFoundException if capsule with this id does not exist.
     */
    CapsuleResponseDto getCapsuleById(Long capsuleId, String username); // Renamed from getCapsule to getCapsuleById for clarity

    /**
     * Retrieves all the capsules of the current user.
     *
     * @param currentUser The username of the current user.
     * @return An unmodifiable set of all the capsules of the user as DTOs.
     */
    Set<CapsuleResponseDto> getAllCapsulesOfUser(String currentUser);

    /**
     * Locks the capsule and its contents become unavailable until the date of the opening.
     *
     * @param id unique identifier of the capsule, must not be null.
     * @param openDateInString the date and time of the opening of the capsule
     * in string format (ISO 8601), must not be null.
     * @param lockingUsername The username of the user locking the capsule.
     * @return The updated capsule as a DTO.
     * @throws IllegalArgumentException if id or openDateInString are null or invalid.
     * @throws ResourceNotFoundException if a capsule with this id does not exist.
     * @throws IllegalStateException if the capsule is not in a 'CREATED' status.
     */
    CapsuleResponseDto lockCapsule(Long id, String openDateInString, String lockingUsername);

    /**
     * Updates and saves the capsule associated with this id in the database, from a data transfer object.
     *
     * @param id unique identifier of the capsule, must not be null.
     * @param capsuleDto must not be null. This DTO can now include a nested partial goal update.
     * @param currentUser The username of the user updating the capsule.
     * @return The updated capsule as a DTO.
     * @throws IllegalArgumentException if id and capsuleDto are null.
     * @throws ResourceNotFoundException if capsule with this id does not exist.
     */
    CapsuleResponseDto updateCapsule(Long id, CapsuleUpdateDto capsuleDto, String currentUser);

    /**
     * Finds and deletes the capsule with the same id.
     *
     * @param id unique identifier of the capsule, must not be null.
     * @param currentUser The username of the user deleting the capsule.
     * @throws IllegalArgumentException if id is null.
     * @throws ResourceNotFoundException if a capsule with this id does not exist.
     */
    void deleteCapsule(Long id, String currentUser);


    /**
     * Returns an unmodifiable set of the memories of the capsule associated with capsuleId.
     * Memories are only accessible if the capsule is opened or if the user is the creator (based on current logic).
     * @param capsuleId unique identifier of the capsule, must not be null.
     * @param currentUser The username of the user requesting memories.
     * @return an unmodifiable set of the memories of this capsule as DTOs.
     * @throws IllegalArgumentException if capsuleId is null.
     * @throws ResourceNotFoundException if a capsule with this id does not exist.
     */
    Set<MemoryDto> getMemoriesFromCapsule(Long capsuleId, String currentUser);

    // Removed getGoalForCapsule method as goal is now part of CapsuleResponseDto
    // Goal getGoalForCapsule(Long id, String currentUser);

    /**
     * Opens a capsule, changing its status to OPENED.
     *
     * @param id unique identifier of the capsule, must not be null.
     * @param currentUser The username of the user opening the capsule.
     * @return The updated capsule as a DTO.
     * @throws IllegalArgumentException if id is null.
     * @throws ResourceNotFoundException if a capsule with this id does not exist.
     * @throws IllegalStateException if the capsule is not in a 'CLOSED' status or its open date has not been reached.
     */
    CapsuleResponseDto openCapsule(Long id, String currentUser);

    Set<CapsuleResponseDto> getCapsulesByUserId(Long userId, String requestingUsername);
}

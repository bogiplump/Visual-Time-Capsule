package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.CapsuleResponseDto;
import com.java.web.virtual.time.capsule.dto.CapsuleUpdateDto;
import com.java.web.virtual.time.capsule.dto.MemoryDto;

import java.util.Set;

public interface CapsuleService {
    /**
     * Creates and saves the capsule in the database, from a data transfer object.
     *
     * @param capsuleDto must not be null.
     * @param currentUser The username of the user creating the capsule.
     * @param sharedWithUserIds Optional: IDs of users to share this capsule with.
     * @return The created capsule as a DTO.
     * @throws IllegalArgumentException if capsuleDto is null.
     */
    CapsuleResponseDto createCapsule(CapsuleCreateDto capsuleDto, String currentUser, Set<Long> sharedWithUserIds);

    /**
     * Retrieves a single capsule by its ID for a specific user.
     *
     * @param capsuleId unique identifier of the capsule, must not be null.
     * @param username The username of the user requesting the capsule.
     * @return The capsule as a DTO.
     */
    CapsuleResponseDto getCapsuleById(Long capsuleId, String username);

    /**
     * Retrieves all the capsules of the current user (created by or shared with).
     *
     * @param currentUser The username of the current user.
     * @return An unmodifiable set of all the capsules of the user as DTOs.
     */
    Set<CapsuleResponseDto> getAllCapsulesOfUser(String currentUser);

    /**
     * Locks the capsule and its contents become unavailable until the date of the opening.
     *
     * @param id unique identifier of the capsule, must not be null.
     * @param openDateTimeInString the date and time of the opening of the capsule
     * in string format (ISO 8601), must not be null.
     * @param lockingUsername The username of the user locking the capsule.
     * @return The updated capsule as a DTO.
     * @throws IllegalArgumentException if id or openDateTimeInString are null or invalid.
     * @throws IllegalStateException if the capsule is not in a 'CREATED' status, or if m < (n/2) for shared capsules.
     */
    CapsuleResponseDto lockCapsule(Long id, String openDateTimeInString, String lockingUsername);

    /**
     * Updates and saves the capsule associated with this id in the database, from a data transfer object.
     *
     * @param id unique identifier of the capsule, must not be null.
     * @param capsuleDto must not be null. This DTO can now include a nested partial goal update.
     * @param currentUser The username of the user updating the capsule.
     * @return The updated capsule as a DTO.
     * @throws IllegalArgumentException if id and capsuleDto are null.
     */
    CapsuleResponseDto updateCapsule(Long id, CapsuleUpdateDto capsuleDto, String currentUser);

    /**
     * Finds and deletes the capsule with the same id.
     *
     * @param id unique identifier of the capsule, must not be null.
     * @param currentUser The username of the user deleting the capsule.
     * @throws IllegalArgumentException if id is null.
     */
    void deleteCapsule(Long id, String currentUser);

    /**
     * Returns an unmodifiable set of the memories of the capsule associated with capsuleId.
     * Memories are only accessible if the capsule is opened or if the user is the creator or a shared participant.
     * @param capsuleId unique identifier of the capsule, must not be null.
     * @param currentUser The username of the user requesting memories.
     * @return an unmodifiable set of the memories of this capsule as DTOs.
     * @throws IllegalArgumentException if capsuleId is null.
     */
    Set<MemoryDto> getMemoriesFromCapsule(Long capsuleId, String currentUser);

    /**
     * Opens a capsule, changing its status to OPENED.
     *
     * @param id unique identifier of the capsule, must not be null.
     * @param currentUser The username of the user opening the capsule.
     * @return The updated capsule as a DTO.
     * @throws IllegalArgumentException if id is null.
     * @throws IllegalStateException if the capsule is not in a 'CLOSED' status or its open date has not been reached.
     */
    CapsuleResponseDto openCapsule(Long id, String currentUser);

    /**
     * Marks a user as "ready to close" for a specific shared capsule.
     *
     * @param capsuleId The ID of the capsule.
     * @param username The username of the user marking themselves ready.
     * @return The updated capsule DTO.
     * @throws IllegalStateException if capsule is not in CREATED status.
     */
    CapsuleResponseDto markReadyToClose(Long capsuleId, String username);
}

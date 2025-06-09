package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.SharedCapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.SharedCapsuleResponseDto;

public interface SharedCapsuleService {
    /**
     * Creates and saves to the database the shared capsule. It's creator and only member is the current user.
     * Returns the id with which the capsule was saved in the database.
     *
     * @param sharedCapsuleCreateDto
     * @param currentUser
     * @return the id of the capsule.
     */
    Long createSharedCapsule(SharedCapsuleCreateDto sharedCapsuleCreateDto, String currentUser);

    /**
     * Retrieves the shared capsule from the database and the returns it as a response data transfer object.
     *
     * @param id the unique identifier of the shared capsule, must not be null.
     * @param currentUser the username of the current user, must not be null.
     * @return a shared capsule response data transfer object.
     */
    SharedCapsuleResponseDto getSharedCapsuleById(Long id, String currentUser);

    /**
     * Adds a user to the shared capsule.
     * Only possible before locking the capsule.
     *
     * @param capsuleId the unique identifier of the shared capsule, must not be null.
     * @param userId the unique identifier of the user who needs to be added, must not be null.
     * @param currentUser the username of the current user, must not be null.
     */
    void addUserToCapsule(Long capsuleId, Long userId, String currentUser);

    /**
     * Removes a user from the shared capsule along with the memories he has added.
     * Only possible before locking the capsule.
     *
     * @param capsuleId the unique identifier of the shared capsule, must not be null.
     * @param userId the unique identifier of the user who needs to be removed, must not be null.
     * @param currentUser the username of the current user, must not be null.
     */
    void removeUserFromCapsule(Long capsuleId, Long userId, String currentUser);

    /**
     * Deletes the shared capsule associated with this id.
     *
     * @param id the unique identifier of the shared capsule, must not be null.
     * @param currentUser the username of the current user, must not be null.
     */
    void deleteSharedCapsuleById(Long id, String currentUser);
}

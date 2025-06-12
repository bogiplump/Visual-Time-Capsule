package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.sharedcapsule.SharedCapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.sharedcapsule.SharedCapsuleResponseDto;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleNotOwnedByYou;
import com.java.web.virtual.time.capsule.exception.sharedcapsuele.IsNotSharedCapsule;
import com.java.web.virtual.time.capsule.exception.sharedcapsuele.UserAlreadyInCapsule;
import com.java.web.virtual.time.capsule.exception.sharedcapsuele.UserNotInCapsule;

public interface SharedCapsuleService {
    /**
     * Creates and saves to the database the shared capsule. It's creator and only member is the current user.
     * Returns the id with which the capsule was saved in the database.
     *
     * @param sharedCapsuleDto the data transfer object for creation, not null.
     * @param currentUser the username of the current user, must not be null.
     * @return the id of the capsule.
     */
    Long createSharedCapsule(SharedCapsuleCreateDto sharedCapsuleDto, String currentUser);

    /**
     * Retrieves the shared capsule from the database and the returns it as a response data transfer object.
     *
     * @param id the unique identifier of the shared capsule, must not be null.
     * @param currentUser the username of the current user, must not be null.
     * @return a shared capsule response data transfer object.
     *
     * @throws IsNotSharedCapsule if capsule is not a shared capsule.
     * @throws UserNotInCapsule  if the current user is not in the capsule.
     */
    SharedCapsuleResponseDto getSharedCapsuleById(Long id, String currentUser);

    /**
     * Adds a user to the shared capsule.
     * Only possible before locking the capsule.
     *
     * @param capsuleId the unique identifier of the shared capsule, must not be null.
     * @param userId the unique identifier of the user who needs to be added, must not be null.
     * @param currentUser the username of the current user, must not be null.
     *
     * @throws IsNotSharedCapsule if capsule is not a shared capsule.
     * @throws CapsuleNotOwnedByYou  if capsule is not created by you.
     * @throws UserAlreadyInCapsule if user is already in the capsule.
     */
    void addUserToCapsule(Long capsuleId, Long userId, String currentUser) throws UserAlreadyInCapsule;

    /**
     * Removes a user from the shared capsule along with the memories he has added.
     * Only possible before locking the capsule. Only the creator of the capsule can remove a participant.
     *
     * @param capsuleId the unique identifier of the shared capsule, must not be null.
     * @param userId the unique identifier of the user who needs to be removed, must not be null.
     * @param currentUser the username of the current user, must not be null.
     *
     * @throws IsNotSharedCapsule if capsule is not a shared capsule.
     * @throws CapsuleNotOwnedByYou  if capsule is not created by you.
     * @throws UserNotInCapsule if user is not in the capsule.
     */
    void removeUserFromCapsule(Long capsuleId, Long userId, String currentUser);

    /**
     * Deletes the shared capsule associated with this id. Only the creator can delete the capsule.
     *
     * @param id the unique identifier of the shared capsule, must not be null.
     * @param currentUser the username of the current user, must not be null.
     *
     * @throws IsNotSharedCapsule if capsule is not a shared capsule.
     * @throws CapsuleNotOwnedByYou  if capsule is not created by you.
     */
    void deleteSharedCapsuleById(Long id, String currentUser);
}

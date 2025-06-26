package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.capsule.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.capsule.CapsulePreviewDto;
import com.java.web.virtual.time.capsule.dto.capsule.CapsuleResponseDto;
import com.java.web.virtual.time.capsule.dto.capsule.CapsuleUpdateDto;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleHasBeenLocked;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleNotFound;
import com.java.web.virtual.time.capsule.model.Goal;
import com.java.web.virtual.time.capsule.model.Memory;

import java.util.Set;

public interface CapsuleService {
    /**
     * Creates and saves the capsule in the database, from a data transfer object.
     *
     * @param capsuleDto must not be null.
     * @throws IllegalArgumentException if capsuleDto is null.
     */
    Long createCapsule(CapsuleCreateDto capsuleDto, String currentUser);

    /**
     * Retrieves a capsule from the database by its unique identifier and returns a preview data transfer object.
     *
     * @param id the unique identifier of the capsule, must not be null.
     * @return the Capsule with the specified ID, as a preview data transfer object, or null if not found. TODO Check this here
     * @throws IllegalArgumentException if id is null.
     * @throws CapsuleNotFound if a capsule with this id does not exist.
     */
    CapsulePreviewDto getCapsulePreview(Long id, String currentUser);

    /**
     * Retrieves a Capsule from the database by its unique identifier and returns a response data transfer object.
     *
     * @param id the unique identifier of the capsule, must not be null.
     * @return the Capsule with the specified ID, as response data transport objects, or null if not found. TODO Check this here
     * @throws IllegalArgumentException if id is null.
     * @throws CapsuleNotFound if a capsule with this id does not exist.
     */
    CapsuleResponseDto getCapsule(Long id, String currentUser);

    /**
     * Retrieves of all the capsules of the current user.
     *
     * @return unmodifiable set of all the capsules of the user, as response data transport objects.
     */
    Set<CapsuleResponseDto> getAllCapsulesOfUser(String currentUser);

    /**
     * Locks the capsule and its contents become unavailable until the date of the opening.
     *
     * @param id unique identifier of the capsule, must not be null.
     * @param openDateInString the date and time of the opening of the capsule
     *                         in string format, must not be null.
     *
     * @throws IllegalArgumentException if id or goal are null.
     * @throws CapsuleNotFound if a capsule with this id does not exist.
     * @throws CapsuleHasBeenLocked if the capsule has already been locked.
     */
    void lockCapsule(Long id, String openDateInString, String lockingUsername);

    /**
     * Updates and saves the capsule associated with this id in the database, from a data transfer object.
     *
     * @param id unique identifier of the capsule, must not be null.
     * @param capsuleDto must not be null.
     *
     * @throws IllegalArgumentException if id and capsuleDto are null.
     * @throws CapsuleNotFound if capsule with this id does not exist.
     */
    void updateCapsule(Long id, CapsuleUpdateDto capsuleDto, String currentUser);

    /**
     * Finds and deletes the capsule with the same id.
     *
     * @param id unique identifier of the capsule, must not be null.
     * @throws IllegalArgumentException if id is null.
     * @throws CapsuleNotFound if a capsule with this id does not exist.
     */
    void deleteCapsule(Long id, String currentUser);


    /**
     * Returns an unmodifiable set of the memories of the capsule associated with capsuleId.
     * @param capsuleId unique identifier of the capsule, must not be null.
     * @return an unmodifiable set of the memories of this capsule
     *
     * @throws IllegalArgumentException if capsuleId or memoryId are null.
     * @throws CapsuleNotFound if a capsule with this id does not exist.
     * @throws CapsuleHasBeenLocked if the capsule is locked and memories are inaccessible.
     */
    Set<Memory> getMemoriesFromCapsule(Long capsuleId, String currentUser);

    Goal getGoalForCapsule(Long id, String currentUser);
}

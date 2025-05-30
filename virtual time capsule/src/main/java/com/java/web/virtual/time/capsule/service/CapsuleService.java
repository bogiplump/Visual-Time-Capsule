package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.CapsuleUpdateDto;
import com.java.web.virtual.time.capsule.dto.MemoryCreateDto;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleHasBeenLocked;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleIsNotClosedYet;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleNotFound;
import com.java.web.virtual.time.capsule.exception.goal.GoalNotFound;
import com.java.web.virtual.time.capsule.exception.memory.MemoryNotFound;
import com.java.web.virtual.time.capsule.exception.memory.MemoryNotInBank;
import com.java.web.virtual.time.capsule.exception.memory.MemoryNotInCapsule;
import com.java.web.virtual.time.capsule.model.entity.CapsuleEntity;
import com.java.web.virtual.time.capsule.model.entity.GoalEntity;
import com.java.web.virtual.time.capsule.model.entity.MemoryEntity;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface CapsuleService {
    /**
     * Retrieves a CapsuleEntity from the database by its unique identifier.
     *
     * @param id the unique identifier of the capsule, must not be null.
     * @return the CapsuleEntity with the specified ID, or null if not found.
     * @throws IllegalArgumentException if id is null.
     * @throws CapsuleNotFound if a capsule with this id does not exist.
     */
    CapsuleEntity getCapsuleById(Long id);

    /**
     * Retrieves of all the capsules of the current user.
     *
     * @return unmodifiable set of all the capsules of the user.
     */
    Set<CapsuleEntity> getAllCapsulesOfUser();

    /**
     * Parses capsule data transfer object to CapsuleEntity.
     *
     * @param capsuleDto must not be null
     * @return the GoalEntity object.
     *
     * @throws IllegalArgumentException if capsuleDto is null.
     */
    CapsuleEntity parseCapsuleCreateDto(CapsuleCreateDto capsuleDto);

    /**
     * Creates and saves the capsule in the database, from a data transfer object.
     *
     * @param capsuleDto must not be null.
     * @throws IllegalArgumentException if capsuleDto is null.
     */
    void createCapsule(CapsuleCreateDto capsuleDto);

    /**
     * Updates and saves the capsule associated with this id in the database, from a data transfer object.
     *
     * @param id unique identifier of the capsule, must not be null.
     * @param capsuleDto must not be null.
     *
     * @throws IllegalArgumentException if id and capsuleDto are null.
     * @throws CapsuleNotFound if capsule with this id does not exist.
     */
    void updateCapsule(Long id, CapsuleUpdateDto capsuleDto);

    /**
     * Finds and deletes the capsule with the same id.
     *
     * @param id unique identifier of the capsule, must not be null.
     * @throws IllegalArgumentException if id is null.
     * @throws CapsuleNotFound if a capsule with this id does not exist.
     */
    void deleteCapsuleById(Long id);

    /**
     * Adds a new memory to the database associated with a certain capsule.
     *
     * @param capsuleId unique identifier of the capsule, must not be null.
     * @param memoryDto the memory object to add, must not be null.
     *
     * @throws IllegalArgumentException if capsuleId or memoryDto are null.
     * @throws CapsuleNotFound if a capsule with this id does not exist.
     */
    void addMemoryToCapsule(Long capsuleId, MemoryCreateDto memoryDto);

    /**
     * Puts a memory from the users profile into a certain capsule.
     *
     * @param capsuleId unique identifier of the capsule, must not be null.
     * @param memoryId unique identifier of the memory, must not be null.
     *
     * @throws IllegalArgumentException if capsuleId or memoryId are null.
     * @throws CapsuleNotFound if a capsule with this id does not exist.
     * @throws MemoryNotFound if memory with this id does not exist.
     * @throws MemoryNotInBank if the id is not for a memory from the users memory bank.
     */
    void putMemoryInCapsule(Long capsuleId, Long memoryId);

    /**
     * Removes the memory with memoryId from the capsule with capsuleId and deletes it.
     *
     * @param capsuleId unique identifier of the capsule, must not be null.
     * @param memoryId unique identifier of the memory, must not be null.
     *
     * @throws IllegalArgumentException if capsuleId or memoryId are null.
     * @throws CapsuleNotFound if a capsule with this id does not exist.
     * @throws MemoryNotFound if memory with this id does not exist.
     */
    void removeMemoryFromCapsule(Long capsuleId, Long memoryId);

    /**
     *
     * @param capsuleId unique identifier of the capsule, must not be null.
     * @param memoryId unique identifier of the memory, must not be null.
     * @return the MemoryEntity associated with this id which is inside the capsule with capsuleId.
     *
     * @throws IllegalArgumentException if capsuleId or memoryId are null.
     * @throws CapsuleNotFound if a capsule with this id does not exist.
     * @throws MemoryNotFound if memory with this id does not exist.
     * @throws MemoryNotInCapsule if memory with this id exits but is not in this capsule.
     * @throws CapsuleHasBeenLocked if the capsule is locked and memories are inaccessible.
     */
    MemoryEntity getMemoryFromCapsule(Long capsuleId, Long memoryId);

    /**
     * Returns an unmodifiable set of the memories of the capsule associated with capsuleId.
     * @param capsuleId unique identifier of the capsule, must not be null.
     * @return an unmodifiable set of the memories of this capsule
     *
     * @throws IllegalArgumentException if capsuleId or memoryId are null.
     * @throws CapsuleNotFound if a capsule with this id does not exist.
     * @throws CapsuleHasBeenLocked if the capsule is locked and memories are inaccessible.
     */
    Set<MemoryEntity> getMemoriesFromCapsule(Long capsuleId);

    /**
     * Adds a goal to a certain capsule.
     *
     * @param capsuleId unique identifier of the capsule, must not be null.
     * @param goal the GoalEntity to add to the capsule.
     *
     * @throws IllegalArgumentException if capsuleId or goal are null.
     * @throws CapsuleNotFound if a capsule with this id does not exist.
     */
    void addGoalToCapsule(Long capsuleId, GoalEntity goal);

    /**
     * Retrieves the goal of a certain capsule from the database.
     *
     * @param capsuleId unique identifier of the capsule, must not be null.
     * @return the GoalEntity of this capsule.
     *
     * @throws IllegalArgumentException if capsuleId or goal are null.
     * @throws CapsuleNotFound if a capsule with this id does not exist.
     */
    GoalEntity getGoalOfCapsule(Long capsuleId);

    /**
     * Removes a goal from a certain capsule and deletes it from the database.
     *
     * @param capsuleId unique identifier of the capsule, must not be null.
     *
     * @throws IllegalArgumentException if capsuleId or goal are null.
     * @throws CapsuleNotFound if a capsule with this id does not exist.
     * @throws GoalNotFound if the capsule has no goal associated with it.
     */
    void removeGoalFromCapsule(Long capsuleId);

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
    void lockCapsuleById(Long id, String openDateInString);

    /**
     * Checks if the date for opening of the capsule which has been set at the moment of closing
     * has passed. If so it moves the capsule to state - opened, otherwise nothing happens.
     *
     * @param capsule a reference to the capsule object, must not be null.
     *
     * @throws IllegalArgumentException if id or goal are null.
     * @throws CapsuleNotFound if a capsule with this id does not exist.
     * @throws CapsuleIsNotClosedYet if the capsule is not in stated - locked.
     */
    void openCapsuleIfPossible(CapsuleEntity capsule);
}

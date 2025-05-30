package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.MemoryCreateDto;
import com.java.web.virtual.time.capsule.exception.memory.MemoryNotFound;
import com.java.web.virtual.time.capsule.model.entity.MemoryEntity;
import org.springframework.stereotype.Service;

@Service
public interface MemoryService {
    /**
     * Retrieves a MemoryEntity from the database by its unique identifier.
     *
     * @param id the unique identifier of the memory, must not be null.
     * @return the MemoryEntity with the specified ID, or null if not found.
     *
     * @throws IllegalArgumentException if id is null.
     * @throws MemoryNotFound if a memory with this id does not exist.
     */
    MemoryEntity getMemoryById(Long id);

    /**
     * Saves the memory in the database.
     *
     * @param memory must not be null.
     * @throws IllegalArgumentException if memory is null.
     */
    void saveMemory(MemoryEntity memory);

    /**
     * Creates and saves the memory in the database, from a data transfer object.
     *
     * @param memoryDto must not be null.
     *
     * @throws IllegalArgumentException if memoryDto is null.
     */
    void createMemory(MemoryCreateDto memoryDto);

    /**
     * Finds and deletes the memory with the same id.
     *
     * @param id unique identifier of the memory, must not be null.
     * @throws IllegalArgumentException if id is null.
     * @throws MemoryNotFound if a capsule with this id does not exist.
     */
    void deleteMemoryById(Long id);

    /**
     * Retrieves a MemoryEntity from the database and returns if the memory is in this capsule.
     *
     * @param memoryId unique identifier of the memory, must not be null.
     * @param capsuleId unique identifier of the capsule, must not be null.
     * @return if the memory is in this capsule.
     *
     * @throws IllegalArgumentException if memoryId or capsuleId are null.
     */
    boolean isMemoryInCapsule(Long memoryId, Long capsuleId);

    /**
     * Parses memory data transfer object to MemoryEntity.
     *
     * @param memoryDto must not be null.
     * @return the MemoryEntity object.
     *
     * @throws IllegalArgumentException if memoryDto is null.
     */
    MemoryEntity parseMemoryDto(MemoryCreateDto memoryDto);
}

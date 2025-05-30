package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.MemoryCreateDto;
import com.java.web.virtual.time.capsule.exception.memory.MemoryNotFound;
import com.java.web.virtual.time.capsule.model.entity.MemoryEntity;

public interface MemoryService {
    /**
     * Retrieves a MemoryEntity from the database by its unique identifier.
     *
     * @param id the unique identifier of the memory, must not be null.
     * @return the MemoryEntity with the specified ID, or null if not found.
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
     * @throws IllegalArgumentException if memoryDto is null.
     */
    void createMemory(MemoryCreateDto memoryDto);

    /**
     * Parses memory data transfer object to MemoryEntity.
     *
     * @param memoryDto must not be null.
     * @throws IllegalArgumentException if memoryDto is null.
     */
    MemoryEntity parseMemoryDto(MemoryCreateDto memoryDto);
}

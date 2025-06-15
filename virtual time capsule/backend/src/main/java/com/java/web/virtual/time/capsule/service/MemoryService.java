package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.MemoryCreateDto;
import com.java.web.virtual.time.capsule.exception.memory.MemoryNotFound;
import com.java.web.virtual.time.capsule.exception.memory.MemoryNotOwnedByYou;
import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.Memory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface MemoryService {
    Memory getMemoryById(Long id);

    Long saveMemory(Long capsuleId, MemoryCreateDto memoryDto, String currentUser)  throws IOException;

    Long addExistingMemoryToCapsule(Long capsuleId, Long id, String currentUser);

    void deleteMemory(Long capsuleId, Long id, String currentUser);
}

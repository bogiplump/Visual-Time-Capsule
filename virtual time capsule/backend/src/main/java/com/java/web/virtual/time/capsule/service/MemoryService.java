package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.MemoryCreateDto;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface MemoryService {
    Long saveMemory(Long capsuleId, MemoryCreateDto memoryDto, String currentUser)  throws IOException;

    Long addMemoryToCapsule(Long capsuleId, Long id, String currentUser);

    void deleteMemory(Long capsuleId, Long id, String currentUser);
}

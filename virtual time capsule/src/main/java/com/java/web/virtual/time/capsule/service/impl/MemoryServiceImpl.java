package com.java.web.virtual.time.capsule.service.impl;

import com.java.web.virtual.time.capsule.dto.memory.MemoryCreateDto;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleNotFound;
import com.java.web.virtual.time.capsule.exception.memory.MemoryNotFound;
import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.Memory;
import com.java.web.virtual.time.capsule.model.User;
import com.java.web.virtual.time.capsule.repository.CapsuleRepository;
import com.java.web.virtual.time.capsule.repository.MemoryRepository;
import com.java.web.virtual.time.capsule.repository.UserRepository;
import com.java.web.virtual.time.capsule.service.MemoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import static com.java.web.virtual.time.capsule.service.impl.CapsuleServiceImpl.handleCapsuleNotOwnedByUser;

@Service
@AllArgsConstructor
public class MemoryServiceImpl implements MemoryService {

    private final MemoryRepository memoryRepository;
    private final CapsuleRepository capsuleRepository;
    private final UserRepository userRepository;

    @Override
    public Memory getMemoryById(Long id) {
        return memoryRepository.findById(id).orElseThrow(() -> new MemoryNotFound("Memory Not Found"));
    }

    @Override
    public  Long saveMemory(Long capsuleId, MemoryCreateDto memoryDto, String currentUser)  throws IOException {
        User creator = userRepository.findByUsername(currentUser);

        Capsule capsule = capsuleRepository.findById(capsuleId)
            .orElseThrow(() -> new CapsuleNotFound("Capsule with  id " + capsuleId + " was not found"));

        handleCapsuleNotOwnedByUser(capsule,creator.getId());

        String uploadDir = "uploads/";
        String filePath = UUID.randomUUID() + "_" +  memoryDto.getName();

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        memoryDto.getContent().transferTo(new File(filePath));

        Memory memory = Memory.builder()
            .memoryType(memoryDto.getType())
            .creationDate(LocalDate.now())
            .path(filePath)
            .creator(creator)
            .description(memoryDto.getDescription())
            .build();

        capsule.addMemory(memory);

        return memoryRepository.save(memory).getId();
    }

    @Override
    public Long addExistingMemoryToCapsule(Long capsuleId, Long id, String currentUser) {
        Memory memory = memoryRepository.findById(capsuleId).orElseThrow(() -> new MemoryNotFound("Memory Not Found"));
        Capsule capsule = capsuleRepository.findById(capsuleId).orElseThrow(() -> new CapsuleNotFound("Capsule with  id " + id + " was not found"));

        User user = userRepository.findByUsername(currentUser);

        handleCapsuleNotOwnedByUser(capsule,user.getId());

        capsule.addMemory(memory);
        capsuleRepository.save(capsule);
        return memoryRepository.save(memory).getId();
    }

    @Override
    public void deleteMemory(Long capsuleId, Long id, String currentUser) {
        Memory memory = memoryRepository.findById(capsuleId).orElseThrow(() -> new MemoryNotFound("Memory Not Found"));
        Capsule capsule = capsuleRepository.findById(capsuleId).orElseThrow(() -> new CapsuleNotFound("Capsule with  id " + id + " was not found"));

        User user = userRepository.findByUsername(currentUser);

        handleCapsuleNotOwnedByUser(capsule,user.getId());

        capsule.removeMemory(memory);
        capsuleRepository.save(capsule);
    }
}

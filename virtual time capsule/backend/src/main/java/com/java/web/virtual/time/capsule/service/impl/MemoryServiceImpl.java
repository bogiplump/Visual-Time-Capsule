package com.java.web.virtual.time.capsule.service.impl;

import com.java.web.virtual.time.capsule.dto.MemoryCreateDto;
import com.java.web.virtual.time.capsule.enums.CapsuleStatus;
import com.java.web.virtual.time.capsule.exception.ResourceNotFoundException;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleHasBeenLocked;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleIsOpened;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleNotFound;
import com.java.web.virtual.time.capsule.exception.memory.MemoryNotFound;
import com.java.web.virtual.time.capsule.exception.user.UserNotFoundException;
import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.Memory;
import com.java.web.virtual.time.capsule.model.UserModel;
import com.java.web.virtual.time.capsule.repository.CapsuleRepository;
import com.java.web.virtual.time.capsule.repository.MemoryRepository;
import com.java.web.virtual.time.capsule.repository.UserRepository;
import com.java.web.virtual.time.capsule.service.MemoryService;

import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class MemoryServiceImpl implements MemoryService {

    @Value("${timecapsule.upload.dir:/app/uploads}")
    private String uploadDirectory; 

    private final MemoryRepository memoryRepository;
    private final CapsuleRepository capsuleRepository;
    private final UserRepository userRepository;

    
    @Autowired 
    public MemoryServiceImpl(MemoryRepository memoryRepository, CapsuleRepository capsuleRepository, UserRepository userRepository) {
        this.memoryRepository = memoryRepository;
        this.capsuleRepository = capsuleRepository;
        this.userRepository = userRepository;
        
    }

    @Override
    @Transactional
    public Long saveMemory(Long capsuleId, MemoryCreateDto memoryDto, String username) throws IOException {
        try {
            Path uploadPath = Paths.get(uploadDirectory).toAbsolutePath().normalize();

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created upload directory: {}", uploadPath);
            }

            Capsule capsule = capsuleRepository.findById(capsuleId)
                .orElseThrow(() -> new ResourceNotFoundException("Capsule not found with id: " + capsuleId));

            
            UserModel creator = userRepository.findByUsername(username);
            if(creator == null) {
                throw new UserNotFoundException("User not found with name: " + username);
            }
            log.info("Saving capsule: {}", capsule.toString());
            if (creator.equals(capsule.getCreator()) && !capsule.getSharedWithUsers().contains(creator)) {
                throw new IllegalArgumentException("User are not authorized to add memories");
            }

            
            if (capsule.getStatus() == CapsuleStatus.CLOSED) {
                throw new CapsuleHasBeenLocked("Cannot add memories to a locked capsule.");
            }
            if (capsule.getStatus() == CapsuleStatus.OPEN) {
                throw new CapsuleIsOpened("Cannot add memories to an opened capsule.");
            }

            
            MultipartFile file = memoryDto.getContent();
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Memory content file cannot be empty.");
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFilename).normalize();

            try {
                file.transferTo(filePath);
                log.info("File saved to: {}", filePath);
            } catch (IOException e) {
                log.error("Failed to save file for memory: {}", originalFilename, e);
                throw new IOException("Failed to store file " + originalFilename, e);
            }
            
            Memory newMemory = Memory.builder()
                .description(memoryDto.getDescription())
                .memoryType(memoryDto.getType())
                .path(uniqueFilename)
                .creationDate(LocalDate.now())
                .creator(creator)
                .capsule(capsule)
                .build();
            
            capsule.getMemoryEntries().add(newMemory);

            memoryRepository.save(newMemory);

            log.info("Memory saved with ID: {}", newMemory.getId());
            return newMemory.getId();

        } catch (Exception e) { 
            log.error("Error saving memory for capsuleId: {} by user: {}", capsuleId, username, e); 
            throw e; 
        }
    }

    @Override
    @Transactional
    public Long addMemoryToCapsule(Long capsuleId, Long id, String currentUser) {
        Memory memory = memoryRepository.findById(id).orElseThrow(() -> new MemoryNotFound("Memory Not Found"));
        Capsule capsule = capsuleRepository.findById(capsuleId).orElseThrow(() -> new CapsuleNotFound("Capsule with id " + capsuleId + " was not found"));

        capsule.addMemory(memory);
        capsuleRepository.save(capsule);
        return memoryRepository.save(memory).getId();
    }

    @Override
    @Transactional
    public void deleteMemory(Long capsuleId, Long id, String currentUser) {
        Memory memory = memoryRepository.findById(id).orElseThrow(() -> new MemoryNotFound("Memory Not Found with id: " + id));
        Capsule capsule = capsuleRepository.findById(capsuleId).orElseThrow(() -> new CapsuleNotFound("Capsule with id " + capsuleId + " was not found"));

        capsule.removeMemory(memory);
        capsuleRepository.save(capsule);
    }
}
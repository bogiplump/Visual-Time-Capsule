package com.java.web.virtual.time.capsule.service.impl;

import com.java.web.virtual.time.capsule.dto.MemoryCreateDto;
import com.java.web.virtual.time.capsule.enums.CapsuleStatus;
import com.java.web.virtual.time.capsule.exception.ResourceNotFoundException;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleHasBeenLocked;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleIsOpened;
import com.java.web.virtual.time.capsule.exception.capsule.CapsuleNotFound;
import com.java.web.virtual.time.capsule.exception.memory.MemoryNotFound;
import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.Memory;
import com.java.web.virtual.time.capsule.model.UserModel;
import com.java.web.virtual.time.capsule.repository.CapsuleRepository;
import com.java.web.virtual.time.capsule.repository.MemoryRepository;
import com.java.web.virtual.time.capsule.repository.UserRepository;
import com.java.web.virtual.time.capsule.service.MemoryService;

import jakarta.transaction.Transactional;
// import lombok.AllArgsConstructor; // REMOVE THIS LINE
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired; // IMPORTANT: Add this import
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
    private String uploadDirectory; // Keep as non-static, non-final

    private final MemoryRepository memoryRepository;
    private final CapsuleRepository capsuleRepository;
    private final UserRepository userRepository;

    // EXPLICIT CONSTRUCTOR FOR DEPENDENCY INJECTION
    @Autowired // Spring will use this constructor for dependency injection
    public MemoryServiceImpl(MemoryRepository memoryRepository, CapsuleRepository capsuleRepository, UserRepository userRepository) {
        this.memoryRepository = memoryRepository;
        this.capsuleRepository = capsuleRepository;
        this.userRepository = userRepository;
        // 'uploadDirectory' will be set by @Value after this constructor runs
    }

    @Override
    public Memory getMemoryById(Long id) {
        return memoryRepository.findById(id).orElseThrow(() -> new MemoryNotFound("Memory Not Found"));
    }

    @Override
    @Transactional // Keep this transactional!
    public Long saveMemory(Long capsuleId, MemoryCreateDto memoryDto, String username) throws IOException {
        try { // ADD THIS TRY BLOCK
            // Ensure the upload directory exists
            Path uploadPath = Paths.get(uploadDirectory).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created upload directory: {}", uploadPath);
            }

            // 1. Fetch the Capsule entity within the current transaction
            Capsule capsule = capsuleRepository.findById(capsuleId)
                .orElseThrow(() -> new ResourceNotFoundException("Capsule not found with id: " + capsuleId));

            // 2. Verify ownership
            UserModel creator = userRepository.findByUsername(username);
            if (creator == null || !Objects.equals(capsule.getCreator().getId(), creator.getId())) {
                throw new IllegalArgumentException("User is not the owner of this capsule or user not found.");
            }

            // 3. Apply business rules based on capsule status BEFORE adding memory
            if (capsule.getStatus() == CapsuleStatus.CLOSED) {
                throw new CapsuleHasBeenLocked("Cannot add memories to a locked capsule.");
            }
            if (capsule.getStatus() == CapsuleStatus.OPEN) {
                throw new CapsuleIsOpened("Cannot add memories to an opened capsule.");
            }

            // 4. Handle file content - Save the MultipartFile to server
            MultipartFile file = memoryDto.getContent();
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Memory content file cannot be empty.");
            }

            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            Path filePath = uploadPath.resolve(uniqueFilename).normalize();

            try {
                file.transferTo(filePath);
                log.info("File saved to: {}", filePath.toString());
            } catch (IOException e) {
                log.error("Failed to save file for memory: {}", originalFilename, e);
                throw new IOException("Failed to store file " + originalFilename, e);
            }

            // 5. Create the new Memory entity
            Memory newMemory = Memory.builder()
                .description(memoryDto.getDescription())
                .memoryType(memoryDto.getType())
                .path(uniqueFilename)
                .creationDate(LocalDate.now())
                .creator(creator)
                .capsule(capsule)
                .build();

            // 6. Add the memory to the capsule's managed collection
            // This is the critical part for CME issues
            capsule.getMemoryEntries().add(newMemory);

            // 7. Save the new memory
            memoryRepository.save(newMemory);

            log.info("Memory saved with ID: {}", newMemory.getId());
            return newMemory.getId();

        } catch (Exception e) { // CATCH ANY EXCEPTION
            log.error("Error saving memory for capsuleId: {} by user: {}", capsuleId, username, e); // LOG THE FULL STACK TRACE
            throw e; // Re-throw to propagate the 500 to the client, but now it's logged
        }
    }

    @Override
    @Transactional
    public Long addExistingMemoryToCapsule(Long capsuleId, Long id, String currentUser) {
        Memory memory = memoryRepository.findById(id).orElseThrow(() -> new MemoryNotFound("Memory Not Found"));
        Capsule capsule = capsuleRepository.findById(capsuleId).orElseThrow(() -> new CapsuleNotFound("Capsule with id " + capsuleId + " was not found"));

        UserModel user = userRepository.findByUsername(currentUser);


        capsule.addMemory(memory);
        capsuleRepository.save(capsule);
        return memoryRepository.save(memory).getId();
    }

    @Override
    @Transactional
    public void deleteMemory(Long capsuleId, Long id, String currentUser) {
        Memory memory = memoryRepository.findById(id).orElseThrow(() -> new MemoryNotFound("Memory Not Found with id: " + id));
        Capsule capsule = capsuleRepository.findById(capsuleId).orElseThrow(() -> new CapsuleNotFound("Capsule with id " + capsuleId + " was not found"));

        UserModel user = userRepository.findByUsername(currentUser);

        capsule.removeMemory(memory);
        capsuleRepository.save(capsule);
    }
}
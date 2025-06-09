package com.java.web.virtual.time.capsule.controller;

import com.java.web.virtual.time.capsule.dto.memory.MemoryCreateDto;
import com.java.web.virtual.time.capsule.service.MemoryService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;

@RestController("api/v1/memories")
@AllArgsConstructor
public class MemoryController {

    private final MemoryService memoryService;

    @PostMapping()
    public ResponseEntity<?> addNewMemoryToCapsule(@NotNull @RequestParam Long capsuleId,@NotNull @RequestBody MemoryCreateDto memoryDto, Principal principal)
        throws IOException {
        Long memoryId = memoryService.saveMemory(capsuleId, memoryDto,principal.getName());
        URI location = URI.create("/api/v1/memories/" + memoryId);
        return ResponseEntity.created(location).build();
    }

    @PostMapping("")

    @PutMapping("/{id}/addMemory")
    public ResponseEntity<?> addExistingMemoryToCapsule(@PathVariable Long id, @RequestParam Long capsuleId, Principal principal) {
        Long memoryId = memoryService.addExistingMemoryToCapsule(capsuleId, id,principal.getName());
        URI location = URI.create("/api/v1/memories/" + memoryId);
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{id}/removeMemory")
    public ResponseEntity<?> removeMemoryFromCapsule(@PathVariable Long id, @RequestParam Long capsuleId, Principal principal) {
        memoryService.deleteMemory(capsuleId,id,principal.getName());
        return ResponseEntity.noContent().build();
    }
}

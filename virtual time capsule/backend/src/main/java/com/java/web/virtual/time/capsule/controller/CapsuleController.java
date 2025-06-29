package com.java.web.virtual.time.capsule.controller;

import com.java.web.virtual.time.capsule.dto.MemoryDto;
import com.java.web.virtual.time.capsule.dto.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.CapsuleResponseDto;
import com.java.web.virtual.time.capsule.dto.CapsuleUpdateDto;
import com.java.web.virtual.time.capsule.service.CapsuleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.security.Principal;
import java.util.Set;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/capsules")
public class CapsuleController {

    private final CapsuleService capsuleService;

    @PostMapping
    public ResponseEntity<CapsuleResponseDto> createCapsule(@Valid @RequestBody CapsuleCreateDto capsuleDto, Principal principal) {
        log.info("Received request to create capsule: {} for user: {} and openTime: {}", capsuleDto.getCapsuleName(), principal.getName(),capsuleDto.getOpenDateTime());
        CapsuleResponseDto createdCapsule = capsuleService.createCapsule(capsuleDto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCapsule);
    }

 
    @GetMapping("/all")
    public ResponseEntity<Set<CapsuleResponseDto>> getAllCapsulesOfUser(Principal principal) {
        log.info("Received request to get all capsules for user: {}", principal.getName());
        Set<CapsuleResponseDto> capsules = capsuleService.getAllCapsulesOfUser(principal.getName());
        log.info("Open times {}", capsules.stream().map(CapsuleResponseDto::getOpenDateTime).toList());
        return ResponseEntity.ok(capsules);
    }

 
    @GetMapping("/{id}")
    public ResponseEntity<CapsuleResponseDto> getCapsule(@NotNull @PathVariable Long id, Principal principal) {
        log.info("Received request to get capsule with id: {} for user: {}", id, principal.getName());
        CapsuleResponseDto capsule = capsuleService.getCapsuleById(id, principal.getName());
        return ResponseEntity.ok(capsule);
    }

 
    @PutMapping("/{id}")
    public ResponseEntity<CapsuleResponseDto> updateCapsule(
        @NotNull @PathVariable Long id,
        @Valid @RequestBody CapsuleUpdateDto capsuleDto, // Now expects CapsuleUpdateDto with nested GoalUpdatePartDto
        Principal principal) {
        log.info("Received request to update capsule with id: {} for user: {}", id, principal.getName());
        CapsuleResponseDto updatedCapsule = capsuleService.updateCapsule(id, capsuleDto, principal.getName());
        return ResponseEntity.ok(updatedCapsule);
    }

 
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCapsule(@NotNull @PathVariable Long id, Principal principal) {
        log.info("Received request to delete capsule with id: {} for user: {}", id, principal.getName());
        capsuleService.deleteCapsule(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

 
    @PutMapping("/{id}/lock")
    public ResponseEntity<CapsuleResponseDto> lockCapsule(
        @NotNull @PathVariable Long id,
        @NotNull @RequestParam String openDate, // This parameter is still directly here for locking specific path
        Principal principal) {
        log.info("Received request to lock capsule with id: {} for user: {} with openDate: {}", id, principal.getName(), openDate);
        CapsuleResponseDto lockedCapsule = capsuleService.lockCapsule(id, openDate, principal.getName());
        return ResponseEntity.ok(lockedCapsule);
    }

 
    @PutMapping("/{id}/open")
    public ResponseEntity<CapsuleResponseDto> openCapsule(@NotNull @PathVariable Long id, Principal principal) {
        log.info("Received request to open capsule with id: {} for user: {}", id, principal.getName());
        CapsuleResponseDto openedCapsule = capsuleService.openCapsule(id, principal.getName());
        return ResponseEntity.ok(openedCapsule);
    }

 
    @GetMapping("/{id}/memories")
    public ResponseEntity<Set<MemoryDto>> getMemoriesForCapsule(@NotNull @PathVariable Long id, Principal principal) {
        log.info("Received request to get memories for capsule with id: {} for user: {}", id, principal.getName());
        Set<MemoryDto> memories = capsuleService.getMemoriesFromCapsule(id, principal.getName());
        return ResponseEntity.ok(memories);
    }
}

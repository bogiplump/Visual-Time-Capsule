package com.java.web.virtual.time.capsule.controller;

import com.java.web.virtual.time.capsule.dto.capsule.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.capsule.CapsuleUpdateDto;
import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.Goal;
import com.java.web.virtual.time.capsule.model.Memory;
import com.java.web.virtual.time.capsule.service.CapsuleService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.Principal;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/capsules")
public class CapsuleController {

    private final CapsuleService capsuleService;

    @PostMapping
    public ResponseEntity<?> createCapsule(@NotNull @RequestBody CapsuleCreateDto capsuleDto, Principal principal) {
        Long capsuleId = capsuleService.createCapsule(capsuleDto, principal.getName());
        URI location = URI.create("/api/v1/capsules/" + capsuleId);
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Capsule> getCapsuleById(@NotNull @PathVariable Long id,Principal principal) {
        return ResponseEntity.ok(capsuleService.getCapsule(id,principal.getName()));
    }

    @GetMapping("/all")
    public ResponseEntity<Set<Capsule>> getAllCapsules(Principal principal) {
        return ResponseEntity.ok(capsuleService.getAllCapsulesOfUser(principal.getName()));
    }

    @PutMapping("/{id}/lock")
    public ResponseEntity<?> lockCapsule(@NotNull @PathVariable Long id, @NotNull @RequestParam String openDate, Principal principal) {
        capsuleService.lockCapsule(id, openDate,principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCapsule(@NotNull @PathVariable Long id, @NotNull @RequestBody CapsuleUpdateDto capsuleDto, Principal principal) {
        capsuleService.updateCapsule(id, capsuleDto, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCapsule(@NotNull @PathVariable Long id, Principal principal) {
        capsuleService.deleteCapsule(id,principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/memories")
    public ResponseEntity<Set<Memory>> getMemoriesForCapsule(@NotNull @PathVariable Long id, Principal principal) {
        Set<Memory> memories = capsuleService.getMemoriesFromCapsule(id,principal.getName());
        return ResponseEntity.ok(memories);
    }

    @GetMapping("/{id}/goal")
    public ResponseEntity<Goal> getGoalForCapsule(@NotNull @PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(capsuleService.getGoalForCapsule(id,principal.getName()));
    }
}
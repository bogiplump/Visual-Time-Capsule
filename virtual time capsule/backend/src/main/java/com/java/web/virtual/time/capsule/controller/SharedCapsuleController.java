package com.java.web.virtual.time.capsule.controller;

import com.java.web.virtual.time.capsule.dto.capsule.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.sharedcapsule.SharedCapsuleResponseDto;
import com.java.web.virtual.time.capsule.service.SharedCapsuleService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("api/v1/shared-capsules")
public class SharedCapsuleController {
    private SharedCapsuleService sharedCapsuleService;

    @PostMapping
    public ResponseEntity<?> createSharedCapsule(@NotNull @RequestBody CapsuleCreateDto sharedCapsuleDto,
                                                 Principal principal) {
        Long capsuleId = sharedCapsuleService.createSharedCapsule(sharedCapsuleDto, principal.getName());

        URI location = URI.create("/api/v1/shared-capsules/" + capsuleId);

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SharedCapsuleResponseDto> getSharedCapsuleById(@NotNull @PathVariable Long id,
                                                                         Principal principal) {
        return ResponseEntity.ok(sharedCapsuleService.getSharedCapsuleById(id, principal.getName()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> addUserToCapsule(@NotNull @PathVariable Long capsuleId, @NotNull @RequestParam Long userId,
                                 Principal principal) {
        sharedCapsuleService.addUserToCapsule(capsuleId, userId, principal.getName());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> removeUserFromCapsule(@NotNull @PathVariable Long id, @NotNull @RequestParam Long userId,
                                                        Principal principal) {
        sharedCapsuleService.addUserToCapsule(id, userId, principal.getName());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}/ready-to-lock")
    public ResponseEntity<?> setReadyToClose(@NotNull @PathVariable Long id, Principal principal) {
        sharedCapsuleService.setReadyToClose(id, principal.getName());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}/creator-ready-to-lock")
    public ResponseEntity<?> creatorReadToClose(@NotNull @PathVariable Long id, @NotNull @RequestParam String openDate,
                                                Principal principal) {
        sharedCapsuleService.creatorLock(id, openDate, principal.getName());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}/force-lock")
    public ResponseEntity<?> forceLock(@NotNull @PathVariable Long id, Principal principal) {
        sharedCapsuleService.forceLock(id, principal.getName());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSharedCapsuleById(@NotNull @PathVariable Long id,
                                                  Principal principal) {
        sharedCapsuleService.deleteSharedCapsuleById(id, principal.getName());

        return ResponseEntity.noContent().build();
    }
}

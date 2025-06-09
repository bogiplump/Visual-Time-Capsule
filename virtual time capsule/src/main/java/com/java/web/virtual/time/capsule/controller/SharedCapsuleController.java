package com.java.web.virtual.time.capsule.controller;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/shared-capsules")
public class SharedCapsuleController {
    @PostMapping
    public void createSharedCapsule() {

    }

    @GetMapping("/{id}")
    public void getSharedCapsuleById(@NotNull @PathVariable Long id) {

    }

    @PatchMapping("/{id}")
    public void addUserToCapsule(@NotNull @PathVariable Long capsuleId, @NotNull @RequestParam Long userId) {

    }

    @PatchMapping("/{id}")
    public void removeUserFromCapsule(@NotNull @PathVariable Long capsuleId, @NotNull @RequestParam Long userId) {

    }

    @DeleteMapping("/{id}")
    public void deleteSharedCapsuleById(@NotNull @PathVariable Long id) {

    }
}

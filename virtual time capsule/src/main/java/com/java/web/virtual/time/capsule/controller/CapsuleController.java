package com.java.web.virtual.time.capsule.controller;

import com.java.web.virtual.time.capsule.dto.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.CapsuleUpdateDto;
import com.java.web.virtual.time.capsule.dto.MemoryCreateDto;
import com.java.web.virtual.time.capsule.model.entity.CapsuleEntity;
import com.java.web.virtual.time.capsule.service.CapsuleService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/capsules")
public class CapsuleController {
    CapsuleService capsuleService;

    public CapsuleController(CapsuleService capsuleService) {
        this.capsuleService = capsuleService;
    }

    @PostMapping
    public void createCapsule(@RequestBody CapsuleCreateDto capsuleDto) {
        capsuleService.createCapsule(capsuleDto);
    }

    @GetMapping("/{id}")
    public CapsuleEntity getCapsuleById(@PathVariable Long id) {
        return capsuleService.getCapsuleById(id);
    }

    @GetMapping("/all")
    public Set<CapsuleEntity> getAllCapsules() {
        return capsuleService.getAllCapsulesOfUser();
    }

    @PatchMapping("/{id}/lock")
    public void lockCapsule(@PathVariable Long id, @RequestParam String openDate) {
        capsuleService.lockCapsuleById(id, openDate);
    }

    @PatchMapping("/{id}/addMemory")
    public void addNewMemoryToCapsule(@PathVariable Long id, @RequestBody MemoryCreateDto memoryDto) {
        capsuleService.addMemoryToCapsule(id, memoryDto);
    }

    @PatchMapping("/{id}/addMemory")
    public void addExistingMemoryToCapsule(@PathVariable Long id, @RequestParam Long memoryId) {
        capsuleService.putMemoryInCapsule(id, memoryId);
    }

    @PatchMapping("/{id}/removeMemory")
    public void removeMemoryFromCapsule(@PathVariable Long id, @RequestParam Long memoryId) {
        capsuleService.removeMemoryFromCapsule(id, memoryId);
    }

    @PatchMapping("/{id}")
    public void updateCapsule(@PathVariable Long id, @RequestBody CapsuleUpdateDto capsuleDto) {
        capsuleService.updateCapsule(id, capsuleDto);
    }

    @DeleteMapping("/{id}")
    public void deleteCapsule(@PathVariable Long id) {
        capsuleService.deleteCapsuleById(id);
    }
}
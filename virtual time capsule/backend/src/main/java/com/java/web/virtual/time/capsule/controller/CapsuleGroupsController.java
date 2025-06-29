package com.java.web.virtual.time.capsule.controller;

import com.java.web.virtual.time.capsule.dto.CreateGroupDto;
import com.java.web.virtual.time.capsule.dto.GroupUpdateDto;
import com.java.web.virtual.time.capsule.service.CapsuleGroupService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.security.Principal;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/groups")
@AllArgsConstructor
public class CapsuleGroupsController {
    @Autowired
    private final CapsuleGroupService capsuleGroupService;

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getGroups(@NotNull @PathVariable Long userId) {
        return ResponseEntity.ok(capsuleGroupService.getAllCapsuleGroupsByUserId(userId));
    }

    @PostMapping("/")
    public ResponseEntity<?> createGroup(@RequestBody @Valid CreateGroupDto createGroupDto, Principal principal) {
        capsuleGroupService.createCapsuleGroup(createGroupDto, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getGroup(@PathVariable Long groupId) {
        return ResponseEntity.ok(capsuleGroupService.getCapsulesInGroup(groupId));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateGroupContent(@NotNull @RequestBody GroupUpdateDto groupUpdateDto) {
        capsuleGroupService.updateGroup(groupUpdateDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteGroup(@NotNull @PathVariable Long id) {
        capsuleGroupService.deleteGroup(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}

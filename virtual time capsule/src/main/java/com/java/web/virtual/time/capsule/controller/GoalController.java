package com.java.web.virtual.time.capsule.controller;

import com.java.web.virtual.time.capsule.dto.goal.GoalCreateDto;
import com.java.web.virtual.time.capsule.dto.goal.GoalUpdateDto;
import com.java.web.virtual.time.capsule.model.Goal;
import com.java.web.virtual.time.capsule.service.GoalService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/goals")
@AllArgsConstructor
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/capsule")
    public ResponseEntity<Goal> createGoal(@RequestParam Long capsuleId, @RequestBody @Valid GoalCreateDto goalDto,
                                           Principal principal) {
        goalService.createGoal(capsuleId, goalDto, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/capsule/{id}")
    public ResponseEntity<Goal> getGoal(@NotNull @PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(goalService.getGoal(id));
    }

    @PatchMapping("/capsule/{id}")
    public ResponseEntity<HttpStatus> updateGoalContent(@NotNull @PathVariable Long id, @NotNull  @RequestBody GoalUpdateDto goalDto,
                                                        Principal principal) {
        goalService.updateGoal(id, goalDto);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/capsule/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long id, Principal principal) {
        goalService.deleteGoal(id);

        return ResponseEntity.ok(HttpStatus.OK);
    }
}

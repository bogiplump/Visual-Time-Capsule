package com.java.web.virtual.time.capsule.controller;

import com.java.web.virtual.time.capsule.dto.GoalCreateDto;
import com.java.web.virtual.time.capsule.dto.GoalDto;
import com.java.web.virtual.time.capsule.dto.UpdateGoalDto;
import com.java.web.virtual.time.capsule.model.Goal;
import com.java.web.virtual.time.capsule.service.GoalService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/v1/goals")
@AllArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @GetMapping("/{id}")
    public ResponseEntity<GoalDto> getGoal(@NotNull @PathVariable Long id) {
        log.info("Get goal with id {}", id);
        return ResponseEntity.ok(goalService.getGoal(id));
    }

    @PostMapping("/")
    public ResponseEntity<Goal> createGoal(@RequestParam Long capsuleId, @RequestBody @Valid GoalCreateDto goalDto, Principal principal) {
        log.info("dto {}", goalDto.toString());
        goalService.createGoal(capsuleId, goalDto,principal.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long id, Principal principal) {
        goalService.deleteGoal(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<HttpStatus> updateGoalContent(@NotNull @PathVariable Long id,@NotNull  @RequestBody UpdateGoalDto updateGoalDto) {
        goalService.updateGoal(id,updateGoalDto);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/{id}/setIsAchieved")
    public ResponseEntity<?> setIsAchieved(@NotNull @PathVariable Long id, @RequestParam Boolean isAchieved) {
        log.info("setIsAchieved with id {} {}", id, isAchieved);
        goalService.setGoalIsAchievedAndMakeVisible(id,isAchieved);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}

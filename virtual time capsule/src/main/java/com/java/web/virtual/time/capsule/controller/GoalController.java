package com.java.web.virtual.time.capsule.controller;

import com.java.web.virtual.time.capsule.dtos.GoalDto;
import com.java.web.virtual.time.capsule.dtos.UpdateGoalDto;
import com.java.web.virtual.time.capsule.service.GoalService;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/goals")
@AllArgsConstructor
public class GoalController {
    @Autowired
    private GoalService goalService;

    @GetMapping("/{id}")
    public ResponseEntity<GoalDto> getGoal(@RequestParam Long id) {
        return ResponseEntity.ok(goalService.getGoal(id));
    }

    @PutMapping("/update")
    public ResponseEntity<HttpStatus> updateGoalContent(@RequestBody UpdateGoalDto updateGoalDto) {
        goalService.updateGoal(updateGoalDto);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<HttpStatus> deleteGoal(@RequestParam Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}

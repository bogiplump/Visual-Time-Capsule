package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.GoalCreateDto;
import com.java.web.virtual.time.capsule.dto.GoalDto;
import com.java.web.virtual.time.capsule.dto.UpdateGoalDto;
import com.java.web.virtual.time.capsule.model.Goal;
import java.util.List;

public interface GoalService {
    void createGoal(Long capsuleId, GoalCreateDto goalCreateDto, String creator);
  
    void updateGoal(Long id, UpdateGoalDto updateGoalDto);

    void deleteGoal(Long id);

    List<Goal> getUserGoals(Long userId);

    void setGoalIsAchievedAndMakeVisible(Long id, Boolean achieved);

    GoalDto getGoal(Long id);
}
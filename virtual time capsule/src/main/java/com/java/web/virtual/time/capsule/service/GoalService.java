package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.goal.GoalCreateDto;
import com.java.web.virtual.time.capsule.dto.goal.GoalUpdateDto;
import com.java.web.virtual.time.capsule.model.Goal;
import java.util.List;

public interface GoalService {
    Goal createGoal(Long capsuleId, GoalCreateDto goalEntity, String creator);

    void updateGoal(Long id, GoalUpdateDto updateGoalDto);

    void deleteGoal(Long id);

    List<Goal> getUserGoals(Long userId);

    void makeGoalVisible(Long id);

    void setGoalIsAchieved(Long id);

    Goal getGoal(Long id);
}
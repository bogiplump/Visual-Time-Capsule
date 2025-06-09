package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.goal.GoalDto;
import com.java.web.virtual.time.capsule.dto.user.UpdateGoalDto;
import com.java.web.virtual.time.capsule.model.Goal;
import java.util.List;

public interface GoalService {
    Goal createGoal(Long capsuleId,GoalDto goalEntity,String creator);

    void updateGoal(Long id, UpdateGoalDto updateGoalDto);

    void deleteGoal(Long id);

    List<Goal> getUserGoals(Long userId);

    void makeGoalVisible(Long id);

    void setGoalIsAchieved(Long id);

    Goal getGoal(Long id);
}
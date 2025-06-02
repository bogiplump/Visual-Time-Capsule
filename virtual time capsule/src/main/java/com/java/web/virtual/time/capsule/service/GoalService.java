package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dtos.GoalDto;
import com.java.web.virtual.time.capsule.dtos.UpdateGoalDto;
import com.java.web.virtual.time.capsule.model.GoalEntity;
import java.util.List;

public interface GoalService {
    void createGoal(GoalEntity goalEntity);

    void updateGoal(UpdateGoalDto updateGoalDto);

    void deleteGoal(Long id);

    List<GoalEntity> getUserGoals(Long userId);

    void makeGoalVisible(Long id);

    void setGoalIsAchieved(Long id);

    GoalDto getGoal(Long id);
}

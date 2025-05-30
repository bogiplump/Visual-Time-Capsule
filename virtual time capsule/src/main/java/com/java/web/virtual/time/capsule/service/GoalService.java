package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dtos.UpdateGoalDto;
import com.java.web.virtual.time.capsule.model.GoalEntity;
import java.util.List;

public interface GoalService {
    void createGoal(GoalEntity goalEntity);

    void updateGoal(UpdateGoalDto updateGoalDto);

    void deleteGoal(Integer id);

    List<GoalEntity> getUserGoals(UpdateGoalDto listGoalsDto);

    void makeGoalVisible(Integer id);

    void setGoalIsAchieved(Integer id);
}

package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.GoalDto;
import com.java.web.virtual.time.capsule.exception.goal.GoalNotFound;
import com.java.web.virtual.time.capsule.model.entity.GoalEntity;
import org.springframework.stereotype.Service;

@Service
public interface GoalService {
    /**
     * Parses goal data transfer object to GoalEntity.
     *
     * @param goalDto must not be null
     * @return the GoalEntity object.
     */
    GoalEntity parseGoalDto(GoalDto goalDto);

    /**
     * Deletes the goal entity associated with this id from the database.
     *
     * @param goalId unique identifier of the goal entity, must not be null.
     *
     * @throws IllegalArgumentException if goalId is null.
     * @throws GoalNotFound if goal with this id does not exist.
     */
    void deleteGoal(Long goalId);
}

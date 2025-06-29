package com.java.web.virtual.time.capsule.service.impl;

import com.java.web.virtual.time.capsule.dto.GoalDto;
import com.java.web.virtual.time.capsule.dto.UpdateGoalDto;
import com.java.web.virtual.time.capsule.exception.goal.GoalNotFoundException;
import com.java.web.virtual.time.capsule.exception.goal.GoalNotVisibleException;
import com.java.web.virtual.time.capsule.mapper.GoalMapper;
import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.Goal;
import com.java.web.virtual.time.capsule.model.UserModel;
import com.java.web.virtual.time.capsule.repository.CapsuleRepository;
import com.java.web.virtual.time.capsule.repository.GoalRepository;
import com.java.web.virtual.time.capsule.repository.UserRepository;

import java.util.List;

import com.java.web.virtual.time.capsule.service.GoalService;
import lombok.AllArgsConstructor;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GoalServiceImpl implements GoalService {

    private GoalMapper goalMapper;
    private GoalRepository goalRepository;
    private UserRepository userRepository;
    private CapsuleRepository capsuleRepository;

    @Override
    public Goal createGoal(Long capsuleId, GoalDto goalEntity, String creator) {
        Capsule capsule = capsuleRepository.findById(capsuleId).orElseThrow(GoalNotFoundException::new);
        UserModel user = userRepository.findByUsername(creator);
        Goal goal = goalMapper.toEntity(goalEntity);
        goal.setCapsule(capsule);
        return goalRepository.save(goal);
    }

    @Override
    public void updateGoal(Long id, UpdateGoalDto updateGoalDto) {
        if (updateGoalDto == null) {
            throw new IllegalArgumentException(("No goal entity provided."));
        }

        Goal goal = goalRepository.findById(id).orElseThrow(GoalNotFoundException::new);
        goal.setContent(updateGoalDto.getContentUpdate());

        goalRepository.save(goal);
    }

    public Goal getGoal(Long id) {
        Goal goal = goalRepository.getReferenceById(id);
        if (!goal.isVisible()) {
            throw new GoalNotVisibleException("Goal not visible to user.");
        }
        return goal;
    }

    @Override
    public void deleteGoal(Long id) {
        if (!goalRepository.existsById(id)) {
            throw new GoalNotFoundException("Goal not found.");
        }

        goalRepository.deleteById(id);
    }

    @Override
    public List<Goal> getUserGoals(Long userId) {
        String username = userRepository.getReferenceById(userId).getUsername();

        List<Goal> userGoals = goalRepository.findByCreator(userRepository.findByUsername(username));

        return userGoals.stream()
            .filter(Goal::isVisible)
            .toList();
    }

    @Override
    public void makeGoalVisible(Long id) {
        goalRepository.getReferenceById(id).setVisible(true);
    }

    @Override
    public void setGoalIsAchieved(Long id) {
        Goal goal = goalRepository.getReferenceById(id);

        goal.setAchieved(true);
        goalRepository.save(goal);
    }
}
package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dtos.GoalDto;
import com.java.web.virtual.time.capsule.dtos.UpdateGoalDto;
import com.java.web.virtual.time.capsule.exception.GoalNotFoundException;
import com.java.web.virtual.time.capsule.exception.GoalNotVisibleException;
import com.java.web.virtual.time.capsule.mapper.GoalMapper;
import com.java.web.virtual.time.capsule.model.GoalEntity;
import com.java.web.virtual.time.capsule.repository.GoalRepository;
import com.java.web.virtual.time.capsule.repository.UserRepository;

import java.util.List;

import lombok.AllArgsConstructor;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GoalServiceImpl implements GoalService {
    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void createGoal(GoalEntity goalEntity) {
        if (goalEntity == null) {
            throw new IllegalArgumentException(("No goal entity provided."));
        }

        goalRepository.save(goalEntity);
    }

    @Override
    public void updateGoal(UpdateGoalDto updateGoalDto) {
        if (updateGoalDto == null) {
            throw new IllegalArgumentException(("No goal entity provided."));
        }

        GoalEntity goalEntity = getGoalEntity(updateGoalDto.getId());

        goalRepository.save(goalEntity);
    }

    private @NotNull GoalEntity getGoalEntity(Integer updateGoalDto) {
        GoalEntity goalEntity = goalRepository.getReferenceById(updateGoalDto);
        if (!goalEntity.isVisible()) {
            throw new GoalNotVisibleException("Goal not visible to user.");
        }
        return goalEntity;
    }

    @Override
    public void deleteGoal(Integer id) {
        if (!goalRepository.existsById(id)) {
            throw new GoalNotFoundException("Goal not found.");
        }

        goalRepository.deleteById(id);
    }

    @Override
    public List<GoalEntity> getUserGoals(Long userId) {
        String username = userRepository.getReferenceById(userId).getUsername();

        List<GoalEntity> userGoals = goalRepository.findByCreator(userRepository.findByUsername(username));

        return userGoals.stream()
            .filter(GoalEntity::isVisible)
            .toList();
    }

    @Override
    public void makeGoalVisible(Integer id) {
        goalRepository.getReferenceById(id).setVisible(true);
    }

    @Override
    public void setGoalIsAchieved(Integer id) {
        GoalEntity goal = getGoalEntity(id);

        goal.setAchieved(true);
        goalRepository.save(goal);
    }

    @Override
    public GoalDto getGoal(Integer id) {
        if (!goalRepository.existsById(id)) {
            throw new GoalNotFoundException("Goal not found.");
        }

        GoalEntity goal = getGoalEntity(id);

        return GoalMapper.INSTANCE.toDTO(goal);
    }
}

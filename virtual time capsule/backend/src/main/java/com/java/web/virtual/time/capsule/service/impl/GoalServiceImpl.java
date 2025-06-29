package com.java.web.virtual.time.capsule.service.impl;

import com.java.web.virtual.time.capsule.dto.GoalCreateDto;
import com.java.web.virtual.time.capsule.dto.GoalDto;
import com.java.web.virtual.time.capsule.dto.UpdateGoalDto;
import com.java.web.virtual.time.capsule.exception.goal.GoalNotFoundException;
import com.java.web.virtual.time.capsule.mapper.GoalMapper;
import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.Goal;
import com.java.web.virtual.time.capsule.model.UserModel;
import com.java.web.virtual.time.capsule.repository.CapsuleRepository;
import com.java.web.virtual.time.capsule.repository.GoalRepository;
import com.java.web.virtual.time.capsule.repository.UserRepository;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import com.java.web.virtual.time.capsule.service.GoalService;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class GoalServiceImpl implements GoalService {

    private GoalMapper goalMapper;
    private GoalRepository goalRepository;
    private UserRepository userRepository;
    private CapsuleRepository capsuleRepository;

    @Override
    public void createGoal(Long capsuleId, GoalCreateDto goalCreateDto, String creator) {
        Capsule capsule = capsuleRepository.findById(capsuleId).orElseThrow(GoalNotFoundException::new);
        UserModel user = userRepository.findByUsername(creator);
        Goal goal = Goal.builder()
            .content(goalCreateDto.getContent())
            .isVisible(goalCreateDto.getIsVisible())
            .build();
        goal.setIsAchieved(false);
        goal.setCapsule(capsule);
        goal.setCreator(user);
        goal.setCreationDate(LocalDate.now(ZoneOffset.UTC));
        log.info("Creating goal {}", goal);
        goalRepository.save(goal);
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

    public GoalDto getGoal(Long id) {
        Goal goal = goalRepository.getReferenceById(id);
        log.info(goal.toString());
        return goalMapper.toDto(goal);
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
            .filter(Goal::getIsVisible)
            .toList();
    }

    @Override
    public void setGoalIsAchievedAndMakeVisible(Long id, Boolean isAchieved) {
        Goal goal = goalRepository.getReferenceById(id);
        goal.setIsVisible(true);
        goal.setIsAchieved(isAchieved);
        goalRepository.save(goal);
    }
}
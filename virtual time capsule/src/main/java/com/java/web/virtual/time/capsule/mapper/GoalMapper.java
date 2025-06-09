package com.java.web.virtual.time.capsule.mapper;

import com.java.web.virtual.time.capsule.dto.goal.GoalDto;
import com.java.web.virtual.time.capsule.model.Goal;

import org.mapstruct.factory.Mappers;

public interface GoalMapper {
    GoalMapper INSTANCE = Mappers.getMapper(GoalMapper.class);

    GoalDto toDTO(Goal entity);
}

package com.java.web.virtual.time.capsule.mapper;

import com.java.web.virtual.time.capsule.dto.GoalDto;
import com.java.web.virtual.time.capsule.model.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface GoalMapper {

    @Mapping(target = "content", source = "content")
    @Mapping(target = "isAchieved" , source = "isAchieved")
    @Mapping(target = "isVisible",source = "isVisible")
    @Mapping(target = "creationDate", expression = "java(goal.getCreationDate() != null ? goal.getCreationDate().toString() : null)")
    @Mapping(target = "creator", source = "creator.id")
    @Mapping(target = "capsuleId", source = "capsule.id")
    GoalDto toDto(Goal goal);

    @Mapping(target = "creationDate", expression = "java(goalDto.getCreationDate() != null ? LocalDateTime.parse(goalDto.getCreationDate()) : null)")
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "capsule", ignore = true)
    Goal toEntity(GoalDto goalDto);
}


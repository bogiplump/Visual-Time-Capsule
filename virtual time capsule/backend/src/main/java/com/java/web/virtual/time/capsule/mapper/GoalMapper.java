package com.java.web.virtual.time.capsule.mapper;

import com.java.web.virtual.time.capsule.dto.GoalDto;
import com.java.web.virtual.time.capsule.model.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDate;

@Mapper(componentModel = "spring", imports = {LocalDate.class}) // Add LocalDate to imports for expressions
public interface GoalMapper {

    @Mapping(target = "creationDate", expression = "java(goal.getCreationDate() != null ? goal.getCreationDate().toString() : null)")
    @Mapping(target = "creator", source = "creator.id") // Map creator UserModel's ID to creator Long in DTO
    @Mapping(target = "capsuleId", source = "capsule.id") // Map capsule's ID to capsuleId Long in DTO
    GoalDto toDto(Goal goal);

    @Mapping(target = "creationDate", expression = "java(goalDto.getCreationDate() != null ? LocalDate.parse(goalDto.getCreationDate()) : null)")
    @Mapping(target = "creator", ignore = true) // Service layer will handle setting the UserModel object
    @Mapping(target = "capsule", ignore = true) // Service layer will handle setting the Capsule object
    Goal toEntity(GoalDto goalDto);
}


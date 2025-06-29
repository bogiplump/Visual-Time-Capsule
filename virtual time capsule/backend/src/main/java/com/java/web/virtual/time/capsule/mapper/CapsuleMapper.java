package com.java.web.virtual.time.capsule.mapper;

import com.java.web.virtual.time.capsule.dto.CapsuleResponseDto;
import com.java.web.virtual.time.capsule.dto.GoalDto;
import com.java.web.virtual.time.capsule.mapper.GoalMapper;
import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.Goal; // Model import
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers; // Import Mappers for explicit access

import java.time.LocalDateTime;
import java.time.ZoneOffset; // Import ZoneOffset here for the mapper interface


@Mapper(
    componentModel = "spring",
    uses = {GoalMapper.class, MemoryMapper.class, UserMapper.class},
    imports = {ZoneOffset.class, Mappers.class} // NEW: Add Mappers.class to imports for generated class
)
public interface CapsuleMapper {

    @Mapping(target = "creationDate", source = "creationDate", qualifiedByName = "localDateTimeToIsoUtc")
    @Mapping(target = "lockDate", source = "lockDate", qualifiedByName = "localDateTimeToIsoUtc")
    @Mapping(target = "openDateTime", source = "openDateTime", qualifiedByName = "localDateTimeToIsoUtc") // Ensure this mapping is correct
    @Mapping(target = "creator", source = "creator")
    @Mapping(target = "goal", source = "goal")
    @Mapping(target = "memories", source = "memoryEntries") // Ensure memories are mapped
    CapsuleResponseDto toDto(Capsule capsule);

    // Helper method to conditionally map the Goal to GoalDto
    @Named("mapVisibleGoal")
    default GoalDto mapVisibleGoal(Goal goal) {
        if (goal == null || !goal.isVisible()) {
            return null;
        }
        // This will use the injected GoalMapper
        // Since this method is part of the CapsuleMapper interface, and GoalMapper is in 'uses',
        // MapStruct knows how to provide GoalMapper here.
        return Mappers.getMapper(GoalMapper.class).toDto(goal); // Explicitly get mapper instance
    }

    // Custom method to convert LocalDateTime to ISO string with 'Z'
    @Named("localDateTimeToIsoUtc")
    default String localDateTimeToIsoUtc(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        // Convert LocalDateTime (assumed to be UTC) to an Instant, then format as ISO string with 'Z'
        return localDateTime.toInstant(ZoneOffset.UTC).toString();
    }
}

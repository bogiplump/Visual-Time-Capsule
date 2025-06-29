package com.java.web.virtual.time.capsule.mapper; // Corrected package name to mappers

import com.java.web.virtual.time.capsule.dto.UserProfileDto;
import com.java.web.virtual.time.capsule.dto.CapsuleResponseDto;
import com.java.web.virtual.time.capsule.dto.GoalDto;
import com.java.web.virtual.time.capsule.mapper.GoalMapper;
import com.java.web.virtual.time.capsule.mapper.MemoryMapper;
import com.java.web.virtual.time.capsule.mapper.UserMapper;
import com.java.web.virtual.time.capsule.model.Capsule; // Corrected model package
import com.java.web.virtual.time.capsule.model.Goal; // Corrected model package
import com.java.web.virtual.time.capsule.model.UserModel; // Added for sharedWithUsers mapping
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime; // Added for localDateTimeToIsoUtc
import java.time.ZoneOffset;
import java.util.Set;

@Mapper(
    componentModel = "spring",
    uses = {GoalMapper.class, MemoryMapper.class, UserMapper.class},
    imports = {LocalDateTime.class, ZoneOffset.class, Mappers.class} // Added LocalDateTime and Mappers.class
)
public interface CapsuleMapper {

//@Mapping(target = "isShared", source = "isShared")
    @Mapping(target = "creationDate", source = "creationDate", qualifiedByName = "localDateTimeToIsoUtc")
    @Mapping(target = "lockDate", source = "lockDate", qualifiedByName = "localDateTimeToIsoUtc")
    @Mapping(target = "openDateTime", source = "openDateTime", qualifiedByName = "localDateTimeToIsoUtc")
    @Mapping(target = "creator", source = "creator", qualifiedByName = "userModelToUserProfileDto") // Use named mapping from UserMapper
    @Mapping(target = "goal", source = "goal", qualifiedByName = "mapVisibleGoal") // Conditionally map goal
    @Mapping(target = "memories", source = "memoryEntries")

    // Map Set<UserModel> to Set<UserProfileDto> using UserMapper's named method
    @Mapping(target = "sharedWithUsers", source = "sharedWithUsers", qualifiedByName = "userModelSetToUserProfileDtoSet")
    // These counts are populated in the service layer, not directly mapped from Capsule entity fields
    @Mapping(target = "readyParticipantsCount", ignore = true)
    @Mapping(target = "totalParticipantsCount", ignore = true)
    @Mapping(target = "isCurrentUserReadyToClose", ignore = true)
    CapsuleResponseDto toDto(Capsule capsule);

    // Helper method to conditionally map the Goal to GoalDto
    @Named("mapVisibleGoal")
    default GoalDto mapVisibleGoal(Goal goal) {
        if (goal == null || !goal.isVisible()) {
            return null;
        }
        // This will use the injected GoalMapper
        return Mappers.getMapper(GoalMapper.class).toDto(goal);
    }

    // Custom method to convert LocalDateTime to ISO string with 'Z'
    @Named("localDateTimeToIsoUtc")
    default String localDateTimeToIsoUtc(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.toInstant(ZoneOffset.UTC).toString();
    }

    // NEW: Helper method to map Set<UserModel> to Set<UserProfileDto>
    @Named("userModelSetToUserProfileDtoSet")
    default Set<UserProfileDto> userModelSetToUserProfileDtoSet(Set<UserModel> userModels) {
        if (userModels == null) {
            return null;
        }
        UserMapper userMapper = Mappers.getMapper(UserMapper.class);
        return userModels.stream()
            .map(userMapper::toUserProfileDto)
            .collect(java.util.stream.Collectors.toSet());
    }
}

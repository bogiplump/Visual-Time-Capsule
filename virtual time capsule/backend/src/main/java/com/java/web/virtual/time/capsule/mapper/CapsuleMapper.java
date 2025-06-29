package com.java.web.virtual.time.capsule.mapper; 

import com.java.web.virtual.time.capsule.dto.UserProfileDto;
import com.java.web.virtual.time.capsule.dto.CapsuleResponseDto;
import com.java.web.virtual.time.capsule.dto.GoalDto;
import com.java.web.virtual.time.capsule.mapper.GoalMapper;
import com.java.web.virtual.time.capsule.mapper.MemoryMapper;
import com.java.web.virtual.time.capsule.mapper.UserMapper;
import com.java.web.virtual.time.capsule.model.Capsule; 
import com.java.web.virtual.time.capsule.model.Goal; 
import com.java.web.virtual.time.capsule.model.UserModel; 
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime; 
import java.time.ZoneOffset;
import java.util.Set;

@Mapper(
    componentModel = "spring",
    uses = {GoalMapper.class, MemoryMapper.class, UserMapper.class},
    imports = {LocalDateTime.class, ZoneOffset.class, Mappers.class} 
)
public interface CapsuleMapper {
    @Mapping(target = "isShared", source = "isShared")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "creationDate", source = "creationDate", qualifiedByName = "localDateTimeToIsoUtc")
    @Mapping(target = "lockDate", source = "lockDate", qualifiedByName = "localDateTimeToIsoUtc")
    @Mapping(target = "openDateTime", source = "openDateTime", qualifiedByName = "localDateTimeToIsoUtc")
    @Mapping(target = "creator", source = "creator", qualifiedByName = "userModelToUserProfileDto") 
    @Mapping(target = "goalId", source = "goal.id")
    @Mapping(target = "memories", source = "memoryEntries")
    @Mapping(target = "sharedWithUsers", source = "sharedWithUsers", qualifiedByName = "userModelSetToUserProfileDtoSet")
    @Mapping(target = "readyParticipantsCount", ignore = true)
    @Mapping(target = "totalParticipantsCount", ignore = true)
    @Mapping(target = "isCurrentUserReadyToClose", ignore = true)
    CapsuleResponseDto toDto(Capsule capsule);
    
    @Named("localDateTimeToIsoUtc")
    default String localDateTimeToIsoUtc(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.toInstant(ZoneOffset.UTC).toString();
    }

    
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

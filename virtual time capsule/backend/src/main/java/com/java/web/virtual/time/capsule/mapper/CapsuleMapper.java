package com.java.web.virtual.time.capsule.mapper; 

import com.java.web.virtual.time.capsule.configuration.SpringContext;
import com.java.web.virtual.time.capsule.dto.CapsuleCreateDto;
import com.java.web.virtual.time.capsule.dto.UserProfileDto;
import com.java.web.virtual.time.capsule.dto.CapsuleResponseDto;
import com.java.web.virtual.time.capsule.dto.GoalDto;
import com.java.web.virtual.time.capsule.mapper.GoalMapper;
import com.java.web.virtual.time.capsule.mapper.MemoryMapper;
import com.java.web.virtual.time.capsule.mapper.UserMapper;
import com.java.web.virtual.time.capsule.model.Capsule; 
import com.java.web.virtual.time.capsule.model.Goal; 
import com.java.web.virtual.time.capsule.model.UserModel;
import com.java.web.virtual.time.capsule.repository.UserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime; 
import java.time.ZoneOffset;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
    componentModel = "spring",
    uses = {GoalMapper.class, MemoryMapper.class, UserMapper.class, UserRepository.class}, // assuming UserRepository is injected
    imports = {LocalDateTime.class, ZoneOffset.class, java.time.format.DateTimeFormatter.class, Mappers.class}
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

    @Mapping(target = "capsuleName", source = "capsuleName")
    @Mapping(target = "openDateTime", source = "openDateTime", qualifiedByName = "isoUtcToLocalDateTime")
    @Mapping(target = "goal", source = "goal")
    @Mapping(target = "sharedWithUsers", source = "sharedWithUserIds", qualifiedByName = "userIdsToUserModels")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "lockDate", ignore = true)
    @Mapping(target = "memoryEntries", ignore = true)
    Capsule toEntity(CapsuleCreateDto dto);


    @Named("localDateTimeToIsoUtc")
    default String localDateTimeToIsoUtc(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.toInstant(ZoneOffset.UTC).toString();
    }

    @Named("isoUtcToLocalDateTime")
    default LocalDateTime isoUtcToLocalDateTime(String isoUtc) {
        if (isoUtc == null) return null;
        return LocalDateTime.ofInstant(java.time.Instant.parse(isoUtc), ZoneOffset.UTC);
    }

    @Named("userModelSetToUserProfileDtoSet")
    default Set<UserProfileDto> userModelSetToUserProfileDtoSet(Set<UserModel> userModels) {
        if (userModels == null) return null;
        UserMapper userMapper = Mappers.getMapper(UserMapper.class);
        return userModels.stream()
            .map(userMapper::toUserProfileDto)
            .collect(Collectors.toSet());
    }

    @Named("userIdsToUserModels")
    default Set<UserModel> userIdsToUserModels(Set<Long> ids) {
        if (ids == null) return null;
        UserRepository repo = SpringContext.getBean(UserRepository.class); // helper to get Spring beans manually
        return ids.stream()
            .map(id -> repo.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found: " + id)))
            .collect(Collectors.toSet());
    }
}

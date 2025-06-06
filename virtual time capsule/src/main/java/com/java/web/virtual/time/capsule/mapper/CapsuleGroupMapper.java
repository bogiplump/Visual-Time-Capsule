package com.java.web.virtual.time.capsule.mapper;

import com.java.web.virtual.time.capsule.dtos.CapsuleInfoForGroupDto;
import com.java.web.virtual.time.capsule.dtos.CreateGroupDto;
import com.java.web.virtual.time.capsule.model.CapsuleEntity;
import com.java.web.virtual.time.capsule.model.CapsuleGroupEntity;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CapsuleGroupMapper {

    @Mapping(target = "capsules", source = "capsules")
    CapsuleGroupEntity toEntity(CreateGroupDto dto);

    @Mapping(target = "group", ignore = true)
    CapsuleEntity toEntity(CapsuleInfoForGroupDto dto);

    List<CapsuleEntity> toEntityList(List<CapsuleInfoForGroupDto> dtos);
}

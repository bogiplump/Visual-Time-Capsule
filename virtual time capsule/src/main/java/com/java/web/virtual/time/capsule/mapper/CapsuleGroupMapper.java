package com.java.web.virtual.time.capsule.mapper;

import com.java.web.virtual.time.capsule.dto.CapsuleInfoForGroupDto;
import com.java.web.virtual.time.capsule.dto.CreateGroupDto;
import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.CapsuleGroup;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CapsuleGroupMapper {

    @Mapping(target = "capsules", source = "capsules")
    CapsuleGroup toEntity(CreateGroupDto dto);

    @Mapping(target = "group", ignore = true)
    Capsule toEntity(CapsuleInfoForGroupDto dto);

    List<Capsule> toEntityList(List<CapsuleInfoForGroupDto> dtos);
}

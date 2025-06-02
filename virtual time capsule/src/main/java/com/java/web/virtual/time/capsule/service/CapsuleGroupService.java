package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dtos.CapsuleRequestDto;
import com.java.web.virtual.time.capsule.dtos.CreateGroupDto;
import com.java.web.virtual.time.capsule.dtos.GroupUpdateDto;
import com.java.web.virtual.time.capsule.model.CapsuleEntity;
import com.java.web.virtual.time.capsule.model.CapsuleGroupEntity;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface CapsuleGroupService {
    List<CapsuleGroupEntity> getAllCapsulesByUserId(Long userId);

    // TODO: implement DTO's
    void createCapsuleGroup(CreateGroupDto createGroupDto);

    CapsuleEntity getCapsule(CapsuleRequestDto capsuleRequestDto);  // TODO: implement dto

    void updateGroup(GroupUpdateDto groupUpdateDto);

    void deleteGroup(Long id);
}

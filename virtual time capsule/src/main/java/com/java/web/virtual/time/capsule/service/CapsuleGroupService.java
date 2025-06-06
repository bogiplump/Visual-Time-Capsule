package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dtos.CreateGroupDto;
import com.java.web.virtual.time.capsule.dtos.GroupUpdateDto;
import com.java.web.virtual.time.capsule.model.CapsuleEntity;
import com.java.web.virtual.time.capsule.model.CapsuleGroupEntity;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface CapsuleGroupService {
    List<CapsuleGroupEntity> getAllCapsuleGroupsByUserId(Long userId);

    // TODO: implement DTO's
    void createCapsuleGroup(CreateGroupDto createGroupDto);

    List<CapsuleEntity> getCapsulesInGroup(Long groupId);

    void updateGroup(GroupUpdateDto groupUpdateDto);

    void deleteGroup(Long id);
}

package com.java.web.virtual.time.capsule.service;

import com.java.web.virtual.time.capsule.dto.CreateGroupDto;
import com.java.web.virtual.time.capsule.dto.GroupUpdateDto;
import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.CapsuleGroup;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public interface CapsuleGroupService {
    List<CapsuleGroup> getAllCapsuleGroupsByUserId(Long userId);

    void createCapsuleGroup(CreateGroupDto createGroupDto, String user);

    List<Capsule> getCapsulesInGroup(Long groupId);

    void updateGroup(GroupUpdateDto groupUpdateDto);

    void deleteGroup(Long id);
}

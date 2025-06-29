package com.java.web.virtual.time.capsule.service.impl;

import com.java.web.virtual.time.capsule.dto.CreateGroupDto;
import com.java.web.virtual.time.capsule.dto.GroupUpdateDto;
import com.java.web.virtual.time.capsule.exception.GroupNotFoundException;
import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.CapsuleGroup;

import com.java.web.virtual.time.capsule.repository.CapsuleGroupRepository;

import com.java.web.virtual.time.capsule.repository.CapsuleRepository;
import com.java.web.virtual.time.capsule.repository.UserRepository;
import com.java.web.virtual.time.capsule.service.CapsuleGroupService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CapsuleGroupServiceImpl implements CapsuleGroupService {
    @Autowired
    private CapsuleGroupRepository capsuleGroupRepository;
    @Autowired
    private CapsuleRepository capsuleRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<CapsuleGroup> getAllCapsuleGroupsByUserId(Long userId) {
        return capsuleGroupRepository.findDistinctByCapsulesCreatorId(userId);
    }

    @Override
    public void createCapsuleGroup(CreateGroupDto createGroupDto, String user) {
        CapsuleGroup groupEntity = new CapsuleGroup();

        groupEntity.setName(createGroupDto.getName());
        groupEntity.setTheme(createGroupDto.getTheme());
        groupEntity.setOpenTime(createGroupDto.getOpenTime());
        groupEntity.setTimeBetweenCapsules(createGroupDto.getTimeBetweenCapsules());
        groupEntity.setCapsules(capsuleRepository.findAllByIdIn(createGroupDto.getCapsuleIds()));
        groupEntity.setTimeOfCreation(LocalDateTime.now());
        groupEntity.setCreator(userRepository.findByUsername(user));

        capsuleGroupRepository.save(groupEntity);
    }

    @Override
    public List<Capsule> getCapsulesInGroup(Long groupId) {
        CapsuleGroup group = capsuleGroupRepository.findById(groupId)
            .orElseThrow(() -> new GroupNotFoundException("Could not find capsule group by id."));
        return group.getCapsules() != null ? group.getCapsules() : new ArrayList<>();
    }

    @Override
    public void updateGroup(GroupUpdateDto groupUpdateDto) {
        CapsuleGroup group = capsuleGroupRepository.findById(groupUpdateDto.getId())
            .orElseThrow(() -> new GroupNotFoundException("Could not find capsule group by id."));

        if (groupUpdateDto.getName() != null) {
            group.setName(groupUpdateDto.getName());
        }
        if (groupUpdateDto.getTheme() != null) {
            group.setTheme(groupUpdateDto.getTheme());
        }
        if (groupUpdateDto.getOpenTime() != null) {
            group.setOpenTime(groupUpdateDto.getOpenTime());
        }
        if (groupUpdateDto.getTimeBetweenCapsules() != null) {
            group.setTimeBetweenCapsules(groupUpdateDto.getTimeBetweenCapsules());
        }
        if (groupUpdateDto.getCapsuleIds() != null) {
            group.setCapsules(capsuleRepository.findAllById(groupUpdateDto.getCapsuleIds()));
        }

        capsuleGroupRepository.save(group);
    }

    @Override
    public void deleteGroup(Long id) {
        if (!capsuleGroupRepository.existsById(id)) {
            throw new GroupNotFoundException("Could not find capsule group by id.");
        }

        capsuleGroupRepository.deleteById(id);
    }
}
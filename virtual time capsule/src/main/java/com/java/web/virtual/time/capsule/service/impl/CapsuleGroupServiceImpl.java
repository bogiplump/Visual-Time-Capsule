package com.java.web.virtual.time.capsule.service.impl;

import com.java.web.virtual.time.capsule.dto.CreateGroupDto;
import com.java.web.virtual.time.capsule.dto.GroupUpdateDto;
import com.java.web.virtual.time.capsule.exception.GroupNotFoundException;
import com.java.web.virtual.time.capsule.model.Capsule;
import com.java.web.virtual.time.capsule.model.CapsuleGroupEntity;

import com.java.web.virtual.time.capsule.repository.CapsuleGroupRepository;
import com.java.web.virtual.time.capsule.repository.CapsuleNodeRepository;

import com.java.web.virtual.time.capsule.service.CapsuleGroupService;
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
    private CapsuleNodeRepository capsuleNodeRepository;

    @Override
    public List<CapsuleGroupEntity> getAllCapsuleGroupsByUserId(Long userId) {
        return capsuleGroupRepository.findDistinctByCapsulesCreatorId(userId);
    }

    @Override
    public void createCapsuleGroup(CreateGroupDto createGroupDto) {
        CapsuleGroupEntity groupEntity = new CapsuleGroupEntity();

        groupEntity.setName(createGroupDto.getName());
        groupEntity.setTheme(createGroupDto.getTheme());
        groupEntity.setOpenTime(createGroupDto.getOpenTime());
        groupEntity.setTimeBetweenCapsules(createGroupDto.getTimeBetweenCapsules());

        capsuleGroupRepository.save(groupEntity);
    }

    @Override
    public List<Capsule> getCapsulesInGroup(Long groupId) {
        CapsuleGroupEntity group = capsuleGroupRepository.findById(groupId)
            .orElseThrow(() -> new GroupNotFoundException("Could not find capsule group by id."));
        return group.getCapsules() != null ? group.getCapsules() : new ArrayList<>();
    }

    @Override
    public void updateGroup(GroupUpdateDto groupUpdateDto) {
        CapsuleGroupEntity group = capsuleGroupRepository.findById(groupUpdateDto.getId())
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
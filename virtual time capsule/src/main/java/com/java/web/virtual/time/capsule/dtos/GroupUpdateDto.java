package com.java.web.virtual.time.capsule.dtos;

import com.java.web.virtual.time.capsule.model.CapsuleEntity;

import java.time.LocalDateTime;

import java.util.List;

import lombok.Data;

@Data
public class GroupUpdateDto {
    private Long id;
    private String name;
    private String theme;
    private List<CapsuleEntity> capsules;
    private LocalDateTime timeBetweenCapsules;
    private LocalDateTime openTime;
}

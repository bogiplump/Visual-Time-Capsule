package com.java.web.virtual.time.capsule.dto;

import java.time.LocalDateTime;

import java.util.List;

import lombok.Data;

@Data
public class CreateGroupDto {
    private String name;
    private String theme;
    private LocalDateTime timeBetweenCapsules;
    private LocalDateTime openTime;
    private List<Long> capsuleIds;
}
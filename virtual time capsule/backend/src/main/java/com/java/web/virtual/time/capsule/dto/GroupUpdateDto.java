package com.java.web.virtual.time.capsule.dto;

import com.java.web.virtual.time.capsule.model.Capsule;

import java.time.LocalDateTime;

import java.util.List;

import lombok.Data;

@Data
public class GroupUpdateDto {
    private Long id;
    private String name;
    private String theme;
    private List<Capsule> capsules;
    private LocalDateTime timeBetweenCapsules;
    private LocalDateTime openTime;
}
package com.java.web.virtual.time.capsule.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CapsuleInfoForGroupDto {
    private String title;
    private String content;
    private String status;
    private LocalDateTime openDate;
}

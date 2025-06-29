package com.java.web.virtual.time.capsule.dto;

import com.java.web.virtual.time.capsule.enums.MemoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemoryDto {
    private Long id;
    private String description;
    private MemoryType memoryType;
    private String path; 
    private String creationDate; 
    private Long creatorId; 
    private Long capsuleId; 
}
